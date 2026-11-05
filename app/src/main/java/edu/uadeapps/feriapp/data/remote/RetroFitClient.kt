package edu.uadeapps.feriapp.data.remote

import edu.uadeapps.feriapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Crea el cliente HTTP de la app. La URL del backend se configura en local.properties
 * (api.baseUrl). Ver app/build.gradle.kts.
 */
class RetrofitClient(tokenProvider: suspend () -> String?) {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenProvider))
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val feriaApi: FeriAppApi = retrofit.create(FeriAppApi::class.java)
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}
