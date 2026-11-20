package com.example.siemprejuntos;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;

/** Pantalla que se abre al tocar la notificación de un aviso (o de su aviso previo). */
public class AlarmaActivity extends BaseActivity {

    @Override
    protected boolean conBarraNavegacion() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);

        final Alarma a = Alarmas.buscar(this, getIntent().getIntExtra(Alarmas.EXTRA_ID, -1));
        if (a == null) {
            finish();
            return;
        }
        final boolean previo = getIntent().getBooleanExtra(Alarmas.EXTRA_PREVIO, false);
        ((TextView) findViewById(R.id.tvAlarmaIcono)).setText(a.icono());
        ((TextView) findViewById(R.id.tvAlarmaTitulo)).setText(previo ? "Pronto: " + a.titulo() : a.titulo());
        ((TextView) findViewById(R.id.tvAlarmaTexto)).setText(Alarmas.textoCompleto(a, previo)
                .substring(a.icono().length() + 1));

        findViewById(R.id.btnAlarmaListo).setOnClickListener(v -> {
            NotificationManagerCompat.from(this).cancel(a.id);
            finish();
        });
        View posponer = findViewById(R.id.btnAlarmaPosponer);
        if (previo) {
            posponer.setVisibility(View.GONE);   // el aviso previo solo se cierra; la hora principal sonará igual
        } else {
            posponer.setOnClickListener(v -> {
                NotificationManagerCompat.from(this).cancel(a.id);
                Alarmas.posponer(this, a);
                finish();
            });
        }
    }
}
