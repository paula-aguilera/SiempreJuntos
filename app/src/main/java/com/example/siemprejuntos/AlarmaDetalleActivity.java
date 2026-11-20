package com.example.siemprejuntos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** Detalles de un aviso: se puede cambiar el texto, la hora o fecha, agregar una descripción y elegir cuánto antes avisar. */
public class AlarmaDetalleActivity extends BaseActivity {

    private Prefs prefs;
    private Alarma original;
    private int hora;
    private int minuto;
    private int anio;
    private int mes;
    private int dia;
    private int antes;

    private EditText etTexto;
    private EditText etDescripcion;
    private TextView tvCuando;
    private Button btnAntes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma_detalle);
        prefs = new Prefs(this);

        original = Alarmas.buscar(this, getIntent().getIntExtra(Alarmas.EXTRA_ID, -1));
        if (original == null) {
            finish();
            return;
        }
        hora = original.hora;
        minuto = original.minuto;
        anio = original.anio;
        mes = original.mes;
        dia = original.dia;
        antes = original.antes;

        ((TextView) findViewById(R.id.tvDetTipo)).setText(original.icono() + "  " + original.nombreTipo());
        etTexto = findViewById(R.id.etDetTexto);
        etDescripcion = findViewById(R.id.etDetDescripcion);
        tvCuando = findViewById(R.id.tvDetCuando);
        btnAntes = findViewById(R.id.btnDetAntes);
        etTexto.setText(original.texto);
        etDescripcion.setText(original.descripcion);

        if (Alarma.AGUA.equals(original.tipo)) {
            TextView tip = findViewById(R.id.tvDetAgua);
            tip.setText("💧 " + Alarma.AVISO_AGUA + " Si su médico le indicó limitar los líquidos, siga su indicación.");
            tip.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnDetHora).setOnClickListener(v -> new TimePickerDialog(this, (vista, h, m) -> {
            hora = h;
            minuto = m;
            actualizar();
        }, hora, minuto, true).show());

        Button btnFecha = findViewById(R.id.btnDetFecha);
        if (original.esDiaria()) {
            btnFecha.setVisibility(View.GONE);
        } else {
            btnFecha.setOnClickListener(v -> {
                DatePickerDialog dp = new DatePickerDialog(this, (vista, y, mo, d) -> {
                    anio = y;
                    mes = mo;
                    dia = d;
                    actualizar();
                }, anio, mes, dia);
                dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dp.show();
            });
        }

        btnAntes.setOnClickListener(v -> {
            final int[] elegido = {Alarma.indiceAntes(antes)};
            new AlertDialog.Builder(this)
                    .setTitle("¿Cuánto antes quiere que le avisemos?")
                    .setSingleChoiceItems(Alarma.etiquetasAntes(original.tipo), elegido[0], (d, cual) -> elegido[0] = cual)
                    .setPositiveButton("LISTO", (d, w) -> {
                        antes = Alarma.OPCIONES_ANTES[elegido[0]];
                        actualizar();
                    })
                    .setNegativeButton("CANCELAR", null)
                    .show();
        });

        findViewById(R.id.btnDetGuardar).setOnClickListener(v -> guardar());
        findViewById(R.id.btnDetQuitar).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("¿Quitar este aviso?")
                .setMessage(original.texto + " (" + original.cuandoTexto() + ")")
                .setPositiveButton("QUITAR", (d, w) -> quitar())
                .setNegativeButton("NO", null)
                .show());

        actualizar();
    }

    private void actualizar() {
        tvCuando.setText("🕒  " + new Alarma(original.id, original.tipo, "", hora, minuto, anio, mes, dia, "", antes)
                .cuandoTexto());
        btnAntes.setText(antes > 0
                ? "🔔  Aviso " + Alarma.antesTexto(antes) + " antes  (cambiar)"
                : "🔔  Sin aviso previo  (cambiar)");
    }

    private void guardar() {
        String texto = etTexto.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, "Escriba qué quiere recordar.", Toast.LENGTH_LONG).show();
            return;
        }
        Alarma nueva = new Alarma(original.id, original.tipo, texto, hora, minuto, anio, mes, dia,
                etDescripcion.getText().toString().trim(), antes);
        if (!nueva.esDiaria() && Alarmas.proxima(nueva, System.currentTimeMillis()) < 0) {
            Toast.makeText(this, "Esa fecha y hora ya pasó. Elija otra.", Toast.LENGTH_LONG).show();
            return;
        }
        Alarmas.cancelar(this, original);
        List<Alarma> lista = new ArrayList<>();
        for (Alarma x : prefs.getAlarmas()) {
            lista.add(x.id == original.id ? nueva : x);
        }
        prefs.setAlarmas(lista);
        Alarmas.programar(this, nueva);
        Toast.makeText(this, "Cambios guardados.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void quitar() {
        Alarmas.cancelar(this, original);
        List<Alarma> lista = new ArrayList<>();
        for (Alarma x : prefs.getAlarmas()) {
            if (x.id != original.id) {
                lista.add(x);
            }
        }
        prefs.setAlarmas(lista);
        finish();
    }
}
