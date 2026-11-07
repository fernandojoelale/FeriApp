package edu.uadeapps.feriapp

import android.app.Application
import edu.uadeapps.feriapp.di.AppContainer
import edu.uadeapps.feriapp.di.AppDataContainer
import edu.uadeapps.feriapp.notificaciones.Notificaciones

class FeriApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        Notificaciones.crearCanales(this)
    }
}
