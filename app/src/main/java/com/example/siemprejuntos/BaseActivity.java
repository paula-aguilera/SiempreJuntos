package com.example.siemprejuntos;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Base de todas las pantallas.
 * - Deja un margen para que el contenido no quede debajo de la barra de estado ni de los botones
 *   de navegación del celular.
 * - Agrega abajo una barra fija con "Volver" e "Inicio", siempre en el mismo lugar, en todas las
 *   pantallas menos en el inicio.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /** Botones propios de cada pantalla que ahora hace la barra fija de abajo. */
    private static final String[] BOTONES_VOLVER_REPETIDOS = {
            "btnVolver", "btnVolverAnimo", "btnSalir", "btnSalirLeccion", "btnFinVolver", "btnNoAvisar"
    };

    /** Las pantallas que no necesitan la barra (el inicio) lo cambian a false. */
    protected boolean conBarraNavegacion() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (conBarraNavegacion()) {
            LinearLayout raiz = new LinearLayout(this);
            raiz.setOrientation(LinearLayout.VERTICAL);

            View contenido = getLayoutInflater().inflate(layoutResID, raiz, false);
            contenido.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            raiz.addView(contenido);

            View barra = getLayoutInflater().inflate(R.layout.barra_navegacion, raiz, false);
            raiz.addView(barra);
            super.setContentView(raiz);

            barra.findViewById(R.id.btnBarraVolver).setOnClickListener(v -> finish());
            barra.findViewById(R.id.btnBarraInicio).setOnClickListener(v -> {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            });
            ocultarBotonesRepetidos();
        } else {
            super.setContentView(layoutResID);
        }

        View raizVista = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(raizVista, (v, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
    }

    private void ocultarBotonesRepetidos() {
        for (String nombre : BOTONES_VOLVER_REPETIDOS) {
            int id = getResources().getIdentifier(nombre, "id", getPackageName());
            if (id != 0) {
                View b = findViewById(id);
                if (b != null) {
                    b.setVisibility(View.GONE);
                }
            }
        }
    }
}
