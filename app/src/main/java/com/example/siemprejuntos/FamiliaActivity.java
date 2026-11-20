package com.example.siemprejuntos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/** Lista de familiares con botones para llamar o abrir WhatsApp. */
public class FamiliaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familia);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        LinearLayout contenedor = findViewById(R.id.llContactos);
        TextView vacio = findViewById(R.id.tvVacio);
        List<Contacto> lista = new Prefs(this).getContactos();
        vacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        LayoutInflater inflador = LayoutInflater.from(this);
        for (final Contacto c : lista) {
            View tarjeta = inflador.inflate(R.layout.item_contacto, contenedor, false);
            ((TextView) tarjeta.findViewById(R.id.tvNombre)).setText(c.nombre);
            ((TextView) tarjeta.findViewById(R.id.tvRelacion)).setText(c.relacion);
            tarjeta.findViewById(R.id.btnLlamar).setOnClickListener(v -> Util.marcar(this, c.telefono));
            tarjeta.findViewById(R.id.btnWhatsApp).setOnClickListener(v -> Util.abrirWhatsApp(this, c.telefono, ""));
            contenedor.addView(tarjeta);
        }
    }
}
