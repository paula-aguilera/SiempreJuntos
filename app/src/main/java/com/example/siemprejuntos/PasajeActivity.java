package com.example.siemprejuntos;

import android.os.Bundle;
import android.widget.TextView;

/** Un pasaje con su pensamiento, en letra grande. */
public class PasajeActivity extends BaseActivity {

    public static final String EXTRA_INDICE = "indice";
    private static final String KEY_INDICE = "indice_actual";

    private int indice = 0;
    private TextView tvReferencia;
    private TextView tvPensamiento;
    private Voz voz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasaje);

        indice = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_INDICE, 0)
                : getIntent().getIntExtra(EXTRA_INDICE, 0);
        tvReferencia = findViewById(R.id.tvReferencia);
        tvPensamiento = findViewById(R.id.tvPensamiento);

        voz = new Voz(this);
        findViewById(R.id.btnEscucharPasaje).setOnClickListener(v ->
                voz.hablar(actual().referencia + ". " + actual().pensamiento));
        findViewById(R.id.btnLeerPasaje).setOnClickListener(v -> Util.abrirEnApp(this, actual().url()));
        findViewById(R.id.btnOtroPasaje).setOnClickListener(v -> {
            indice = (indice + 1) % Contenido.PASAJES.length;
            mostrar();
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        mostrar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDICE, indice);
    }

    @Override
    protected void onPause() {
        super.onPause();
        voz.detener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        voz.cerrar();
    }

    private Contenido.Pasaje actual() {
        return Contenido.PASAJES[Math.floorMod(indice, Contenido.PASAJES.length)];
    }

    private void mostrar() {
        Contenido.Pasaje p = actual();
        tvReferencia.setText("📖  " + p.referencia);
        tvPensamiento.setText(p.pensamiento);
    }
}
