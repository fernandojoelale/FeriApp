package edu.uadeapps.feriapp.data.remote

import edu.uadeapps.feriapp.data.remote.dto.CrearFeriaDto
import edu.uadeapps.feriapp.data.remote.dto.FeriaDetalleDto
import edu.uadeapps.feriapp.data.remote.dto.ReservaDto
import edu.uadeapps.feriapp.data.remote.dto.ReservaRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class FeriaDto(
    val id: Long = 0,
    val nombre: String = "",
    val fecha: String = "",
    val zona: String = "",
    val rubro: String = "",
    val precioPuesto: Double = 0.0,
    val puestosLibres: Int = 0,
    val horaInicio: String = "",
    val horaFin: String = "",
    val direccion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val organizadorNombre: String = ""
)

// Interfaz de Retrofit para consumir la API
interface FeriAppApi {
    @GET("api/ferias")
    suspend fun getFerias(): List<FeriaDto>

    @GET("api/ferias/mias")
    suspend fun getMisFerias(): List<FeriaDto>

    @GET("api/ferias/{id}")
    suspend fun getFeriaDetalle(@Path("id") id: Long): FeriaDetalleDto

    @POST("api/ferias")
    suspend fun crearFeria(@Body body: CrearFeriaDto): FeriaDto

    @POST("api/reservas")
    suspend fun reservar(@Body body: ReservaRequestDto): ReservaDto

    @GET("api/reservas/mias")
    suspend fun getMisReservas(): List<ReservaDto>

    @GET("api/reservas/{id}")
    suspend fun getReserva(@Path("id") id: Long): ReservaDto
}
