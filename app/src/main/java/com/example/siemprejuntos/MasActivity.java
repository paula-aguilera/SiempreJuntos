package com.example.siemprejuntos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/** "Más cosas": las opciones que no hacen falta todos los días, para no llenar el inicio. */
public class MasActivity extends BaseActivity {

    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mas);
        prefs = new Prefs(this);

        findViewById(R.id.btnMasAprender).setOnClickListener(v -> startActivity(new Intent(this, AprenderActivity.class)));
        findViewById(R.id.btnMasBienestar).setOnClickListener(v -> startActivity(new Intent(this, BienestarActivity.class)));
        findViewById(R.id.btnMasFe).setOnClickListener(v -> startActivity(new Intent(this, FeActivity.class)));
        findViewById(R.id.btnMasEstoyBien).setOnClickListener(v -> startActivity(new Intent(this, EstoyBienActivity.class)));
        findViewById(R.id.btnMasEmergencia).setOnClickListener(v -> startActivity(new Intent(this, EmergenciaActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.btnMasFe).setVisibility(prefs.getEspacioFe() ? View.VISIBLE : View.GONE);
    }
}
