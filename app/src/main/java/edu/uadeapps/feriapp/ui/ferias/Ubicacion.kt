package edu.uadeapps.feriapp.ui.ferias

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

fun tienePermisoUbicacion(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

/**
 * Ubicación aproximada del dispositivo (latitud, longitud) o null si no se pudo obtener.
 * Solo se usa en el celular para ordenar por cercanía: nunca se guarda ni se envía al servidor.
 */
suspend fun obtenerUbicacion(context: Context): Pair<Double, Double>? {
    if (!tienePermisoUbicacion(context)) return null
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
    val proveedores = try {
        manager.getProviders(true)
    } catch (e: SecurityException) {
        return null
    }

    // 1) Última ubicación conocida (instantánea).
    var mejor: Location? = null
    for (p in proveedores) {
        val loc = try {
            manager.getLastKnownLocation(p)
        } catch (e: SecurityException) {
            null
        }
        if (loc != null && (mejor == null || loc.time > mejor.time)) mejor = loc
    }
    if (mejor != null) return mejor.latitude to mejor.longitude

    // 2) Pedir una ubicación nueva (Android 11 en adelante), con tiempo máximo de espera.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        for (p in proveedores) {
            val loc = withTimeoutOrNull(8_000) {
                suspendCancellableCoroutine<Location?> { cont ->
                    try {
                        manager.getCurrentLocation(p, null, ContextCompat.getMainExecutor(context)) { cont.resume(it) }
                    } catch (e: SecurityException) {
                        cont.resume(null)
                    }
                }
            }
            if (loc != null) return loc.latitude to loc.longitude
        }
    }
    return null
}
