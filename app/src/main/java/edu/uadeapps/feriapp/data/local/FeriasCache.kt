package edu.uadeapps.feriapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.uadeapps.feriapp.data.remote.FeriaDto
import kotlinx.coroutines.flow.first

private val Context.feriasDataStore: DataStore<Preferences> by preferencesDataStore(name = "ferias_cache")

/**
 * Guarda la última lista de ferias recibida del servidor (solo lectura, RNF06):
 * sin internet se muestra esa lista; las reservas siempre requieren conexión.
 */
class FeriasCache(private val context: Context) {

    private val gson = Gson()
    private val clave = stringPreferencesKey("ferias_json")

    suspend fun guardar(ferias: List<FeriaDto>) {
        context.feriasDataStore.edit { it[clave] = gson.toJson(ferias) }
    }

    /** Devuelve la lista guardada o null si todavía no hay ninguna (o está dañada). */
    suspend fun leer(): List<FeriaDto>? {
        val json = context.feriasDataStore.data.first()[clave] ?: return null
        return try {
            val tipo = object : TypeToken<List<FeriaDto>>() {}.type
            gson.fromJson<List<FeriaDto>>(json, tipo)
        } catch (e: Exception) {
            null
        }
    }
}
