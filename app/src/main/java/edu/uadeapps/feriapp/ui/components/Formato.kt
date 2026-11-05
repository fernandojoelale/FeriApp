package edu.uadeapps.feriapp.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val localeAr = Locale("es", "AR")

/** 18000.0 -> "$ 18.000" */
fun formatearPrecio(precio: Double): String =
    "$ " + NumberFormat.getIntegerInstance(localeAr).format(precio)

/** "2026-11-14" -> "sáb 14 nov". Si no se puede interpretar, devuelve el texto original. */
fun formatearFecha(fechaIso: String): String =
    try {
        LocalDate.parse(fechaIso).format(DateTimeFormatter.ofPattern("EEE d MMM", localeAr))
    } catch (e: Exception) {
        fechaIso
    }

fun formatearHorario(inicio: String, fin: String): String =
    if (inicio.isBlank() || fin.isBlank()) "" else "$inicio–$fin h"

/** "sáb 14 nov · 10:00–18:00 h" */
fun formatearFechaHorario(fechaIso: String, inicio: String, fin: String): String {
    val horario = formatearHorario(inicio, fin)
    return if (horario.isEmpty()) formatearFecha(fechaIso) else "${formatearFecha(fechaIso)} · $horario"
}

/** Abre la app de mapas del dispositivo (intent). Si no hay ninguna, abre el navegador. */
fun abrirMapa(context: Context, latitud: Double, longitud: Double, etiqueta: String) {
    val geo = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud(${Uri.encode(etiqueta)})")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, geo))
    } catch (e: ActivityNotFoundException) {
        try {
            val web = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitud,$longitud")
            context.startActivity(Intent(Intent.ACTION_VIEW, web))
        } catch (e2: ActivityNotFoundException) {
            Toast.makeText(context, "No encontramos una app de mapas en este dispositivo.", Toast.LENGTH_LONG).show()
        }
    }
}

fun esFechaPasada(fechaIso: String): Boolean =
    try {
        LocalDate.parse(fechaIso).isBefore(LocalDate.now())
    } catch (e: Exception) {
        false
    }
