package com.example.siemprejuntos;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/**
 * Modo audio: lee los textos en voz alta con la voz en español que ya trae el celular.
 * No usa internet ni cuesta nada.
 */
public class Voz implements TextToSpeech.OnInitListener {

    private final Context contexto;
    private TextToSpeech tts;
    private boolean iniciado = false;
    private boolean disponible = false;
    private String pendiente = null;

    public Voz(Context contexto) {
        this.contexto = contexto.getApplicationContext();
        tts = new TextToSpeech(this.contexto, this);
    }

    @Override
    public void onInit(int estado) {
        iniciado = true;
        if (estado != TextToSpeech.SUCCESS || tts == null) {
            disponible = false;
            return;
        }
        int r = tts.setLanguage(new Locale("es", "CL"));
        if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) {
            r = tts.setLanguage(new Locale("es", "ES"));
        }
        if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) {
            r = tts.setLanguage(new Locale("es"));
        }
        disponible = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED;
        tts.setSpeechRate(0.85f); // un poco más lento, más fácil de entender
        if (pendiente != null) {
            String t = pendiente;
            pendiente = null;
            hablar(t);
        }
    }

    /** Lee el texto. Si ya estaba leyendo, se detiene (el mismo botón sirve para parar). */
    public void hablar(String texto) {
        if (tts == null) {
            avisarSinVoz();
            return;
        }
        if (!iniciado) {
            pendiente = texto;
            return;
        }
        if (!disponible) {
            avisarSinVoz();
            return;
        }
        if (tts.isSpeaking()) {
            tts.stop();
            return;
        }
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "siemprejuntos");
    }

    public void detener() {
        if (tts != null) {
            tts.stop();
        }
    }

    public void cerrar() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    private void avisarSinVoz() {
        Toast.makeText(contexto, "Este teléfono no tiene voz en español. "
                + "Pídale a su familiar que la active en Ajustes > Idioma > Texto a voz.",
                Toast.LENGTH_LONG).show();
    }
}
