package com.example.siemprejuntos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

/** Lista de tutoriales paso a paso. */
public class AprenderActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprender);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        LinearLayout lista = findViewById(R.id.llLecciones);
        for (int i = 0; i < Contenido.LECCIONES.length; i++) {
            final int indice = i;
            Contenido.Leccion l = Contenido.LECCIONES[i];
            Button b = (Button) getLayoutInflater().inflate(R.layout.item_opcion, lista, false);
            b.setText(l.icono + "  " + l.titulo);
            b.setBackgroundResource(i % 2 == 0 ? R.drawable.bg_btn_verde : R.drawable.bg_btn_azul);
            b.setOnClickListener(v -> {
                Intent intent = new Intent(this, LeccionActivity.class);
                intent.putExtra(LeccionActivity.EXTRA_INDICE, indice);
                startActivity(intent);
            });
            lista.addView(b);
        }
    }
}
