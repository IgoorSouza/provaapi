package com.igor.provaapi

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://restcountries.com/v3.1/name/"

    private lateinit var editTextCountryQuery: EditText
    private lateinit var buttonSearch: Button
    private lateinit var resultsLayout: LinearLayout
    private lateinit var textViewCommonName: TextView
    private lateinit var textViewOfficialName: TextView
    private lateinit var textViewCapital: TextView
    private lateinit var textViewPopulation: TextView
    private lateinit var textViewRegion: TextView
    private lateinit var textViewCurrencies: TextView
    private lateinit var textViewLanguages: TextView
    private lateinit var textViewError: TextView
    private lateinit var imageViewFlag: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextCountryQuery = findViewById(R.id.editTextCountryQuery)
        buttonSearch = findViewById(R.id.buttonSearch)
        resultsLayout = findViewById(R.id.resultsLayout)
        textViewCommonName = findViewById(R.id.textViewCommonName)
        textViewOfficialName = findViewById(R.id.textViewOfficialName)
        textViewCapital = findViewById(R.id.textViewCapital)
        textViewPopulation = findViewById(R.id.textViewPopulation)
        textViewRegion = findViewById(R.id.textViewRegion)
        textViewCurrencies = findViewById(R.id.textViewCurrencies)
        textViewLanguages = findViewById(R.id.textViewLanguages)
        textViewError = findViewById(R.id.textViewError)
        imageViewFlag = findViewById(R.id.imageViewFlag)

        buttonSearch.setOnClickListener {
            val query = editTextCountryQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchCountryData(query)
            } else {
                Toast.makeText(this, "Por favor, digite o nome de um país.", Toast.LENGTH_SHORT).show()
            }
        }

        resultsLayout.visibility = View.GONE
        textViewError.visibility = View.GONE
    }

    private fun fetchCountryData(countryName: String) {
        lifecycleScope.launch {
            try {
                val countryData = withContext(Dispatchers.IO) {
                    performApiCall(countryName)
                }

                if (countryData != null) {
                    displayCountryData(countryData)
                } else {
                    displayError("País não encontrado. Verifique o nome.")
                }
            } catch (e: Exception) {
                displayError("Erro de conexão. Verifique sua rede.")
            }
        }
    }

    private fun performApiCall(countryName: String): CountryData? {
        val encodedName = java.net.URLEncoder.encode(countryName, "UTF-8")
        val urlString = "$BASE_URL$encodedName"
        val url = URL(urlString)
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        try {
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                val errorStream = connection.errorStream
                if (errorStream != null) {
                    reader = BufferedReader(InputStreamReader(errorStream))
                    reader.readText()
                }
                return null
            }

            val inputStream = connection.inputStream
            reader = BufferedReader(InputStreamReader(inputStream))
            val jsonResponse = reader.readText()

            return parseJson(jsonResponse)

        } catch (e: Exception) {
            return null
        } finally {
            reader?.close()
            connection?.disconnect()
        }
    }

    private fun parseJson(jsonString: String): CountryData? {
        try {
            val jsonArray = JSONArray(jsonString)
            if (jsonArray.length() == 0) return null

            val countryObject = jsonArray.getJSONObject(0)

            val nameObject = countryObject.getJSONObject("name")
            val commonName = nameObject.getString("common")
            val officialName = nameObject.getString("official")
            val population = countryObject.getLong("population")
            val region = countryObject.getString("region")
            val subregion = countryObject.optString("subregion", "N/A")

            val capitalArray = countryObject.optJSONArray("capital")
            val capital = if (capitalArray != null && capitalArray.length() > 0) {
                capitalArray.getString(0)
            } else {
                "N/A"
            }

            val currenciesObject = countryObject.optJSONObject("currencies")
            val currenciesString = if (currenciesObject != null) {
                currenciesObject.keys().asSequence().joinToString(", ") { code ->
                    val currency = currenciesObject.getJSONObject(code)
                    "${code} (${currency.getString("name")})"
                }
            } else {
                "N/A"
            }

            val languagesObject = countryObject.optJSONObject("languages")
            val languagesString = if (languagesObject != null) {
                languagesObject.keys().asSequence().joinToString(", ") { key ->
                    languagesObject.getString(key)
                }
            } else {
                "N/A"
            }

            val flagsObject = countryObject.getJSONObject("flags")
            val flagUrl = flagsObject.getString("png")

            return CountryData(
                commonName, officialName, capital, population,
                region, subregion, currenciesString, languagesString, flagUrl
            )

        } catch (e: Exception) {
            return null
        }
    }

    private fun displayCountryData(data: CountryData) {
        val formatter = NumberFormat.getInstance(Locale.getDefault())

        textViewCommonName.text = data.commonName
        textViewOfficialName.text = "Nome Oficial: ${data.officialName}"
        textViewCapital.text = "Capital: ${data.capital}"
        textViewPopulation.text = "População: ${formatter.format(data.population)}"
        textViewRegion.text = "Região / Sub-região: ${data.region} / ${data.subregion}"
        textViewCurrencies.text = "Moedas: ${data.currencies}"
        textViewLanguages.text = "Idiomas: ${data.languages}"

        Glide.with(this)
            .load(data.flagUrl)
            .into(imageViewFlag)

        resultsLayout.visibility = View.VISIBLE
        textViewError.visibility = View.GONE
    }

    private fun displayError(message: String) {
        resultsLayout.visibility = View.GONE
        textViewError.text = message
        textViewError.visibility = View.VISIBLE
    }
}
