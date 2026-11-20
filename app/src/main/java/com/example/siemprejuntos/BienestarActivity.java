package com.example.siemprejuntos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/** Música, ejercicios suaves, infusiones, respiración y videos que agrega la familia. */
public class BienestarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienestar);
        Prefs prefs = new Prefs(this);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvAvisoSalud)).setText("ℹ️  " + Contenido.AVISO_SALUD);

        final String busquedaMusica = Contenido.busquedaMusica(prefs.getMusica(), prefs.getEdad());
        String epoca = Contenido.epocaMusical(prefs.getEdad());
        if (epoca != null && prefs.getMusica().isEmpty()) {
            ((Button) findViewById(R.id.btnMusica)).setText("🎵  Música de su juventud (" + epoca + ")");
        }
        findViewById(R.id.btnMusica).setOnClickListener(v -> Util.buscarEnYoutube(this, busquedaMusica));
        findViewById(R.id.btnEjercicios).setOnClickListener(v -> Util.buscarEnYoutube(this, Contenido.BUSQUEDA_EJERCICIOS));
        findViewById(R.id.btnInfusiones).setOnClickListener(v -> Util.buscarEnYoutube(this, Contenido.BUSQUEDA_INFUSIONES));
        findViewById(R.id.btnRespiracion).setOnClickListener(v -> Util.buscarEnYoutube(this, Contenido.BUSQUEDA_RESPIRACION));
        findViewById(R.id.btnMemoria).setOnClickListener(v -> Util.buscarEnYoutube(this, Contenido.BUSQUEDA_MEMORIA));

        LinearLayout lista = findViewById(R.id.llVideosFamilia);
        List<VideoLink> videos = prefs.getVideos();
        findViewById(R.id.tvSinVideos).setVisibility(videos.isEmpty() ? View.VISIBLE : View.GONE);
        for (final VideoLink video : videos) {
            Button b = (Button) getLayoutInflater().inflate(R.layout.item_opcion, lista, false);
            b.setText("▶  " + video.titulo);
            b.setBackgroundResource(R.drawable.bg_btn_naranja);
            b.setOnClickListener(v -> Util.abrirEnApp(this, video.url));
            lista.addView(b);
        }
    }
}
