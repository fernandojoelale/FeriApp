package edu.uadeapps.feriapp

import android.app.Application
import edu.uadeapps.feriapp.di.AppContainer
import edu.uadeapps.feriapp.di.AppDataContainer

class FeriApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer()
    }
}
