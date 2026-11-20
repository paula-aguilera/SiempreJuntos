package com.example.siemprejuntos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Se ejecuta cuando llega la hora de un aviso (o su aviso previo): muestra la notificación y programa el siguiente. */
public class AlarmaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(Alarmas.EXTRA_ID, -1);
        boolean repeticion = intent.getBooleanExtra(Alarmas.EXTRA_REPETICION, false);
        boolean previo = intent.getBooleanExtra(Alarmas.EXTRA_PREVIO, false);
        Alarma a = Alarmas.buscar(context, id);
        if (a == null) {
            return;
        }
        if (previo) {
            Alarmas.mostrarAviso(context, a, true);
            if (a.esDiaria()) {
                Alarmas.programarPrevio(context, a, 60 * 1000L);
            }
            return;
        }
        Alarmas.mostrarAviso(context, a, false);
        if (!repeticion && a.esDiaria()) {
            Alarmas.programar(context, a);
        }
    }
}
