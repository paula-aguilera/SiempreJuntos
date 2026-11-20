package com.example.siemprejuntos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Vuelve a programar los avisos cuando el celular se enciende, se actualiza la app o cambia la hora. */
public class ArranqueReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarmas.reprogramarTodas(context);
    }
}
