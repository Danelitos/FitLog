package com.example.proyecto_das;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;


import androidx.core.app.NotificationCompat;

public class NotificacionActivity {

    private static final String CANAL_ID = "CanalBienvenida";
    private static final CharSequence NOMBRE_CANAL = "Canal de Bienvenida";
    private static final String DESCRIPCION_CANAL = "Canal de notificación para mensajes de bienvenida";

    public static void enviarNotificacionBienvenida(Context context, String nombreUsuario) {
        // Crear el canal de notificación
        crearCanalNotificacion(context);

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.fitlog)
                .setContentTitle(context.getString(R.string.ntf_bienvenido))
                .setContentText(context.getString(R.string.ntf_hola) + nombreUsuario + context.getString(R.string.ntf_bienApp))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private static void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(CANAL_ID, NOMBRE_CANAL, importancia);
            canal.setDescription(DESCRIPCION_CANAL);
            canal.enableLights(true);
            canal.setLightColor(Color.RED);
            canal.enableVibration(true);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
        }
    }
}

