package com.example.siemprejuntos;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Fotos (con botones Anterior / Siguiente) y audios guardados por el familiar. */
public class RecuerdosActivity extends BaseActivity {

    private final List<Recuerdo> fotos = new ArrayList<>();
    private final List<Recuerdo> audios = new ArrayList<>();
    private final ExecutorService hilo = Executors.newSingleThreadExecutor();

    private int indice = 0;
    private View panelFotos;
    private View panelAudios;
    private ImageView ivFoto;
    private TextView tvTituloFoto;
    private TextView tvContador;
    private TextView tvVacio;
    private LinearLayout llAudios;

    private MediaPlayer reproductor;
    private Button botonActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuerdos);

        panelFotos = findViewById(R.id.panelFotos);
        panelAudios = findViewById(R.id.panelAudios);
        ivFoto = findViewById(R.id.ivFoto);
        tvTituloFoto = findViewById(R.id.tvTituloFoto);
        tvContador = findViewById(R.id.tvContador);
        tvVacio = findViewById(R.id.tvVacio);
        llAudios = findViewById(R.id.llAudios);

        for (Recuerdo r : new Prefs(this).getRecuerdos()) {
            if (Recuerdo.FOTO.equals(r.tipo)) {
                fotos.add(r);
            } else if (Recuerdo.AUDIO.equals(r.tipo)) {
                audios.add(r);
            }
        }

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        findViewById(R.id.btnAgregarRecuerdos).setOnClickListener(v ->
                PinHelper.pedir(this, new Prefs(this), () ->
                        startActivity(new Intent(this, ConfigActivity.class))));
        findViewById(R.id.btnFotos).setOnClickListener(v -> mostrarPanelFotos());
        findViewById(R.id.btnAudios).setOnClickListener(v -> mostrarPanelAudios());
        findViewById(R.id.btnSiguiente).setOnClickListener(v -> {
            if (!fotos.isEmpty()) {
                indice = (indice + 1) % fotos.size();
                mostrarFoto();
            }
        });
        findViewById(R.id.btnAnterior).setOnClickListener(v -> {
            if (!fotos.isEmpty()) {
                indice = (indice - 1 + fotos.size()) % fotos.size();
                mostrarFoto();
            }
        });

        if (!fotos.isEmpty()) {
            mostrarPanelFotos();
        } else if (!audios.isEmpty()) {
            mostrarPanelAudios();
        } else {
            tvVacio.setVisibility(View.VISIBLE);
        }
    }

    // ------------------------------------------------------------------ fotos

    private void mostrarPanelFotos() {
        detenerAudio();
        panelAudios.setVisibility(View.GONE);
        if (fotos.isEmpty()) {
            panelFotos.setVisibility(View.GONE);
            tvVacio.setVisibility(View.VISIBLE);
            tvVacio.setText("Aún no hay fotos. Un familiar puede agregarlas en Configuración.");
            return;
        }
        tvVacio.setVisibility(View.GONE);
        panelFotos.setVisibility(View.VISIBLE);
        mostrarFoto();
    }

    private void mostrarFoto() {
        final int pedida = indice;
        Recuerdo r = fotos.get(pedida);
        tvTituloFoto.setText(r.titulo);
        tvContador.setText("Foto " + (pedida + 1) + " de " + fotos.size());
        ivFoto.setImageBitmap(null);
        final Uri uri = Uri.parse(r.uri);
        hilo.execute(() -> {
            final Bitmap bm = Util.cargarImagen(getContentResolver(), uri, 1280);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed() || pedida != indice) {
                    return;
                }
                if (bm != null) {
                    ivFoto.setImageBitmap(bm);
                } else {
                    tvTituloFoto.setText("No se pudo abrir esta foto");
                }
            });
        });
    }

    // ----------------------------------------------------------------- audios

    private void mostrarPanelAudios() {
        detenerAudio();
        panelFotos.setVisibility(View.GONE);
        if (audios.isEmpty()) {
            panelAudios.setVisibility(View.GONE);
            tvVacio.setVisibility(View.VISIBLE);
            tvVacio.setText("Aún no hay audios. Un familiar puede agregarlos en Configuración.");
            return;
        }
        tvVacio.setVisibility(View.GONE);
        panelAudios.setVisibility(View.VISIBLE);
        llAudios.removeAllViews();
        LayoutInflater inflador = LayoutInflater.from(this);
        for (final Recuerdo r : audios) {
            View fila = inflador.inflate(R.layout.item_audio, llAudios, false);
            ((TextView) fila.findViewById(R.id.tvTituloAudio)).setText(r.titulo);
            final Button boton = fila.findViewById(R.id.btnPlay);
            boton.setOnClickListener(v -> alternarAudio(r, boton));
            llAudios.addView(fila);
        }
    }

    private void alternarAudio(Recuerdo r, Button boton) {
        if (reproductor != null && botonActivo == boton) {
            detenerAudio();
            return;
        }
        detenerAudio();
        try {
            reproductor = new MediaPlayer();
            reproductor.setDataSource(this, Uri.parse(r.uri));
            reproductor.setOnPreparedListener(MediaPlayer::start);
            reproductor.setOnCompletionListener(mp -> detenerAudio());
            reproductor.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "No se pudo reproducir este mensaje.", Toast.LENGTH_LONG).show();
                detenerAudio();
                return true;
            });
            reproductor.prepareAsync();
            botonActivo = boton;
            boton.setText("■ DETENER");
        } catch (IOException | RuntimeException e) {
            Toast.makeText(this, "No se pudo reproducir este mensaje.", Toast.LENGTH_LONG).show();
            detenerAudio();
        }
    }

    private void detenerAudio() {
        if (reproductor != null) {
            try {
                reproductor.stop();
            } catch (IllegalStateException e) {
                // aún no estaba listo; se libera igual
            }
            reproductor.release();
            reproductor = null;
        }
        if (botonActivo != null) {
            botonActivo.setText("▶ ESCUCHAR");
            botonActivo = null;
        }
    }

    // ------------------------------------------------------------- ciclo de vida

    @Override
    protected void onStop() {
        super.onStop();
        detenerAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hilo.shutdownNow();
    }
}
