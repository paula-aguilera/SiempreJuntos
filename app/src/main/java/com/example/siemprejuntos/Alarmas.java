package com.example.siemprejuntos;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.List;

/**
 * Programa los avisos con el reloj del celular y muestra la notificación cuando llega la hora.
 * Se usa setAlarmClock porque suena a la hora exacta incluso con el celular en reposo y no
 * necesita permisos especiales.
 */
public final class Alarmas {

    public static final String EXTRA_ID = "alarma_id";
    public static final String EXTRA_REPETICION = "es_repeticion";
    public static final String EXTRA_PREVIO = "es_previo";
    private static final String CANAL = "recordatorios";
    private static final int DESPLAZAMIENTO_POSPONER = 100000;
    private static final int DESPLAZAMIENTO_ANTES = 200000;

    private Alarmas() {
    }

    // ------------------------------------------------------------ programar

    /** Próximo momento en que debe sonar, o -1 si ya pasó (cita de una sola vez). */
    public static long proxima(Alarma a, long ahora) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ahora);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (!a.esDiaria()) {
            c.set(a.anio, a.mes, a.dia, a.hora, a.minuto, 0);
            c.set(Calendar.MILLISECOND, 0);
            return c.getTimeInMillis() > ahora ? c.getTimeInMillis() : -1;
        }
        c.set(Calendar.HOUR_OF_DAY, a.hora);
        c.set(Calendar.MINUTE, a.minuto);
        if (c.getTimeInMillis() <= ahora) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        return c.getTimeInMillis();
    }

    public static void programar(Context ctx, Alarma a) {
        long momento = proxima(a, System.currentTimeMillis());
        if (momento < 0) {
            return;
        }
        programarEn(ctx, a.id, momento, a.id, false, false);
        programarPrevio(ctx, a, 0);
    }

    /**
     * Programa el aviso previo ("dentro de 10 minutos"), si la alarma lo tiene.
     * "margenMs" evita que el mismo aviso previo se repita justo después de sonar.
     */
    public static void programarPrevio(Context ctx, Alarma a, long margenMs) {
        if (a.antes <= 0) {
            return;
        }
        long antesMs = a.antes * 60 * 1000L;
        long principal = proxima(a, System.currentTimeMillis() + antesMs + margenMs);
        if (principal < 0) {
            return;
        }
        programarEn(ctx, a.id, principal - antesMs, a.id + DESPLAZAMIENTO_ANTES, false, true);
    }

    /** Vuelve a sonar en 10 minutos (por ejemplo si la persona toca "Avisarme después"). */
    public static void posponer(Context ctx, Alarma a) {
        programarEn(ctx, a.id, System.currentTimeMillis() + 10 * 60 * 1000L, a.id + DESPLAZAMIENTO_POSPONER, true, false);
    }

    private static PendingIntent intentDeAlarma(Context ctx, int id, int codigo, boolean repeticion, boolean previo) {
        Intent i = new Intent(ctx, AlarmaReceiver.class);
        i.putExtra(EXTRA_ID, id);
        i.putExtra(EXTRA_REPETICION, repeticion);
        i.putExtra(EXTRA_PREVIO, previo);
        return PendingIntent.getBroadcast(ctx, codigo, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void programarEn(Context ctx, int id, long momento, int codigo, boolean repeticion, boolean previo) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            return;
        }
        PendingIntent mostrar = PendingIntent.getActivity(ctx, 0, new Intent(ctx, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        try {
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(momento, mostrar),
                    intentDeAlarma(ctx, id, codigo, repeticion, previo));
        } catch (SecurityException e) {
            // si el sistema no permite programar, simplemente no se programa
        }
    }

    public static void cancelar(Context ctx, Alarma a) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            return;
        }
        am.cancel(intentDeAlarma(ctx, a.id, a.id, false, false));
        am.cancel(intentDeAlarma(ctx, a.id, a.id + DESPLAZAMIENTO_POSPONER, true, false));
        am.cancel(intentDeAlarma(ctx, a.id, a.id + DESPLAZAMIENTO_ANTES, false, true));
        NotificationManagerCompat.from(ctx).cancel(a.id);
        NotificationManagerCompat.from(ctx).cancel(a.id + DESPLAZAMIENTO_ANTES);
    }

    /** Se usa al encender el celular, actualizar la app o cambiar la hora. */
    public static void reprogramarTodas(Context ctx) {
        List<Alarma> lista = new Prefs(ctx).getAlarmas();
        for (Alarma a : lista) {
            programar(ctx, a);
        }
    }

    public static Alarma buscar(Context ctx, int id) {
        for (Alarma a : new Prefs(ctx).getAlarmas()) {
            if (a.id == id) {
                return a;
            }
        }
        return null;
    }

    // --------------------------------------------------------- notificación

    /** Texto completo de un aviso: lo que hay que hacer, la descripción y, si es agua, el consejo de los 2 litros. */
    public static String textoCompleto(Alarma a, boolean previo) {
        String texto = a.icono() + " " + a.texto + (previo
                ? " — dentro de " + Alarma.antesTexto(a.antes) + " (" + a.horaTexto() + ")"
                : " (" + a.horaTexto() + ")");
        if (!a.descripcion.isEmpty()) {
            texto += "\n" + a.descripcion;
        }
        if (Alarma.AGUA.equals(a.tipo)) {
            texto += "\n💧 " + Alarma.AVISO_AGUA;
        }
        return texto;
    }

    public static void mostrarAviso(Context ctx, Alarma a, boolean previo) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        crearCanal(ctx);

        Intent abrir = new Intent(ctx, AlarmaActivity.class);
        abrir.putExtra(EXTRA_ID, a.id);
        abrir.putExtra(EXTRA_PREVIO, previo);
        abrir.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int idNotificacion = previo ? a.id + DESPLAZAMIENTO_ANTES : a.id;
        PendingIntent contenido = PendingIntent.getActivity(ctx, idNotificacion, abrir,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String texto = textoCompleto(a, previo);
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CANAL)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(previo ? "Pronto: " + a.titulo() : a.titulo())
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contenido);
        try {
            NotificationManagerCompat.from(ctx).notify(idNotificacion, b.build());
        } catch (SecurityException e) {
            // sin permiso de notificaciones: no se muestra
        }
    }

    private static void crearCanal(Context ctx) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null && nm.getNotificationChannel(CANAL) == null) {
                NotificationChannel canal = new NotificationChannel(CANAL, "Recordatorios",
                        NotificationManager.IMPORTANCE_HIGH);
                canal.setDescription("Avisos de remedios, agua y citas médicas");
                canal.enableVibration(true);
                nm.createNotificationChannel(canal);
            }
        }
    }
}
