package com.example.siemprejuntos;

import android.os.Bundle;

/** Números de emergencia de Chile, cada uno abre el marcador. */
public class EmergenciaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);

        findViewById(R.id.btn131).setOnClickListener(v -> Util.marcar(this, "131"));
        findViewById(R.id.btn132).setOnClickListener(v -> Util.marcar(this, "132"));
        findViewById(R.id.btn133).setOnClickListener(v -> Util.marcar(this, "133"));
        findViewById(R.id.btnSaludResponde).setOnClickListener(v -> Util.marcar(this, "6003607777"));
        findViewById(R.id.btn4141).setOnClickListener(v -> Util.marcar(this, "*4141"));
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}
