package com.example.siemprejuntos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/** "¿Cómo está hoy?" con tres opciones y aviso opcional a la familia. */
public class EstoyBienActivity extends BaseActivity {

    private Prefs prefs;
    private Contacto principal;
    private String mensaje = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estoy_bien);
        prefs = new Prefs(this);

        List<Contacto> lista = prefs.getContactos();
        principal = lista.isEmpty() ? null : lista.get(0);

        findViewById(R.id.btnBien).setOnClickListener(v ->
                elegir("Bien", "Hola, estoy bien 😊. Un abrazo grande.", false));
        findViewById(R.id.btnRegular).setOnClickListener(v ->
                elegir("Más o menos", "Hola, hoy estoy más o menos. Llámame cuando puedas.", false));
        findViewById(R.id.btnMal).setOnClickListener(v ->
                elegir("No me siento bien", "Hola, hoy no me siento bien. Por favor llámame.", true));

        findViewById(R.id.btnVolverAnimo).setOnClickListener(v -> finish());
        findViewById(R.id.btnNoAvisar).setOnClickListener(v -> finish());
        findViewById(R.id.btnLlamar131).setOnClickListener(v -> Util.marcar(this, "131"));

        findViewById(R.id.btnAvisarWhatsApp).setOnClickListener(v ->
                Util.abrirWhatsApp(this, principal.telefono, mensaje));
        findViewById(R.id.btnAvisarSms).setOnClickListener(v ->
                Util.abrirSms(this, principal.telefono, mensaje));
    }

    private void elegir(String etiqueta, String texto, boolean mal) {
        prefs.guardarAnimo(etiqueta);

        String nombre = prefs.getNombre();
        mensaje = nombre.isEmpty() ? texto : texto + " — " + nombre;

        findViewById(R.id.panelAnimo).setVisibility(View.GONE);
        findViewById(R.id.panelAvisar).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.tvResultado)).setText("Usted dijo: " + etiqueta);
        findViewById(R.id.btnLlamar131).setVisibility(mal ? View.VISIBLE : View.GONE);

        TextView tvSin = findViewById(R.id.tvSinContactos);
        Button wa = findViewById(R.id.btnAvisarWhatsApp);
        Button sms = findViewById(R.id.btnAvisarSms);
        if (principal == null) {
            tvSin.setText("Aún no hay contactos guardados. Un familiar puede agregarlos en Configuración.");
            wa.setEnabled(false);
            wa.setAlpha(0.4f);
            sms.setEnabled(false);
            sms.setAlpha(0.4f);
        } else {
            tvSin.setText("¿Quiere avisar a " + principal.nombre + "?");
        }
    }
}
