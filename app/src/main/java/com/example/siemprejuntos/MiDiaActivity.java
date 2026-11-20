package com.example.siemprejuntos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Saludo diario con preguntas sencillas, una por una.
 * Las respuestas no se guardan: solo se recuerda que hoy ya hubo saludo.
 */
public class MiDiaActivity extends BaseActivity {

    private static final int P_INICIO = 9;
    private static final int P_CORAZON = 8;
    private static final int P_ANIMO = 0;
    private static final int P_DIA = 1;
    private static final int P_REMEDIOS = 2;
    private static final int P_COMIDA = 3;
    private static final int P_RECORDATORIO = 4;

    private Prefs prefs;
    private final List<Integer> pasos = new ArrayList<>();
    private int actual = 0;

    private String respAnimo = "";
    private String respRemedios = "";
    private String respComida = "";
    private boolean estaMal = false;

    private TextView tvProgreso;
    private TextView tvPregunta;
    private TextView tvSub;
    private LinearLayout llOpciones;
    private Contacto principal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_dia);
        prefs = new Prefs(this);

        tvProgreso = findViewById(R.id.tvProgreso);
        tvPregunta = findViewById(R.id.tvPregunta);
        tvSub = findViewById(R.id.tvSub);
        llOpciones = findViewById(R.id.llOpciones);

        List<Contacto> contactos = prefs.getContactos();
        principal = contactos.isEmpty() ? null : contactos.get(0);

        if (Util.horaActual() < 12) {
            pasos.add(P_INICIO);      // un inicio cálido para comenzar el día
        }
        pasos.add(P_ANIMO);
        pasos.add(P_CORAZON);         // pregunta cariñosa al azar
        pasos.add(P_DIA);
        if (prefs.getPreguntaRemedios()) {
            pasos.add(P_REMEDIOS);
        }
        pasos.add(P_COMIDA);
        if (!prefs.getRecordatorio().isEmpty()) {
            pasos.add(P_RECORDATORIO);
        }

        findViewById(R.id.btnSalir).setOnClickListener(v -> finish());
        findViewById(R.id.btnFinVolver).setOnClickListener(v -> finish());
        findViewById(R.id.btnFin131).setOnClickListener(v -> Util.marcar(this, "131"));
        findViewById(R.id.btnFinMusica).setOnClickListener(v -> Util.buscarEnYoutube(this, busquedaMusica()));
        findViewById(R.id.btnFinFamilia).setOnClickListener(v ->
                startActivity(new Intent(this, FamiliaActivity.class)));
        findViewById(R.id.btnFinAvisar).setOnClickListener(v -> avisarFamilia());

        mostrarPaso();
    }

    // ------------------------------------------------------------------ pasos

    private void mostrarPaso() {
        if (actual >= pasos.size()) {
            mostrarFinal();
            return;
        }
        boolean hayInicio = pasos.get(0) == P_INICIO;
        int totalPreguntas = pasos.size() - (hayInicio ? 1 : 0);
        int numero = actual + 1 - (hayInicio ? 1 : 0);
        tvProgreso.setText(pasos.get(actual) == P_INICIO
                ? "☀️  Para comenzar el día"
                : "Pregunta " + numero + " de " + totalPreguntas);
        llOpciones.removeAllViews();
        tvSub.setTextSize(20);

        switch (pasos.get(actual)) {
            case P_INICIO:
                inicioDelDia();
                break;
            case P_CORAZON:
                preguntaCorazon();
                break;
            case P_ANIMO:
                preguntaAnimo();
                break;
            case P_DIA:
                preguntaDia();
                break;
            case P_REMEDIOS:
                preguntaRemedios();
                break;
            case P_COMIDA:
                preguntaComida();
                break;
            default:
                avisoRecordatorio();
                break;
        }
    }

    private void siguiente() {
        actual++;
        mostrarPaso();
    }

    /** Primer paso de la mañana: un saludo cálido y un pensamiento positivo, sin preguntar nada. */
    private void inicioDelDia() {
        String nombre = prefs.getNombre();
        tvPregunta.setText(nombre.isEmpty() ? "¡Buenos días! ☀️" : "¡Buenos días, " + nombre + "! ☀️");
        tvSub.setText("Hoy es " + Util.fechaHoy() + ".\n\n" + Contenido.mensajeDelDia(prefs.getEspacioFe()));
        tvSub.setTextSize(21);
        agregarOpcion("💛  ¡Comencemos el día!", R.drawable.bg_btn_verde, this::siguiente);
    }

    /** Una pregunta cariñosa al azar, con respuestas para tocar y un cierre de ánimo. */
    private void preguntaCorazon() {
        final Contenido.Corazon q = Contenido.preguntaDelCorazon(prefs.getEspacioFe(), Util.horaActual());
        tvPregunta.setText(q.pregunta);
        tvSub.setText((prefs.getEspacioFe() ? "📖  " : "💛  ") + q.inspiracion);
        int[] fondos = {R.drawable.bg_btn_verde, R.drawable.bg_btn_azul, R.drawable.bg_btn_naranja};
        for (int i = 0; i < q.opciones.length; i++) {
            agregarOpcion(q.opciones[i], fondos[i % fondos.length], () ->
                    new AlertDialog.Builder(this)
                            .setTitle("💛")
                            .setMessage(q.cierre)
                            .setCancelable(false)
                            .setPositiveButton("SEGUIR", (d, w) -> siguiente())
                            .show());
        }
    }

    private void preguntaAnimo() {
        int h = Util.horaActual();
        tvPregunta.setText(h < 12 ? "¿Cómo amaneció hoy?" : (h < 20 ? "¿Cómo ha estado hoy?" : "¿Cómo pasó el día?"));
        tvSub.setText("Elija la carita que mejor lo diga.");
        agregarOpcion("😊  Bien", R.drawable.bg_btn_verde, () -> {
            respAnimo = "bien";
            siguiente();
        });
        agregarOpcion("😐  Más o menos", R.drawable.bg_btn_naranja, () -> {
            respAnimo = "más o menos";
            siguiente();
        });
        agregarOpcion("😟  No muy bien", R.drawable.bg_btn_rojo, () -> {
            respAnimo = "no muy bien";
            estaMal = true;
            siguiente();
        });
    }

    private void preguntaDia() {
        tvPregunta.setText("¿Qué día es hoy?");
        tvSub.setText("Toque la respuesta que le parezca correcta.");

        final int hoy = Util.diaDeLaSemanaHoy();
        List<Integer> opciones = new ArrayList<>();
        opciones.add(hoy);
        opciones.add((hoy + 1) % 7);
        opciones.add((hoy + 3) % 7);
        Collections.shuffle(opciones);

        for (final int dia : opciones) {
            String nombre = Util.nombreDia(dia);
            String etiqueta = Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
            agregarOpcion(etiqueta, R.drawable.bg_btn_azul, () -> {
                if (dia == hoy) {
                    Toast.makeText(this, "¡Muy bien!", Toast.LENGTH_SHORT).show();
                    siguiente();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Gracias por responder")
                            .setMessage("Hoy es " + Util.fechaHoy() + ".")
                            .setCancelable(false)
                            .setPositiveButton("SEGUIR", (d, w) -> siguiente())
                            .show();
                }
            });
        }
    }

    private void preguntaRemedios() {
        tvPregunta.setText("¿Ya tomó sus remedios de hoy?");
        tvSub.setText("Solo para acompañarlo(a). Usted decide qué responder.");
        agregarOpcion("Sí, ya los tomé", R.drawable.bg_btn_verde, () -> {
            respRemedios = "ya tomé mis remedios";
            siguiente();
        });
        agregarOpcion("Todavía no", R.drawable.bg_btn_naranja, () -> {
            respRemedios = "todavía no tomo mis remedios";
            new AlertDialog.Builder(this)
                    .setTitle("Un recordatorio")
                    .setMessage("Tómelos a su hora. Si tiene dudas, pregunte a su médico o a su familia.")
                    .setCancelable(false)
                    .setPositiveButton("ENTENDIDO", (d, w) -> siguiente())
                    .show();
        });
        agregarOpcion("Hoy no me tocan", R.drawable.bg_btn_gris, () -> {
            respRemedios = "";
            siguiente();
        });
    }

    private void preguntaComida() {
        int h = Util.horaActual();
        final String momento;
        if (h < 12) {
            momento = "desayuno";
            tvPregunta.setText("¿Ya tomó desayuno?");
        } else if (h < 17) {
            momento = "almuerzo";
            tvPregunta.setText("¿Ya almorzó?");
        } else {
            momento = "once o cena";
            tvPregunta.setText("¿Ya tomó once o cenó?");
        }
        tvSub.setText("Comer rico y tomar agüita también es compañía.");
        agregarOpcion("Sí, ya comí", R.drawable.bg_btn_verde, () -> {
            respComida = "ya tomé " + momento;
            siguiente();
        });
        agregarOpcion("Todavía no", R.drawable.bg_btn_naranja, () -> {
            respComida = "todavía no tomo " + momento;
            new AlertDialog.Builder(this)
                    .setTitle("Cuando pueda")
                    .setMessage("Coma algo rico y tome un vaso de agua 💧")
                    .setCancelable(false)
                    .setPositiveButton("ENTENDIDO", (d, w) -> siguiente())
                    .show();
        });
    }

    private void avisoRecordatorio() {
        tvPregunta.setText("📌 Algo importante para hoy");
        tvSub.setText(prefs.getRecordatorio());
        tvSub.setTextSize(26);
        agregarOpcion("Entendido", R.drawable.bg_btn_verde, this::siguiente);
    }

    // ------------------------------------------------------------------ final

    private void mostrarFinal() {
        prefs.marcarSaludoHoy();
        prefs.guardarAnimo(respAnimo);

        findViewById(R.id.panelPregunta).setVisibility(View.GONE);
        findViewById(R.id.panelFin).setVisibility(View.VISIBLE);

        String nombre = prefs.getNombre();
        ((TextView) findViewById(R.id.tvFinTitulo)).setText(
                nombre.isEmpty() ? "¡Gracias por saludar!" : "¡Gracias por saludar, " + nombre + "!");

        TextView tvTexto = findViewById(R.id.tvFinTexto);
        if (estaMal) {
            tvTexto.setText("Lamento que hoy no se sienta muy bien. No está solo(a): "
                    + "puede hablar ahora con su familia. Si es una urgencia, llame al 131.");
            findViewById(R.id.btnFin131).setVisibility(View.VISIBLE);
        } else if ("más o menos".equals(respAnimo)) {
            tvTexto.setText("Gracias por contarme. Un ratito de música o una llamada a alguien querido puede ayudar.");
        } else {
            tvTexto.setText("Me alegra mucho. Que tenga un día lindo. "
                    + "Si quiere, avise a su familia o escuche un poco de música.");
        }

        Button avisar = findViewById(R.id.btnFinAvisar);
        if (principal == null) {
            avisar.setEnabled(false);
            avisar.setAlpha(0.4f);
        }
    }

    private String busquedaMusica() {
        return Contenido.busquedaMusica(prefs.getMusica(), prefs.getEdad());
    }

    private void avisarFamilia() {
        if (principal == null) {
            return;
        }
        String nombre = prefs.getNombre();
        StringBuilder sb = new StringBuilder(Util.saludoSegunHora()).append(". ");
        if (!nombre.isEmpty()) {
            sb.append("Soy ").append(nombre).append(". ");
        }
        sb.append("Hoy estoy ").append(respAnimo).append(". ");
        if (!respComida.isEmpty()) {
            sb.append(respComida.substring(0, 1).toUpperCase()).append(respComida.substring(1)).append(". ");
        }
        if (!respRemedios.isEmpty()) {
            sb.append(respRemedios.substring(0, 1).toUpperCase()).append(respRemedios.substring(1)).append(". ");
        }
        sb.append("Un abrazo 😊");
        Util.abrirWhatsApp(this, principal.telefono, sb.toString());
    }

    // ---------------------------------------------------------------- ayuda

    private void agregarOpcion(String texto, int fondo, final Runnable accion) {
        Button b = (Button) getLayoutInflater().inflate(R.layout.item_opcion, llOpciones, false);
        b.setText(texto);
        b.setBackgroundResource(fondo);
        b.setOnClickListener(v -> accion.run());
        llOpciones.addView(b);
    }
}
