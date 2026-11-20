package com.example.siemprejuntos;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

/** Pantalla de inicio: saludo, clima, mensaje del día, accesos y botón de ayuda abajo. */
public class MainActivity extends BaseActivity {

    private Prefs prefs;
    private TextView tvSaludo;
    private TextView tvFecha;
    private TextView tvClima;
    private TextView tvConsejoClima;
    private TextView tvMensaje;
    private Voz voz;
    private Button btnMiDia;
    private long ultimaCargaClima = 0;
    private String climaResumen = null;
    private String climaConsejo = null;

    private final ActivityResultLauncher<String[]> permisoUbicacion =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    r -> cargarClima(true));

    @Override
    protected boolean conBarraNavegacion() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = new Prefs(this);

        tvSaludo = findViewById(R.id.tvSaludo);
        tvFecha = findViewById(R.id.tvFecha);
        tvClima = findViewById(R.id.tvClima);
        tvConsejoClima = findViewById(R.id.tvConsejoClima);
        btnMiDia = findViewById(R.id.btnMiDia);

        tvMensaje = findViewById(R.id.tvMensaje);
        voz = new Voz(this);
        findViewById(R.id.btnEscuchar).setOnClickListener(v -> voz.hablar(textoParaLeer()));

        findViewById(R.id.btnAyuda).setOnClickListener(v -> startActivity(new Intent(this, AyudaActivity.class)));
        btnMiDia.setOnClickListener(v -> startActivity(new Intent(this, MiDiaActivity.class)));
        findViewById(R.id.btnFamilia).setOnClickListener(v -> startActivity(new Intent(this, FamiliaActivity.class)));
        findViewById(R.id.btnRecuerdos).setOnClickListener(v -> startActivity(new Intent(this, RecuerdosActivity.class)));
        findViewById(R.id.btnRecordatorios).setOnClickListener(v -> startActivity(new Intent(this, RecordatoriosActivity.class)));
        findViewById(R.id.btnMas).setOnClickListener(v -> startActivity(new Intent(this, MasActivity.class)));
        findViewById(R.id.btnConfig).setOnClickListener(v -> pedirPin());

        preguntarNombreSiHaceFalta();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String nombre = prefs.getNombre();
        String saludo = Util.saludoSegunHora();
        tvSaludo.setText(nombre.isEmpty() ? saludo : saludo + ", " + nombre);
        tvFecha.setText("Hoy es " + Util.fechaHoy());
        btnMiDia.setText(prefs.saludoHechoHoy() ? "✅  Saludo de hoy listo" : "☀️  Mi saludo de hoy");
        tvMensaje.setText(Contenido.mensajeDelDia(prefs.getEspacioFe()));
        cargarClima(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (voz != null) {
            voz.detener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voz != null) {
            voz.cerrar();
        }
    }

    /** Lo que se lee en voz alta: saludo con nombre, fecha, clima y el mensaje cariñoso. */
    private String textoParaLeer() {
        String nombre = prefs.getNombre();
        StringBuilder sb = new StringBuilder(Util.saludoSegunHora());
        if (!nombre.isEmpty()) {
            sb.append(", ").append(nombre);
        }
        sb.append(". Hoy es ").append(Util.fechaHoy()).append(". ");
        if (climaResumen != null) {
            sb.append(climaResumen).append(". ");
            if (climaConsejo != null && !climaConsejo.isEmpty()) {
                sb.append(climaConsejo).append(". ");
            }
        }
        sb.append(tvMensaje.getText());
        return sb.toString();
    }

    // ----------------------------------------------------------------- clima

    private void cargarClima(boolean forzar) {
        boolean vigente = climaResumen != null
                && System.currentTimeMillis() - ultimaCargaClima < 30 * 60 * 1000L;
        if (vigente && !forzar) {
            mostrarClima(climaResumen, climaConsejo);
            return;
        }
        Location ubicacion = Util.ultimaUbicacion(this);
        if (ubicacion == null) {
            sinClima();
            return;
        }
        tvClima.setText("Buscando el clima de hoy…");
        tvClima.setOnClickListener(null);
        Clima.cargar(ubicacion, new Clima.Resultado() {
            @Override
            public void listo(String resumen, String consejo) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    climaResumen = resumen;
                    climaConsejo = consejo;
                    ultimaCargaClima = System.currentTimeMillis();
                    mostrarClima(resumen, consejo);
                });
            }

            @Override
            public void error() {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        sinClima();
                    }
                });
            }
        });
    }

    private void mostrarClima(String resumen, String consejo) {
        tvClima.setText(resumen);
        tvClima.setOnClickListener(null);
        tvConsejoClima.setText(consejo);
        tvConsejoClima.setVisibility(View.VISIBLE);
    }

    private void sinClima() {
        tvClima.setText("🌤️  Toque aquí para ver el clima de hoy");
        tvConsejoClima.setVisibility(View.GONE);
        tvClima.setOnClickListener(v -> Util.abrirUrl(this,
                "https://www.google.com/search?q=" + Uri.encode("clima hoy")));
    }

    // ------------------------------------------------------------ ubicación

    /** Primer ingreso: pregunta cómo le gusta que le llamen y luego explica el permiso de ubicación. */
    private void preguntarNombreSiHaceFalta() {
        if (prefs.yaPreguntoNombre()) {
            explicarUbicacionSiHaceFalta();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Hola 💛")
                .setMessage("Bienvenido(a) a SiempreJuntos.\n\n"
                        + "Es una compañía sencilla para su día: le saluda, le recuerda sus remedios y citas, "
                        + "le ayuda a hablar con su familia y a pedir ayuda cuando la necesite.\n\n"
                        + "Aquí no está solo(a). Estamos con usted.")
                .setCancelable(false)
                .setPositiveButton("COMENZAR", (d, w) -> pedirNombre())
                .show();
    }

    private void pedirNombre() {
        final EditText et = new EditText(this);
        et.setHint("Por ejemplo: María");
        et.setTextSize(24);
        et.setSingleLine(true);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        int margen = (int) (20 * getResources().getDisplayMetrics().density);
        FrameLayout caja = new FrameLayout(this);
        caja.setPadding(margen, margen / 2, margen, 0);
        caja.addView(et);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Me gustaría conocerle")
                .setMessage("¿Cómo le gusta que le llamen? Así podré saludarle con cariño todos los días.")
                .setView(caja)
                .setCancelable(false)
                .setPositiveButton("GUARDAR", null)
                .setNegativeButton("AHORA NO", (d, w) -> {
                    prefs.marcarNombrePreguntado();
                    Toast.makeText(this, "Está bien. Cuando quiera, me lo cuenta.", Toast.LENGTH_LONG).show();
                    pedirEdad("");
                })
                .create();
        dialogo.setOnShowListener(d -> dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = et.getText().toString().trim();
            if (nombre.isEmpty()) {
                et.setError("Escriba su nombre o toque AHORA NO");
                return;
            }
            prefs.setNombre(nombre);
            prefs.marcarNombrePreguntado();
            tvSaludo.setText(Util.saludoSegunHora() + ", " + nombre);
            dialogo.dismiss();
            pedirEdad(nombre);
        }));
        dialogo.show();
    }

    /** Segundo paso de la bienvenida: la edad (se puede omitir). */
    private void pedirEdad(final String nombre) {
        final EditText et = new EditText(this);
        et.setHint("Por ejemplo: 72");
        et.setTextSize(24);
        et.setSingleLine(true);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        int margen = (int) (20 * getResources().getDisplayMetrics().density);
        FrameLayout caja = new FrameLayout(this);
        caja.setPadding(margen, margen / 2, margen, 0);
        caja.addView(et);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("¿Cuántos años tiene?")
                .setMessage("Solo la usaré para elegir la música de su época, "
                        + "esa que trae buenos recuerdos. El dato queda solo en este teléfono.")
                .setView(caja)
                .setCancelable(false)
                .setPositiveButton("GUARDAR", null)
                .setNegativeButton("AHORA NO", (d, w) -> despedidaBienvenida(nombre))
                .create();
        dialogo.setOnShowListener(d -> dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int edad = 0;
            try {
                edad = Integer.parseInt(et.getText().toString().trim());
            } catch (NumberFormatException e) {
                edad = 0;
            }
            if (edad < 18 || edad > 120) {
                et.setError("Escriba un número, por ejemplo 72, o toque AHORA NO");
                return;
            }
            prefs.setEdad(edad);
            dialogo.dismiss();
            despedidaBienvenida(nombre);
        }));
        dialogo.show();
    }

    /** Último paso de la bienvenida: un saludo cálido y luego el permiso de ubicación. */
    private void despedidaBienvenida(String nombre) {
        String titulo = nombre.isEmpty() ? "¡Gracias! 💛" : "Mucho gusto, " + nombre + " 💛";
        String texto = "Qué alegría tenerle aquí. Cada día le daré los buenos días, "
                + "estaré atento(a) a que se cuide y le acompañaré en lo que necesite.";
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(texto)
                .setCancelable(false)
                .setPositiveButton("ENTRAR", (d, w) -> explicarUbicacionSiHaceFalta())
                .show();
    }

    private void explicarUbicacionSiHaceFalta() {
        if (prefs.yaPidioUbicacion()) {
            return;
        }
        prefs.marcarUbicacionPedida();
        new AlertDialog.Builder(this)
                .setTitle("Para avisar dónde está")
                .setMessage("Cuando toque AYUDA, la aplicación puede enviar a su familia el lugar donde usted está. "
                        + "Solo se usa en ese momento y no se guarda un mapa de por dónde anduvo.\n\n"
                        + "A continuación el teléfono le pedirá permiso.")
                .setCancelable(false)
                .setPositiveButton("CONTINUAR", (d, w) -> permisoUbicacion.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}))
                .show();
    }

    // ------------------------------------------------------------------ PIN

    private void pedirPin() {
        PinHelper.pedir(this, prefs, () -> startActivity(new Intent(this, ConfigActivity.class)));
    }
}
