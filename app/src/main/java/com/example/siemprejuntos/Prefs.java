package com.example.siemprejuntos;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Guarda los datos en el propio celular (SharedPreferences).
 * En esta versión no hay nube: todo queda en este teléfono.
 */
public class Prefs {
    public static final int MAX_CONTACTOS = 5;

    private static final String ARCHIVO = "siemprejuntos";
    private static final String K_NOMBRE = "nombre";
    private static final String K_PIN = "pin";
    private static final String K_CONTACTOS = "contactos";
    private static final String K_RECUERDOS = "recuerdos";
    private static final String K_ANIMO = "animo";
    private static final String K_ANIMO_HORA = "animo_hora";
    private static final String K_UBICACION_PEDIDA = "ubicacion_pedida";
    private static final String K_NOMBRE_PREGUNTADO = "nombre_preguntado";
    private static final String K_EDAD = "edad";
    private static final String K_RECORDATORIO = "recordatorio";
    private static final String K_MUSICA = "musica";
    private static final String K_PREGUNTA_REMEDIOS = "pregunta_remedios";
    private static final String K_VIDEOS = "videos";
    private static final String K_SALUDO_FECHA = "saludo_fecha";
    private static final String K_ESPACIO_FE = "espacio_fe";
    private static final String K_ALARMAS = "alarmas";
    private static final String K_ALARMA_ID = "alarma_id";

    private final SharedPreferences sp;

    public Prefs(Context contexto) {
        sp = contexto.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    // ---- Nombre de la persona ----
    /** true si ya se le preguntó el nombre en el primer ingreso (aunque haya dicho "ahora no"). */
    public boolean yaPreguntoNombre() {
        return sp.getBoolean(K_NOMBRE_PREGUNTADO, false) || !getNombre().isEmpty();
    }

    public void marcarNombrePreguntado() {
        sp.edit().putBoolean(K_NOMBRE_PREGUNTADO, true).apply();
    }

    /** Edad en años; 0 si no se ha indicado. */
    public int getEdad() {
        return sp.getInt(K_EDAD, 0);
    }

    public void setEdad(int edad) {
        sp.edit().putInt(K_EDAD, edad < 0 ? 0 : edad).apply();
    }

    public String getNombre() {
        return sp.getString(K_NOMBRE, "");
    }

    public void setNombre(String nombre) {
        sp.edit().putString(K_NOMBRE, nombre.trim()).apply();
    }

    // ---- PIN del familiar (la persona mayor no lo necesita) ----
    public boolean tienePin() {
        return !sp.getString(K_PIN, "").isEmpty();
    }

    public boolean pinCorrecto(String pin) {
        return sp.getString(K_PIN, "").equals(pin);
    }

    public void setPin(String pin) {
        sp.edit().putString(K_PIN, pin).apply();
    }

    // ---- Contactos de confianza ----
    public List<Contacto> getContactos() {
        List<Contacto> lista = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(sp.getString(K_CONTACTOS, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                lista.add(new Contacto(o.optString("nombre"), o.optString("relacion"), o.optString("telefono")));
            }
        } catch (JSONException e) {
            // si algo falla, se devuelve la lista vacía
        }
        return lista;
    }

    public void setContactos(List<Contacto> lista) {
        JSONArray arr = new JSONArray();
        try {
            for (Contacto c : lista) {
                JSONObject o = new JSONObject();
                o.put("nombre", c.nombre);
                o.put("relacion", c.relacion);
                o.put("telefono", c.telefono);
                arr.put(o);
            }
        } catch (JSONException e) {
            // no debería ocurrir con textos simples
        }
        sp.edit().putString(K_CONTACTOS, arr.toString()).apply();
    }

    // ---- Recuerdos (fotos y audios) ----
    public List<Recuerdo> getRecuerdos() {
        List<Recuerdo> lista = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(sp.getString(K_RECUERDOS, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                lista.add(new Recuerdo(o.optString("tipo"), o.optString("uri"), o.optString("titulo")));
            }
        } catch (JSONException e) {
            // lista vacía
        }
        return lista;
    }

    public void setRecuerdos(List<Recuerdo> lista) {
        JSONArray arr = new JSONArray();
        try {
            for (Recuerdo r : lista) {
                JSONObject o = new JSONObject();
                o.put("tipo", r.tipo);
                o.put("uri", r.uri);
                o.put("titulo", r.titulo);
                arr.put(o);
            }
        } catch (JSONException e) {
            // no debería ocurrir
        }
        sp.edit().putString(K_RECUERDOS, arr.toString()).apply();
    }

    // ---- Último ánimo informado ----
    public void guardarAnimo(String animo) {
        sp.edit().putString(K_ANIMO, animo).putLong(K_ANIMO_HORA, System.currentTimeMillis()).apply();
    }

    // ---- Permiso de ubicación: se explica una sola vez ----
    public boolean yaPidioUbicacion() {
        return sp.getBoolean(K_UBICACION_PEDIDA, false);
    }

    public void marcarUbicacionPedida() {
        sp.edit().putBoolean(K_UBICACION_PEDIDA, true).apply();
    }

    // ---- Recordatorio importante del día (lo escribe el familiar) ----
    public String getRecordatorio() {
        return sp.getString(K_RECORDATORIO, "");
    }

    public void setRecordatorio(String texto) {
        sp.edit().putString(K_RECORDATORIO, texto.trim()).apply();
    }

    // ---- Música favorita (por ejemplo "boleros" o "cueca") ----
    public String getMusica() {
        return sp.getString(K_MUSICA, "");
    }

    public void setMusica(String texto) {
        sp.edit().putString(K_MUSICA, texto.trim()).apply();
    }

    // ---- Preguntar por los remedios en el saludo (opcional) ----
    public boolean getPreguntaRemedios() {
        return sp.getBoolean(K_PREGUNTA_REMEDIOS, false);
    }

    public void setPreguntaRemedios(boolean si) {
        sp.edit().putBoolean(K_PREGUNTA_REMEDIOS, si).apply();
    }

    // ---- Videos de la familia (título + enlace) ----
    public List<VideoLink> getVideos() {
        List<VideoLink> lista = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(sp.getString(K_VIDEOS, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                lista.add(new VideoLink(o.optString("titulo"), o.optString("url")));
            }
        } catch (JSONException e) {
            // lista vacía
        }
        return lista;
    }

    public void setVideos(List<VideoLink> lista) {
        JSONArray arr = new JSONArray();
        try {
            for (VideoLink v : lista) {
                JSONObject o = new JSONObject();
                o.put("titulo", v.titulo);
                o.put("url", v.url);
                arr.put(o);
            }
        } catch (JSONException e) {
            // no debería ocurrir
        }
        sp.edit().putString(K_VIDEOS, arr.toString()).apply();
    }

    // ---- Saludo diario: solo se guarda la fecha del último saludo, no las respuestas ----
    public boolean saludoHechoHoy() {
        return Util.claveHoy().equals(sp.getString(K_SALUDO_FECHA, ""));
    }

    public void marcarSaludoHoy() {
        sp.edit().putString(K_SALUDO_FECHA, Util.claveHoy()).apply();
    }

    // ---- Espacio de fe (Palabras de ánimo): lo activa el familiar ----
    public boolean getEspacioFe() {
        return sp.getBoolean(K_ESPACIO_FE, false);
    }

    public void setEspacioFe(boolean si) {
        sp.edit().putBoolean(K_ESPACIO_FE, si).apply();
    }

    // ---- Alarmas: remedios, agua, citas médicas y otros avisos ----
    public List<Alarma> getAlarmas() {
        List<Alarma> lista = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(sp.getString(K_ALARMAS, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String tipo = o.optString("tipo");
                int antes = o.has("antes") ? o.optInt("antes") : Alarma.antesPorDefecto(tipo);
                lista.add(new Alarma(o.optInt("id"), tipo, o.optString("texto"),
                        o.optInt("hora"), o.optInt("minuto"), o.optInt("anio"), o.optInt("mes"), o.optInt("dia"),
                        o.optString("descripcion"), antes));
            }
        } catch (JSONException e) {
            // lista vacía
        }
        return lista;
    }

    public void setAlarmas(List<Alarma> lista) {
        JSONArray arr = new JSONArray();
        try {
            for (Alarma a : lista) {
                JSONObject o = new JSONObject();
                o.put("id", a.id);
                o.put("tipo", a.tipo);
                o.put("texto", a.texto);
                o.put("hora", a.hora);
                o.put("minuto", a.minuto);
                o.put("anio", a.anio);
                o.put("mes", a.mes);
                o.put("dia", a.dia);
                o.put("descripcion", a.descripcion);
                o.put("antes", a.antes);
                arr.put(o);
            }
        } catch (JSONException e) {
            // no debería ocurrir
        }
        sp.edit().putString(K_ALARMAS, arr.toString()).apply();
    }

    /** Número único para cada alarma nueva. */
    public int siguienteIdAlarma() {
        int id = sp.getInt(K_ALARMA_ID, 1);
        sp.edit().putInt(K_ALARMA_ID, id + 1).apply();
        return id;
    }
}
