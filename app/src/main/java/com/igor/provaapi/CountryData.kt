package com.igor.provaapi

data class CountryData(
    val commonName: String,
    val officialName: String,
    val capital: String,
    val population: Long,
    val region: String,
    val subregion: String,
    val currencies: String,
    val languages: String,
    val flagUrl: String
)
