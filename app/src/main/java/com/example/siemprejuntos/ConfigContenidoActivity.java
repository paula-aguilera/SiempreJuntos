package com.example.siemprejuntos;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/** Para el familiar: recordatorio del día, música favorita, remedios y videos de la familia. */
public class ConfigContenidoActivity extends BaseActivity {

    private Prefs prefs;
    private EditText etRecordatorio;
    private EditText etMusica;
    private CheckBox cbRemedios;
    private CheckBox cbFe;
    private EditText etVideoTitulo;
    private EditText etVideoUrl;
    private LinearLayout llVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_contenido);
        prefs = new Prefs(this);

        etRecordatorio = findViewById(R.id.etRecordatorio);
        etMusica = findViewById(R.id.etMusica);
        cbRemedios = findViewById(R.id.cbRemedios);
        cbFe = findViewById(R.id.cbFe);
        etVideoTitulo = findViewById(R.id.etVideoTitulo);
        etVideoUrl = findViewById(R.id.etVideoUrl);
        llVideos = findViewById(R.id.llVideosCfg);

        etRecordatorio.setText(prefs.getRecordatorio());
        etMusica.setText(prefs.getMusica());
        cbRemedios.setChecked(prefs.getPreguntaRemedios());
        cbFe.setChecked(prefs.getEspacioFe());

        findViewById(R.id.btnGuardarContenido).setOnClickListener(v -> {
            guardar();
            Toast.makeText(this, "Guardado.", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnAgregarVideo).setOnClickListener(v -> agregarVideo());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        refrescarVideos();
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardar();
    }

    private void guardar() {
        prefs.setRecordatorio(etRecordatorio.getText().toString());
        prefs.setMusica(etMusica.getText().toString());
        prefs.setPreguntaRemedios(cbRemedios.isChecked());
        prefs.setEspacioFe(cbFe.isChecked());
    }

    private void agregarVideo() {
        String titulo = etVideoTitulo.getText().toString().trim();
        String url = etVideoUrl.getText().toString().trim();
        if (titulo.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Escriba el nombre y el enlace del video.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!url.contains(".")) {
            Toast.makeText(this, "Ese enlace no parece correcto.", Toast.LENGTH_LONG).show();
            return;
        }
        List<VideoLink> lista = prefs.getVideos();
        if (lista.size() >= 20) {
            Toast.makeText(this, "Ya hay 20 videos. Quite alguno para agregar otro.", Toast.LENGTH_LONG).show();
            return;
        }
        lista.add(new VideoLink(titulo, url));
        prefs.setVideos(lista);
        etVideoTitulo.setText("");
        etVideoUrl.setText("");
        refrescarVideos();
    }

    private void refrescarVideos() {
        llVideos.removeAllViews();
        final List<VideoLink> lista = prefs.getVideos();
        for (int i = 0; i < lista.size(); i++) {
            final int indice = i;
            View fila = getLayoutInflater().inflate(R.layout.item_video_cfg, llVideos, false);
            ((TextView) fila.findViewById(R.id.tvVideoCfg)).setText(lista.get(i).titulo);
            fila.findViewById(R.id.btnQuitarVideo).setOnClickListener(v -> {
                lista.remove(indice);
                prefs.setVideos(lista);
                refrescarVideos();
            });
            llVideos.addView(fila);
        }
    }
}
