package edu.uadeapps.feriapp.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import edu.uadeapps.feriapp.MainActivity

object Notificaciones {
    const val CANAL_RESERVAS = "reservas"
    const val EXTRA_FERIA_ID = "feriaId"

    /** El canal se crea una sola vez al iniciar la app (Android 8+, que es nuestro mínimo). */
    fun crearCanales(context: Context) {
        val canal = NotificationChannel(
            CANAL_RESERVAS,
            "Reservas de puestos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Avisos cuando un feriante reserva un puesto de tus ferias" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    /** Muestra el aviso de reserva; al tocarlo se abre el detalle de la feria (RF04). */
    fun mostrarReserva(context: Context, titulo: String, cuerpo: String, feriaId: Long) {
        // Android 13+: sin el permiso concedido no se puede mostrar (el perfil avisa al organizador).
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_FERIA_ID, feriaId)
        }
        val pendiente = PendingIntent.getActivity(
            context,
            feriaId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificacion = NotificationCompat.Builder(context, CANAL_RESERVAS)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendiente)
            .build()
        try {
            context.getSystemService(NotificationManager::class.java)
                .notify(System.currentTimeMillis().toInt(), notificacion)
        } catch (e: SecurityException) {
            // El permiso se revocó justo ahora: no hay nada que mostrar.
        }
    }
}
