package com.example.siemprejuntos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/** Un tutorial mostrado paso a paso, con letra grande. */
public class LeccionActivity extends BaseActivity {

    public static final String EXTRA_INDICE = "indice";
    private static final String KEY_PASO = "paso";

    private Contenido.Leccion leccion;
    private int paso = 0;

    private TextView tvNumero;
    private TextView tvTexto;
    private TextView tvConsejo;
    private Button btnSiguiente;
    private Button btnAnterior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leccion);

        int indice = getIntent().getIntExtra(EXTRA_INDICE, 0);
        if (indice < 0 || indice >= Contenido.LECCIONES.length) {
            indice = 0;
        }
        leccion = Contenido.LECCIONES[indice];
        if (savedInstanceState != null) {
            paso = savedInstanceState.getInt(KEY_PASO, 0);
        }

        ((TextView) findViewById(R.id.tvTituloLeccion)).setText(leccion.icono + "  " + leccion.titulo);
        tvNumero = findViewById(R.id.tvNumeroPaso);
        tvTexto = findViewById(R.id.tvTextoPaso);
        tvConsejo = findViewById(R.id.tvConsejo);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnAnterior = findViewById(R.id.btnAnterior);

        btnSiguiente.setOnClickListener(v -> {
            if (paso < leccion.pasos.length - 1) {
                paso++;
                mostrar();
            } else {
                finish();
            }
        });
        btnAnterior.setOnClickListener(v -> {
            if (paso > 0) {
                paso--;
                mostrar();
            }
        });
        findViewById(R.id.btnVideoLeccion).setOnClickListener(v -> Util.buscarEnYoutube(this, leccion.busquedaVideo));
        findViewById(R.id.btnSalirLeccion).setOnClickListener(v -> finish());

        mostrar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_PASO, paso);
    }

    private void mostrar() {
        int total = leccion.pasos.length;
        tvNumero.setText("Paso " + (paso + 1) + " de " + total);
        tvTexto.setText(leccion.pasos[paso]);
        boolean ultimo = paso == total - 1;
        btnSiguiente.setText(ultimo ? "✔  ¡Listo!" : "Siguiente  ▶");
        btnAnterior.setVisibility(paso == 0 ? View.GONE : View.VISIBLE);
        if (ultimo) {
            tvConsejo.setText("💡 " + leccion.consejo);
            tvConsejo.setVisibility(View.VISIBLE);
        } else {
            tvConsejo.setVisibility(View.GONE);
        }
    }
}
