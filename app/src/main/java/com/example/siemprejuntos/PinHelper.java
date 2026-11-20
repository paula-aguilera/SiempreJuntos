package com.example.siemprejuntos;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/** Pide el PIN del familiar (o lo crea la primera vez) antes de entrar a las funciones del cuidador. */
public final class PinHelper {

    private PinHelper() {
    }

    public static void pedir(Context c, Prefs prefs, Runnable alExito) {
        if (prefs.tienePin()) {
            ingresar(c, prefs, alExito);
        } else {
            crear(c, prefs, alExito);
        }
    }

    private static EditText campo(Context c) {
        EditText et = new EditText(c);
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        et.setTextSize(28);
        et.setHint("4 números");
        return et;
    }

    private static FrameLayout envolver(Context c, EditText et) {
        FrameLayout marco = new FrameLayout(c);
        int m = Util.dp(c, 24);
        marco.setPadding(m, Util.dp(c, 8), m, 0);
        marco.addView(et);
        return marco;
    }

    private static void crear(final Context c, final Prefs prefs, final Runnable alExito) {
        final EditText et = campo(c);
        new AlertDialog.Builder(c)
                .setTitle("Crear PIN del familiar")
                .setMessage("Elija 4 números. Los necesitará para cambiar la configuración. "
                        + "La persona mayor no necesita este PIN.")
                .setView(envolver(c, et))
                .setPositiveButton("GUARDAR", (d, w) -> {
                    String pin = et.getText().toString();
                    if (pin.length() == 4) {
                        prefs.setPin(pin);
                        alExito.run();
                    } else {
                        Toast.makeText(c, "El PIN debe tener 4 números.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private static void ingresar(final Context c, final Prefs prefs, final Runnable alExito) {
        final EditText et = campo(c);
        new AlertDialog.Builder(c)
                .setTitle("PIN del familiar")
                .setView(envolver(c, et))
                .setPositiveButton("ENTRAR", (d, w) -> {
                    if (prefs.pinCorrecto(et.getText().toString())) {
                        alExito.run();
                    } else {
                        Toast.makeText(c, "PIN incorrecto.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("OLVIDÉ MI PIN", (d, w) -> new AlertDialog.Builder(c)
                        .setTitle("Si olvidó el PIN")
                        .setMessage("Para restablecerlo, borre los datos de la aplicación: Ajustes del celular → "
                                + "Aplicaciones → SiempreJuntos → Almacenamiento → Borrar datos. "
                                + "Se perderán los contactos y recuerdos guardados.")
                        .setPositiveButton("ENTENDIDO", null)
                        .show())
                .setNegativeButton("CANCELAR", null)
                .show();
    }
}
