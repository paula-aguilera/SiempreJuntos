package com.example.siemprejuntos;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Pantalla de ayuda. Primero pregunta si de verdad necesita ayuda (para evitar toques por error).
 * Si responde que sí, deja el teléfono listo con el 131 (la persona solo toca el botón verde de
 * llamar) y permite avisar a la familia con la ubicación.
 */
public class AyudaActivity extends BaseActivity {

    private static final String KEY_CONFIRMADO = "confirmado";

    private Prefs prefs;
    private Contacto principal;
    private Location ubicacion;
    private TextView tvUbicacion;
    private boolean confirmado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);
        prefs = new Prefs(this);

        tvUbicacion = findViewById(R.id.tvUbicacion);
        TextView tvContacto = findViewById(R.id.tvContactoPrincipal);
        Button btnWhatsApp = findViewById(R.id.btnAvisarWhatsApp);
        Button btnSms = findViewById(R.id.btnAvisarSms);
        Button btnLlamarFamiliar = findViewById(R.id.btnLlamarFamiliar);

        List<Contacto> lista = prefs.getContactos();
        principal = lista.isEmpty() ? null : lista.get(0);

        if (principal == null) {
            tvContacto.setText("Aún no hay contactos guardados. Un familiar debe agregarlos en Configuración.");
            deshabilitar(btnWhatsApp);
            deshabilitar(btnSms);
            deshabilitar(btnLlamarFamiliar);
        } else {
            tvContacto.setText("Se avisará a: " + principal.nombre + " (" + principal.relacion + ")");
        }

        findViewById(R.id.btnLlamar131).setOnClickListener(v -> Util.marcar(this, "131"));
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        btnWhatsApp.setOnClickListener(v -> Util.abrirWhatsApp(this, principal.telefono, armarMensaje()));
        btnSms.setOnClickListener(v -> Util.abrirSms(this, principal.telefono, armarMensaje()));
        btnLlamarFamiliar.setOnClickListener(v -> Util.marcar(this, principal.telefono));

        // Se pregunta primero. Si la pantalla se gira o se vuelve a abrir, no se repite la llamada.
        boolean yaConfirmado = savedInstanceState != null && savedInstanceState.getBoolean(KEY_CONFIRMADO, false);
        confirmado = yaConfirmado;
        mostrarPanel();

        findViewById(R.id.btnSiAyuda).setOnClickListener(v -> {
            confirmado = true;
            mostrarPanel();
            Util.marcar(this, "131");
        });
        findViewById(R.id.btnNoAyuda).setOnClickListener(v -> finish());
    }

    private void mostrarPanel() {
        findViewById(R.id.panelConfirmar).setVisibility(confirmado ? View.GONE : View.VISIBLE);
        findViewById(R.id.panelAyuda).setVisibility(confirmado ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_CONFIRMADO, confirmado);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ubicacion = Util.ultimaUbicacion(this);
        if (ubicacion == null) {
            tvUbicacion.setText("No pudimos saber dónde está. El mensaje avisará sin ubicación.");
        } else {
            tvUbicacion.setText("Se enviará su ubicación aproximada (de hace " + Util.minutosDesde(ubicacion) + " min).");
        }
    }

    private void deshabilitar(Button b) {
        b.setEnabled(false);
        b.setAlpha(0.4f);
    }

    private String armarMensaje() {
        String nombre = prefs.getNombre();
        StringBuilder sb = new StringBuilder("🆘 NECESITO AYUDA");
        if (!nombre.isEmpty()) {
            sb.append(" (").append(nombre).append(")");
        }
        sb.append(". ");
        if (ubicacion != null) {
            sb.append("Estoy aquí: ").append(Util.enlaceMapa(ubicacion))
                    .append(" (ubicación aproximada, de hace ").append(Util.minutosDesde(ubicacion)).append(" min). ");
        } else {
            sb.append("No pude enviar mi ubicación. ");
        }
        sb.append("Por favor llámame.");
        return sb.toString();
    }
}
