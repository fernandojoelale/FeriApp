package edu.uadeapps.feriapp.data.remote

import edu.uadeapps.feriapp.data.remote.dto.AuthResponseDto
import edu.uadeapps.feriapp.data.remote.dto.LoginRequestDto
import edu.uadeapps.feriapp.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto
}
