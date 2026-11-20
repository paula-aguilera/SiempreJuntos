package com.example.siemprejuntos;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Funciones de apoyo: llamadas, WhatsApp, mensajes, ubicación e imágenes. */
public final class Util {

    private Util() {
    }

    // ---------------------------------------------------------------- teléfono

    /** Deja solo dígitos, + y * (por ejemplo "+56 9 1234 5678" queda "+56912345678"). */
    public static String limpiarNumero(String texto) {
        return texto.replaceAll("[^0-9+*]", "");
    }

    /** Número para WhatsApp: solo dígitos y con 56 al inicio si es un celular chileno de 9 dígitos. */
    public static String telefonoInternacional(String texto) {
        String digitos = texto.replaceAll("[^0-9]", "");
        if (digitos.length() == 9 && digitos.startsWith("9")) {
            return "56" + digitos;
        }
        return digitos;
    }

    /** Abre el marcador con el número escrito. La persona solo debe tocar el botón verde de llamar. */
    public static void marcar(Context c, String numero) {
        Uri uri = Uri.fromParts("tel", limpiarNumero(numero), null);
        iniciar(c, new Intent(Intent.ACTION_DIAL, uri), "No se pudo abrir el teléfono.");
    }

    /** Abre el chat de WhatsApp con el mensaje ya escrito (la persona toca enviar). */
    public static void abrirWhatsApp(Context c, String telefono, String mensaje) {
        String url = "https://wa.me/" + telefonoInternacional(telefono);
        if (mensaje != null && !mensaje.isEmpty()) {
            url += "?text=" + Uri.encode(mensaje);
        }
        iniciar(c, new Intent(Intent.ACTION_VIEW, Uri.parse(url)), "No se pudo abrir WhatsApp.");
    }

    /** Abre la app de mensajes con el texto ya escrito (la persona toca enviar). */
    public static void abrirSms(Context c, String telefono, String mensaje) {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", limpiarNumero(telefono), null));
        i.putExtra("sms_body", mensaje);
        iniciar(c, i, "No se pudo abrir los mensajes.");
    }

    private static void iniciar(Context c, Intent intent, String error) {
        try {
            if (!(c instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            c.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(c, error, Toast.LENGTH_LONG).show();
        }
    }

    // --------------------------------------------------------------- ubicación

    /** Última ubicación conocida del celular, o null si no hay permiso o no hay dato. */
    public static Location ultimaUbicacion(Context c) {
        boolean fina = ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean aprox = ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if (!fina && !aprox) {
            return null;
        }
        LocationManager lm = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) {
            return null;
        }
        Location mejor = null;
        try {
            for (String proveedor : lm.getProviders(true)) {
                Location l = lm.getLastKnownLocation(proveedor);
                if (l != null && (mejor == null || l.getTime() > mejor.getTime())) {
                    mejor = l;
                }
            }
        } catch (SecurityException e) {
            return null;
        }
        return mejor;
    }

    public static String enlaceMapa(Location l) {
        return "https://maps.google.com/?q="
                + String.format(Locale.US, "%.6f", l.getLatitude()) + ","
                + String.format(Locale.US, "%.6f", l.getLongitude());
    }

    public static int minutosDesde(Location l) {
        long min = (System.currentTimeMillis() - l.getTime()) / 60000L;
        return (int) Math.max(0L, min);
    }

    // ------------------------------------------------------------------ fechas

    /** Por ejemplo: "jueves 19 de noviembre de 2026". */
    public static String fechaHoy() {
        SimpleDateFormat formato = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "CL"));
        return formato.format(new Date());
    }

    /** Clave de la fecha de hoy, por ejemplo "20261120". */
    public static String claveHoy() {
        return new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
    }

    private static final String[] DIAS = {"domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"};

    /** Nombre del día de la semana según número 0 (domingo) a 6 (sábado). */
    public static String nombreDia(int indice) {
        return DIAS[((indice % 7) + 7) % 7];
    }

    /** Número del día de hoy: 0 = domingo ... 6 = sábado. */
    public static int diaDeLaSemanaHoy() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int horaActual() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /** "Buenos días", "Buenas tardes" o "Buenas noches" según la hora. */
    public static String saludoSegunHora() {
        int h = horaActual();
        if (h < 12) {
            return "Buenos días";
        } else if (h < 20) {
            return "Buenas tardes";
        }
        return "Buenas noches";
    }

    // ------------------------------------------------------------ enlaces y videos

    /** Abre un enlace (YouTube, página web...) en la app que corresponda. */
    public static void abrirUrl(Context c, String url) {
        String limpia = url.trim();
        if (!limpia.startsWith("http://") && !limpia.startsWith("https://")) {
            limpia = "https://" + limpia;
        }
        iniciar(c, new Intent(Intent.ACTION_VIEW, Uri.parse(limpia)), "No se pudo abrir el enlace.");
    }

    /** Abre una página DENTRO de la aplicación (con "Volver" siempre visible abajo). */
    public static void abrirEnApp(Context c, String url) {
        String limpia = url.trim();
        if (!limpia.startsWith("http://") && !limpia.startsWith("https://")) {
            limpia = "https://" + limpia;
        }
        Intent i = new Intent(c, VideoWebActivity.class);
        i.putExtra(VideoWebActivity.EXTRA_URL, limpia);
        c.startActivity(i);
    }

    /** Abre los resultados de búsqueda de YouTube para un tema, dentro de la aplicación. */
    public static void buscarEnYoutube(Context c, String tema) {
        abrirEnApp(c, "https://m.youtube.com/results?search_query=" + Uri.encode(tema));
    }

    // ---------------------------------------------------------------- pantalla

    public static int dp(Context c, int valor) {
        return (int) (valor * c.getResources().getDisplayMetrics().density + 0.5f);
    }

    // ---------------------------------------------------------------- imágenes

    /** Carga una foto reducida (para no gastar memoria) y la gira según su orientación. */
    public static Bitmap cargarImagen(ContentResolver cr, Uri uri, int maxLado) {
        try {
            BitmapFactory.Options medida = new BitmapFactory.Options();
            medida.inJustDecodeBounds = true;
            try (InputStream in = cr.openInputStream(uri)) {
                if (in == null) {
                    return null;
                }
                BitmapFactory.decodeStream(in, null, medida);
            }
            int lado = Math.max(medida.outWidth, medida.outHeight);
            int muestra = 1;
            while (lado / (muestra * 2) >= maxLado) {
                muestra *= 2;
            }
            BitmapFactory.Options opciones = new BitmapFactory.Options();
            opciones.inSampleSize = muestra;
            Bitmap bm;
            try (InputStream in = cr.openInputStream(uri)) {
                if (in == null) {
                    return null;
                }
                bm = BitmapFactory.decodeStream(in, null, opciones);
            }
            if (bm == null) {
                return null;
            }
            int grados = 0;
            try (InputStream in = cr.openInputStream(uri)) {
                if (in != null) {
                    ExifInterface exif = new ExifInterface(in);
                    int o = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    if (o == ExifInterface.ORIENTATION_ROTATE_90) {
                        grados = 90;
                    } else if (o == ExifInterface.ORIENTATION_ROTATE_180) {
                        grados = 180;
                    } else if (o == ExifInterface.ORIENTATION_ROTATE_270) {
                        grados = 270;
                    }
                }
            } catch (IOException | RuntimeException e) {
                grados = 0;
            }
            if (grados != 0) {
                Matrix m = new Matrix();
                m.postRotate(grados);
                Bitmap girado = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                if (girado != bm) {
                    bm.recycle();
                }
                bm = girado;
            }
            return bm;
        } catch (IOException | SecurityException | OutOfMemoryError e) {
            return null;
        }
    }
}
