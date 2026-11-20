package com.example.siemprejuntos;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/** Configuración para el familiar: nombre, contactos, fotos, audios, ubicación y PIN. */
public class ConfigActivity extends BaseActivity {

    private Prefs prefs;
    private EditText etNombre;
    private EditText etEdad;
    private LinearLayout llContactos;
    private LinearLayout llRecuerdos;

    private final ActivityResultLauncher<Intent> selectorFotos =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    r -> procesarSeleccion(r, Recuerdo.FOTO));

    private final ActivityResultLauncher<Intent> selectorAudios =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    r -> procesarSeleccion(r, Recuerdo.AUDIO));

    private final ActivityResultLauncher<String[]> permisoUbicacion =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    r -> Toast.makeText(this, "Listo. Puede cambiar el permiso en Ajustes del celular.",
                            Toast.LENGTH_LONG).show());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        prefs = new Prefs(this);

        etNombre = findViewById(R.id.etNombre);
        llContactos = findViewById(R.id.llContactos);
        llRecuerdos = findViewById(R.id.llRecuerdos);
        etNombre.setText(prefs.getNombre());
        etEdad = findViewById(R.id.etEdad);
        if (prefs.getEdad() > 0) {
            etEdad.setText(String.valueOf(prefs.getEdad()));
        }

        findViewById(R.id.btnGuardarNombre).setOnClickListener(v -> {
            guardarNombreYEdad();
            Toast.makeText(this, "Datos guardados.", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnAgregarContacto).setOnClickListener(v -> dialogoAgregarContacto());
        findViewById(R.id.btnAgregarFotos).setOnClickListener(v -> selectorFotos.launch(intentSeleccion("image/*")));
        findViewById(R.id.btnAgregarAudios).setOnClickListener(v -> selectorAudios.launch(intentSeleccion("audio/*")));
        findViewById(R.id.btnUbicacion).setOnClickListener(v -> permisoUbicacion.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}));
        findViewById(R.id.btnConfigRecordatorios).setOnClickListener(v ->
                startActivity(new Intent(this, RecordatoriosActivity.class)));
        findViewById(R.id.btnContenidoDiario).setOnClickListener(v ->
                startActivity(new Intent(this, ConfigContenidoActivity.class)));
        findViewById(R.id.btnCambiarPin).setOnClickListener(v -> dialogoCambiarPin());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        configurarSeccion(R.id.hdrDatos, R.id.cuerpoDatos, "👤  Datos de la persona", true);
        configurarSeccion(R.id.hdrContactos, R.id.cuerpoContactos, "📞  Contactos de confianza", false);
        configurarSeccion(R.id.hdrRecuerdos, R.id.cuerpoRecuerdos, "📷  Fotos y audios", false);
        configurarSeccion(R.id.hdrAvisos, R.id.cuerpoAvisos, "⏰  Recordatorios y saludo", false);
        configurarSeccion(R.id.hdrSeguridad, R.id.cuerpoSeguridad, "🔒  Ayuda y seguridad", false);

        refrescarContactos();
        refrescarRecuerdos();
    }

    /** Una sección que se abre y se cierra al tocar su título, para que la pantalla no quede amontonada. */
    private void configurarSeccion(int idTitulo, int idCuerpo, final String titulo, boolean abierta) {
        final TextView encabezado = findViewById(idTitulo);
        final View cuerpo = findViewById(idCuerpo);
        cuerpo.setVisibility(abierta ? View.VISIBLE : View.GONE);
        encabezado.setText((abierta ? "▾   " : "▸   ") + titulo);
        encabezado.setOnClickListener(v -> {
            boolean abrir = cuerpo.getVisibility() != View.VISIBLE;
            cuerpo.setVisibility(abrir ? View.VISIBLE : View.GONE);
            encabezado.setText((abrir ? "▾   " : "▸   ") + titulo);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Se guardan aunque no se haya tocado el botón.
        guardarNombreYEdad();
    }

    private void guardarNombreYEdad() {
        prefs.setNombre(etNombre.getText().toString());
        String e = etEdad.getText().toString().trim();
        if (e.isEmpty()) {
            prefs.setEdad(0);
            return;
        }
        try {
            int edad = Integer.parseInt(e);
            if (edad >= 18 && edad <= 120) {
                prefs.setEdad(edad);
            }
        } catch (NumberFormatException ignorado) {
            // se deja la edad anterior
        }
    }

    // --------------------------------------------------------------- contactos

    private void refrescarContactos() {
        llContactos.removeAllViews();
        final List<Contacto> lista = prefs.getContactos();
        if (lista.isEmpty()) {
            llContactos.addView(texto("Aún no hay contactos.", 16));
        }
        for (int i = 0; i < lista.size(); i++) {
            final int pos = i;
            final Contacto c = lista.get(i);
            String detalle = (i == 0 ? "★ Principal: " : "") + c.nombre + " (" + c.relacion + ")\n" + c.telefono;
            llContactos.addView(fila(detalle, v -> confirmarBorrado("¿Borrar a " + c.nombre + "?", () -> {
                List<Contacto> nueva = prefs.getContactos();
                if (pos < nueva.size()) {
                    nueva.remove(pos);
                    prefs.setContactos(nueva);
                    refrescarContactos();
                }
            })));
        }
    }

    private void dialogoAgregarContacto() {
        final List<Contacto> actuales = prefs.getContactos();
        if (actuales.size() >= Prefs.MAX_CONTACTOS) {
            Toast.makeText(this, "Ya hay " + Prefs.MAX_CONTACTOS + " contactos. Borre uno para agregar otro.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        LinearLayout caja = new LinearLayout(this);
        caja.setOrientation(LinearLayout.VERTICAL);
        int m = Util.dp(this, 24);
        caja.setPadding(m, Util.dp(this, 8), m, 0);

        final EditText etNom = new EditText(this);
        etNom.setHint("Nombre (ej. Paula)");
        etNom.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        final EditText etRel = new EditText(this);
        etRel.setHint("Relación (ej. Hija)");
        etRel.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final EditText etTel = new EditText(this);
        etTel.setHint("Teléfono (ej. +56 9 1234 5678)");
        etTel.setInputType(InputType.TYPE_CLASS_PHONE);
        caja.addView(etNom);
        caja.addView(etRel);
        caja.addView(etTel);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo contacto")
                .setView(caja)
                .setPositiveButton("GUARDAR", (d, w) -> {
                    String nombre = etNom.getText().toString().trim();
                    String relacion = etRel.getText().toString().trim();
                    String telefono = etTel.getText().toString().trim();
                    String digitos = telefono.replaceAll("[^0-9]", "");
                    if (nombre.isEmpty() || digitos.length() < 8) {
                        Toast.makeText(this, "Escriba un nombre y un teléfono válido.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    actuales.add(new Contacto(nombre, relacion.isEmpty() ? "Familiar" : relacion, telefono));
                    prefs.setContactos(actuales);
                    refrescarContactos();
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    // --------------------------------------------------------------- recuerdos

    private Intent intentSeleccion(String tipoMime) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(tipoMime);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        return i;
    }

    private void procesarSeleccion(ActivityResult resultado, String tipo) {
        Intent datos = resultado.getData();
        if (resultado.getResultCode() != RESULT_OK || datos == null) {
            return;
        }
        List<Uri> uris = new ArrayList<>();
        if (datos.getClipData() != null) {
            for (int i = 0; i < datos.getClipData().getItemCount(); i++) {
                uris.add(datos.getClipData().getItemAt(i).getUri());
            }
        } else if (datos.getData() != null) {
            uris.add(datos.getData());
        }

        List<Recuerdo> actuales = prefs.getRecuerdos();
        int nuevos = 0;
        for (Uri uri : uris) {
            try {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                // si no se puede conservar el permiso, igual se intenta usar
            }
            boolean existe = false;
            for (Recuerdo r : actuales) {
                if (r.uri.equals(uri.toString())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                actuales.add(new Recuerdo(tipo, uri.toString(), nombreArchivo(uri)));
                nuevos++;
            }
        }
        prefs.setRecuerdos(actuales);
        Toast.makeText(this, nuevos + " recuerdo(s) agregado(s).", Toast.LENGTH_SHORT).show();
        refrescarRecuerdos();
    }

    private String nombreArchivo(Uri uri) {
        String nombre = "Recuerdo";
        try (Cursor c = getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int col = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (col >= 0 && c.getString(col) != null) {
                    nombre = c.getString(col);
                }
            }
        } catch (RuntimeException e) {
            // se usa el nombre por defecto
        }
        int punto = nombre.lastIndexOf('.');
        return punto > 0 ? nombre.substring(0, punto) : nombre;
    }

    private void refrescarRecuerdos() {
        llRecuerdos.removeAllViews();
        final List<Recuerdo> lista = prefs.getRecuerdos();
        if (lista.isEmpty()) {
            llRecuerdos.addView(texto("Aún no hay recuerdos.", 16));
        }
        for (int i = 0; i < lista.size(); i++) {
            final int pos = i;
            final Recuerdo r = lista.get(i);
            String etiqueta = (Recuerdo.FOTO.equals(r.tipo) ? "📷 " : "🎵 ") + r.titulo;
            llRecuerdos.addView(fila(etiqueta, v -> confirmarBorrado("¿Quitar este recuerdo?", () -> {
                List<Recuerdo> nueva = prefs.getRecuerdos();
                if (pos < nueva.size()) {
                    nueva.remove(pos);
                    prefs.setRecuerdos(nueva);
                    refrescarRecuerdos();
                }
            })));
        }
    }

    // --------------------------------------------------------------------- PIN

    private void dialogoCambiarPin() {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        et.setHint("4 números");
        FrameLayout marco = new FrameLayout(this);
        int m = Util.dp(this, 24);
        marco.setPadding(m, Util.dp(this, 8), m, 0);
        marco.addView(et);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo PIN")
                .setView(marco)
                .setPositiveButton("GUARDAR", (d, w) -> {
                    String pin = et.getText().toString();
                    if (pin.length() == 4) {
                        prefs.setPin(pin);
                        Toast.makeText(this, "PIN cambiado.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "El PIN debe tener 4 números.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    // ----------------------------------------------------------------- apoyo UI

    private TextView texto(String contenido, int sp) {
        TextView tv = new TextView(this);
        tv.setText(contenido);
        tv.setTextSize(sp);
        tv.setTextColor(ContextCompat.getColor(this, R.color.am_texto));
        tv.setPadding(0, Util.dp(this, 6), 0, Util.dp(this, 6));
        return tv;
    }

    private LinearLayout fila(String contenido, android.view.View.OnClickListener alBorrar) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(0, Util.dp(this, 4), 0, Util.dp(this, 4));

        TextView tv = texto(contenido, 16);
        fila.addView(tv, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button borrar = new Button(this);
        borrar.setText("BORRAR");
        borrar.setTextColor(ContextCompat.getColor(this, R.color.am_blanco));
        borrar.setBackgroundResource(R.drawable.bg_btn_rojo);
        borrar.setOnClickListener(alBorrar);
        fila.addView(borrar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return fila;
    }

    private void confirmarBorrado(String pregunta, final Runnable accion) {
        new AlertDialog.Builder(this)
                .setMessage(pregunta)
                .setPositiveButton("SÍ, BORRAR", (d, w) -> accion.run())
                .setNegativeButton("NO", null)
                .show();
    }
}
