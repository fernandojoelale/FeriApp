package edu.uadeapps.feriapp.di

import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.RetrofitClient
import edu.uadeapps.feriapp.data.repository.FeriaRepository

interface AppContainer {
    val feriaRepository: FeriaRepository
}

class AppDataContainer : AppContainer {
    private val api: FeriAppApi = RetrofitClient.feriaApi

    override val feriaRepository: FeriaRepository by lazy {
        FeriaRepository(api)
    }
}
