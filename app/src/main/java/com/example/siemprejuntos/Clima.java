package com.example.siemprejuntos;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Consulta el clima de hoy en Open-Meteo (no necesita clave). Funciona solo con internet
 * y usando la última ubicación conocida del celular.
 */
public final class Clima {

    public interface Resultado {
        /** Se llama desde un hilo secundario: use runOnUiThread para tocar la pantalla. */
        void listo(String resumen, String consejo);

        void error();
    }

    private Clima() {
    }

    public static void cargar(final Location ubicacion, final Resultado cb) {
        new Thread(() -> {
            HttpURLConnection con = null;
            try {
                String url = "https://api.open-meteo.com/v1/forecast?latitude="
                        + String.format(Locale.US, "%.3f", ubicacion.getLatitude())
                        + "&longitude=" + String.format(Locale.US, "%.3f", ubicacion.getLongitude())
                        + "&current=temperature_2m,weather_code"
                        + "&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max"
                        + "&timezone=auto&forecast_days=1";
                con = (HttpURLConnection) new URL(url).openConnection();
                con.setConnectTimeout(8000);
                con.setReadTimeout(8000);
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        sb.append(linea);
                    }
                }
                JSONObject raiz = new JSONObject(sb.toString());
                JSONObject actual = raiz.getJSONObject("current");
                JSONObject diario = raiz.getJSONObject("daily");
                long ahora = Math.round(actual.getDouble("temperature_2m"));
                int codigo = actual.getInt("weather_code");
                long max = Math.round(first(diario, "temperature_2m_max"));
                long min = Math.round(first(diario, "temperature_2m_min"));
                double lluvia = diario.has("precipitation_probability_max")
                        ? first(diario, "precipitation_probability_max") : 0;

                String resumen = "Ahora: " + ahora + "° · " + descripcion(codigo)
                        + "\nHoy: mínima " + min + "° · máxima " + max + "°";
                String consejo = consejo(max, min, lluvia, codigo);
                cb.listo(resumen, consejo);
            } catch (Exception e) {
                cb.error();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        }).start();
    }

    private static double first(JSONObject o, String clave) throws Exception {
        JSONArray a = o.getJSONArray(clave);
        return a.isNull(0) ? 0 : a.getDouble(0);
    }

    private static String descripcion(int c) {
        if (c == 0) {
            return "despejado";
        } else if (c <= 2) {
            return "poco nublado";
        } else if (c == 3) {
            return "nublado";
        } else if (c == 45 || c == 48) {
            return "con neblina";
        } else if (c >= 51 && c <= 57) {
            return "llovizna";
        } else if (c >= 61 && c <= 67) {
            return "lluvia";
        } else if (c >= 71 && c <= 77) {
            return "nieve";
        } else if (c >= 80 && c <= 82) {
            return "chubascos";
        } else if (c >= 95) {
            return "tormenta";
        }
        return "variable";
    }

    private static String consejo(long max, long min, double lluvia, int codigo) {
        boolean llueve = lluvia >= 50 || (codigo >= 51 && codigo <= 67) || (codigo >= 80 && codigo <= 82) || codigo >= 95;
        if (llueve) {
            return "☔ Puede llover: si sale, lleve paraguas y cuide no resbalar.";
        } else if (max >= 28) {
            return "🥵 Hoy hará calor: tome agüita seguido y use ropa liviana.";
        } else if (min <= 8) {
            return "🧣 Hará frío: abríguese bien, sobre todo en la mañana y en la noche.";
        }
        return "🌤️ Día agradable. Si puede, aproveche de salir al sol un ratito.";
    }
}
