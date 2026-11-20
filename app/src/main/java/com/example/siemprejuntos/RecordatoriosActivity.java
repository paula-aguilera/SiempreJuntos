package com.example.siemprejuntos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/** Lista y creación de avisos: remedios, agua, citas médicas y otros. La usa la persona o su familiar. */
public class RecordatoriosActivity extends BaseActivity {

    private static final String PERMISO_NOTIFICACIONES = "android.permission.POST_NOTIFICATIONS";

    private Prefs prefs;
    private LinearLayout llAlarmas;
    private TextView tvSin;

    private final ActivityResultLauncher<String> permisoNotificaciones =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (!concedido) {
                    Toast.makeText(this, "Sin este permiso el celular no mostrará los avisos.",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios);
        prefs = new Prefs(this);

        llAlarmas = findViewById(R.id.llAlarmas);
        tvSin = findViewById(R.id.tvSinAlarmas);
        ((TextView) findViewById(R.id.tvAvisoAlarmas)).setText(
                "ℹ️  Toque un aviso para ver sus detalles, agregar una descripción o cambiar cuánto antes avisar. "
                        + "Escriba solo lo necesario, por ejemplo \"Remedio de la mañana\". "
                        + "Los avisos funcionan sin internet. Para que no se atrasen, en Ajustes del celular "
                        + "→ Aplicaciones → SiempreJuntos → Batería, elija \"Sin restricciones\". "
                        + "Esta función ayuda a recordar y no reemplaza las indicaciones de su médico.");

        findViewById(R.id.btnAgregarRemedio).setOnClickListener(v ->
                pedirTexto(Alarma.REMEDIO, "Tomar mi remedio"));
        findViewById(R.id.btnAgregarCita).setOnClickListener(v ->
                pedirTexto(Alarma.CITA, "Ir a mi cita médica"));
        findViewById(R.id.btnAgregarOtro).setOnClickListener(v ->
                pedirTexto(Alarma.OTRO, ""));
        findViewById(R.id.btnAgregarAgua).setOnClickListener(v -> crearAgua());

        pedirPermisoSiHaceFalta();
        refrescar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refrescar();   // por si se cambió algo en la pantalla de detalles
    }

    // --------------------------------------------------------------- permiso

    private void pedirPermisoSiHaceFalta() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, PERMISO_NOTIFICACIONES)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("Para poder avisarle")
                    .setMessage("El celular le pedirá permiso para mostrar avisos. Toque \"Permitir\".")
                    .setCancelable(false)
                    .setPositiveButton("CONTINUAR", (d, w) -> permisoNotificaciones.launch(PERMISO_NOTIFICACIONES))
                    .show();
        }
    }

    // --------------------------------------------------------------- crear

    private void pedirTexto(final String tipo, String sugerido) {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        et.setTextSize(22);
        et.setText(sugerido);
        et.setSelection(et.getText().length());
        et.setHint("¿Qué quiere recordar?");
        FrameLayout marco = new FrameLayout(this);
        int m = Util.dp(this, 24);
        marco.setPadding(m, Util.dp(this, 8), m, 0);
        marco.addView(et);

        new AlertDialog.Builder(this)
                .setTitle("¿Qué quiere que le recordemos?")
                .setView(marco)
                .setPositiveButton("SIGUIENTE", (d, w) -> {
                    String texto = et.getText().toString().trim();
                    if (texto.isEmpty()) {
                        Toast.makeText(this, "Escriba qué quiere recordar.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (Alarma.REMEDIO.equals(tipo)) {
                        pedirHora(tipo, texto, 0, 0, 0);   // el remedio se repite todos los días
                    } else {
                        pedirFecha(tipo, texto);           // citas y otros: con fecha y hora
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void pedirFecha(final String tipo, final String texto) {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (vista, anio, mes, dia) ->
                pedirHora(tipo, texto, anio, mes, dia),
                hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));
        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.setTitle(Alarma.CITA.equals(tipo) ? "¿Qué día es la cita?" : "¿Qué día es?");
        dp.show();
    }

    private void pedirHora(final String tipo, final String texto, final int anio, final int mes, final int dia) {
        Calendar ahora = Calendar.getInstance();
        TimePickerDialog tp = new TimePickerDialog(this, (vista, hora, minuto) ->
                pedirAntes(tipo, texto, hora, minuto, anio, mes, dia),
                ahora.get(Calendar.HOUR_OF_DAY), ahora.get(Calendar.MINUTE), true);
        tp.setTitle("¿A qué hora?");
        tp.show();
    }

    /** Pregunta cuánto tiempo antes quiere que se le avise; viene marcada la opción sugerida. */
    private void pedirAntes(final String tipo, final String texto, final int hora, final int minuto,
                            final int anio, final int mes, final int dia) {
        final int[] elegido = {Alarma.indiceAntes(Alarma.antesPorDefecto(tipo))};
        new AlertDialog.Builder(this)
                .setTitle("¿Cuánto antes quiere que le avisemos?")
                .setSingleChoiceItems(Alarma.etiquetasAntes(tipo), elegido[0], (d, cual) -> elegido[0] = cual)
                .setPositiveButton("GUARDAR", (d, w) -> guardar(new Alarma(prefs.siguienteIdAlarma(), tipo, texto,
                        hora, minuto, anio, mes, dia, "", Alarma.OPCIONES_ANTES[elegido[0]])))
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void guardar(Alarma a) {
        if (!a.esDiaria() && Alarmas.proxima(a, System.currentTimeMillis()) < 0) {
            Toast.makeText(this, "Esa fecha y hora ya pasó. Elija otra.", Toast.LENGTH_LONG).show();
            return;
        }
        List<Alarma> lista = prefs.getAlarmas();
        lista.add(a);
        prefs.setAlarmas(lista);
        Alarmas.programar(this, a);
        refrescar();
        String previo = a.antes > 0 ? ", y " + Alarma.antesTexto(a.antes) + " antes" : "";
        Toast.makeText(this, "Listo. Le avisaremos a las " + a.horaTexto() + previo + ".", Toast.LENGTH_LONG).show();
    }

    /** Agua a las 11:00, 13:00, 16:00 y 19:00, con el recordatorio de beber mínimo 2 litros al día. */
    private void crearAgua() {
        boolean hayAgua = false;
        for (Alarma x : prefs.getAlarmas()) {
            if (Alarma.AGUA.equals(x.tipo)) {
                hayAgua = true;
                break;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("💧 Avisos para tomar agua")
                .setMessage((hayAgua ? "Ya tiene avisos de agua: se cambiarán por estos.\n\n" : "")
                        + "Le avisaremos todos los días a las 11:00, 13:00, 16:00 y 19:00.\n\n"
                        + Alarma.AVISO_AGUA + " Repártalos durante el día.\n\n"
                        + "Si su médico le indicó limitar los líquidos, siga siempre su indicación.")
                .setPositiveButton("CREAR", (d, w) -> {
                    List<Alarma> lista = new ArrayList<>();
                    for (Alarma x : prefs.getAlarmas()) {
                        if (Alarma.AGUA.equals(x.tipo)) {
                            Alarmas.cancelar(this, x);
                        } else {
                            lista.add(x);
                        }
                    }
                    int[] horas = {11, 13, 16, 19};
                    for (int h : horas) {
                        Alarma nueva = new Alarma(prefs.siguienteIdAlarma(), Alarma.AGUA, "Tome un vaso de agua",
                                h, 0, 0, 0, 0);
                        lista.add(nueva);
                        Alarmas.programar(this, nueva);
                    }
                    prefs.setAlarmas(lista);
                    refrescar();
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    // --------------------------------------------------------------- lista

    private void refrescar() {
        llAlarmas.removeAllViews();
        List<Alarma> lista = prefs.getAlarmas();
        Collections.sort(lista, (x, y) -> Long.compare(clave(x), clave(y)));
        tvSin.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        for (final Alarma a : lista) {
            View fila = getLayoutInflater().inflate(R.layout.item_video_cfg, llAlarmas, false);
            String extra = a.antes > 0 ? "\n🔔 Aviso " + Alarma.antesTexto(a.antes) + " antes" : "";
            if (!a.descripcion.isEmpty()) {
                extra += "\n📝 " + a.descripcion;
            }
            ((TextView) fila.findViewById(R.id.tvVideoCfg)).setText(
                    a.icono() + "  " + a.texto + "\n" + a.cuandoTexto() + extra);
            fila.setOnClickListener(v -> startActivity(new Intent(this, AlarmaDetalleActivity.class)
                    .putExtra(Alarmas.EXTRA_ID, a.id)));
            fila.findViewById(R.id.btnQuitarVideo).setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("¿Quitar este aviso?")
                            .setMessage(a.texto + " (" + a.cuandoTexto() + ")")
                            .setPositiveButton("QUITAR", (d, w) -> quitar(a))
                            .setNegativeButton("NO", null)
                            .show());
            llAlarmas.addView(fila);
        }
    }

    /** Orden: primero los diarios por hora, después las citas por fecha. */
    private long clave(Alarma a) {
        long hora = a.hora * 60L + a.minuto;
        if (a.esDiaria()) {
            return hora;
        }
        return 10000L + (a.anio * 10000L + (a.mes + 1) * 100L + a.dia) * 2000L + hora;
    }

    private void quitar(Alarma a) {
        Alarmas.cancelar(this, a);
        List<Alarma> lista = prefs.getAlarmas();
        List<Alarma> nueva = new ArrayList<>();
        for (Alarma x : lista) {
            if (x.id != a.id) {
                nueva.add(x);
            }
        }
        prefs.setAlarmas(nueva);
        refrescar();
    }
}
