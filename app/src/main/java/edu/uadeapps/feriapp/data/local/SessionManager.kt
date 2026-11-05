package edu.uadeapps.feriapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import edu.uadeapps.feriapp.domain.model.Rol
import edu.uadeapps.feriapp.domain.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/** Guarda la sesión (token, rol, nombre) en DataStore para que sobreviva al cerrar la app. */
class SessionManager(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val ROL = stringPreferencesKey("rol")
        val NOMBRE = stringPreferencesKey("nombre")
        val EMAIL = stringPreferencesKey("email")
        val USUARIO_ID = longPreferencesKey("usuario_id")
    }

    val session: Flow<Session?> = context.sessionDataStore.data.map { prefs ->
        val token = prefs[Keys.TOKEN]
        if (token.isNullOrBlank()) {
            null
        } else {
            Session(
                token = token,
                rol = runCatching { Rol.valueOf(prefs[Keys.ROL] ?: "") }.getOrDefault(Rol.FERIANTE),
                nombre = prefs[Keys.NOMBRE] ?: "",
                email = prefs[Keys.EMAIL] ?: "",
                usuarioId = prefs[Keys.USUARIO_ID] ?: 0L
            )
        }
    }

    suspend fun guardar(session: Session) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = session.token
            prefs[Keys.ROL] = session.rol.name
            prefs[Keys.NOMBRE] = session.nombre
            prefs[Keys.EMAIL] = session.email
            prefs[Keys.USUARIO_ID] = session.usuarioId
        }
    }

    suspend fun cerrar() {
        context.sessionDataStore.edit { it.clear() }
    }
}
