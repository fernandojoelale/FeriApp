package edu.uadeapps.feriapp.data.remote

import retrofit2.http.GET

data class FeriaDto(
    val id: Long = 0,
    val nombre: String = "",
    val fecha: String = "",
    val zona: String = "",
    val rubro: String = "",
    val precioPuesto: Double = 0.0,
    val puestosLibres: Int = 0
)

// Interfaz de Retrofit para consumir la API
interface FeriAppApi {
    @GET("api/ferias")
    suspend fun getFerias(): List<FeriaDto>
}