# RESTCountries API Client – Android App

Este projeto consiste em desenvolver um **aplicativo de consulta de dados de países** em **Android Studio**, com foco em aprimorar o uso de **layouts XML**, personalização de interface e implementação da **lógica de consumo de API** utilizando a biblioteca nativa de Java (`HttpURLConnection`) e **Coroutines** em Kotlin para operações de rede, buscando informações da API pública [RESTCountries](https://restcountries.com/).

## 📱 Features

- **Consulta de Dados de Países**
  - Permite ao usuário buscar um país pelo nome.
  - Exibe detalhes do país, como Nome Comum e Oficial, Capital, População, Região/Sub-região, Moedas e Idiomas.
  - Carrega e exibe a bandeira do país utilizando a biblioteca Glide.

- **Consumo de API Assíncrono**
  - Utiliza a API pública **RESTCountries (v3.1)** (`https://restcountries.com/v3.1/name/`) para obter os dados.
  - Implementa chamadas de rede de forma assíncrona usando **Kotlin Coroutines** (Dispatchers.IO).
  - Inclui tratamento de erros para notificar o usuário quando o país não for encontrado ou houver falha de conexão.

- **UI**
  - Layout com design escuro (Dark Theme) desenvolvido em **XML**.
  - Campo de texto para consulta e botões de ação bem organizados.

<img src="https://github.com/user-attachments/assets/1080cd17-4138-425c-b1fc-a74724a99e1f" alt="UI de Busca de País (Dark Theme)" width="300"/>  
<img src="https://github.com/user-attachments/assets/8b0b6bdd-39ff-4ff5-8c3d-462064f48fbf" alt="UI de Resultados com Informações e Bandeira do País (Dark Theme)" width="300"/> 

## 🛠️ Technologies Used

- **Language:** Kotlin
- **Layout:** XML
- **IDE:** Android Studio
- **API/Libraries:** RESTCountries API, Glide, Kotlin Coroutines
