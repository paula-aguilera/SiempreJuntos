package com.example.siemprejuntos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Palabras de ánimo: pensamiento del día y lista de pasajes bíblicos para leer. */
public class FeActivity extends BaseActivity {

    private LinearLayout lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fe);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvAvisoFe)).setText("ℹ️  " + Contenido.AVISO_FE);

        final Contenido.Pasaje hoy = Contenido.pasajeDelDia();
        ((TextView) findViewById(R.id.tvRefHoy)).setText(hoy.referencia);
        ((TextView) findViewById(R.id.tvPensamientoHoy)).setText(hoy.pensamiento);
        findViewById(R.id.btnLeerHoy).setOnClickListener(v -> Util.abrirEnApp(this, hoy.url()));
        findViewById(R.id.btnBiblia).setOnClickListener(v -> Util.abrirEnApp(this, Contenido.URL_BIBLIA));

        lista = findViewById(R.id.llPasajes);
        findViewById(R.id.btnFiltroTodos).setOnClickListener(v -> mostrarLista(""));
        findViewById(R.id.btnFiltroSalmos).setOnClickListener(v -> mostrarLista("Salmo"));
        findViewById(R.id.btnFiltroProverbios).setOnClickListener(v -> mostrarLista("Proverbios"));
        mostrarLista("");
    }

    /** Muestra los pasajes que empiezan con el texto dado (vacío = todos). */
    private void mostrarLista(String comienzo) {
        lista.removeAllViews();
        int n = 0;
        for (int i = 0; i < Contenido.PASAJES.length; i++) {
            final int indice = i;
            Contenido.Pasaje p = Contenido.PASAJES[i];
            if (!p.referencia.startsWith(comienzo)) {
                continue;
            }
            Button b = (Button) getLayoutInflater().inflate(R.layout.item_opcion, lista, false);
            b.setText("📖  " + p.referencia);
            b.setBackgroundResource(n % 2 == 0 ? R.drawable.bg_btn_azul : R.drawable.bg_btn_verde);
            b.setOnClickListener(v -> {
                Intent intent = new Intent(this, PasajeActivity.class);
                intent.putExtra(PasajeActivity.EXTRA_INDICE, indice);
                startActivity(intent);
            });
            lista.addView(b);
            n++;
        }
    }
}
