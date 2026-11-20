package com.example.siemprejuntos;

/**
 * Un aviso programado: remedio, agua, cita médica u otro.
 * Si "anio" es 0, se repite todos los días a esa hora. Si no, es una sola vez en esa fecha.
 * "antes" son los minutos de aviso previo (0 = sin aviso previo).
 */
public class Alarma {
    public static final String REMEDIO = "remedio";
    public static final String AGUA = "agua";
    public static final String CITA = "cita";
    public static final String OTRO = "otro";

    public static final String AVISO_AGUA = "Beba agua en abundancia: mínimo 2 litros al día.";

    public final int id;
    public final String tipo;
    public final String texto;
    public final int hora;
    public final int minuto;
    public final int anio;
    public final int mes;   // 0 a 11, como Calendar
    public final int dia;
    public final String descripcion;
    public final int antes;

    public Alarma(int id, String tipo, String texto, int hora, int minuto, int anio, int mes, int dia,
                  String descripcion, int antes) {
        this.id = id;
        this.tipo = tipo;
        this.texto = texto;
        this.hora = hora;
        this.minuto = minuto;
        this.anio = anio;
        this.mes = mes;
        this.dia = dia;
        this.descripcion = descripcion == null ? "" : descripcion;
        this.antes = Math.max(0, antes);
    }

    /** Aviso nuevo: remedios avisan 10 minutos antes y citas médicas 1 hora antes. */
    public Alarma(int id, String tipo, String texto, int hora, int minuto, int anio, int mes, int dia) {
        this(id, tipo, texto, hora, minuto, anio, mes, dia, "", antesPorDefecto(tipo));
    }

    public static int antesPorDefecto(String tipo) {
        if (REMEDIO.equals(tipo)) {
            return 10;
        }
        if (CITA.equals(tipo)) {
            return 60;
        }
        return 0;
    }

    public boolean esDiaria() {
        return anio == 0;
    }

    public String icono() {
        switch (tipo) {
            case REMEDIO:
                return "💊";
            case AGUA:
                return "💧";
            case CITA:
                return "🩺";
            default:
                return "🔔";
        }
    }

    public String titulo() {
        switch (tipo) {
            case REMEDIO:
                return "Hora de su remedio";
            case AGUA:
                return "Hora de tomar agua";
            case CITA:
                return "Cita médica";
            default:
                return "Recordatorio";
        }
    }

    public String nombreTipo() {
        switch (tipo) {
            case REMEDIO:
                return "Remedio";
            case AGUA:
                return "Agua";
            case CITA:
                return "Cita médica";
            default:
                return "Recordatorio";
        }
    }

    public String horaTexto() {
        return String.format(java.util.Locale.US, "%02d:%02d", hora, minuto);
    }

    public String cuandoTexto() {
        if (esDiaria()) {
            return "Todos los días a las " + horaTexto();
        }
        return String.format(java.util.Locale.US, "%02d/%02d/%d a las %s", dia, mes + 1, anio, horaTexto());
    }

    /** Opciones que se ofrecen para "avisarme antes". */
    public static final int[] OPCIONES_ANTES = {0, 10, 30, 60, 120};

    /** Nombre de una opción; marca con ⭐ la sugerida para ese tipo de aviso. */
    public static String etiquetaAntes(int minutos, String tipo) {
        if (minutos <= 0) {
            return "No avisar antes";
        }
        String s = antesTexto(minutos) + " antes";
        return minutos == antesPorDefecto(tipo) ? s + "  ⭐ sugerido" : s;
    }

    public static String[] etiquetasAntes(String tipo) {
        String[] r = new String[OPCIONES_ANTES.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = etiquetaAntes(OPCIONES_ANTES[i], tipo);
        }
        return r;
    }

    public static int indiceAntes(int minutos) {
        for (int i = 0; i < OPCIONES_ANTES.length; i++) {
            if (OPCIONES_ANTES[i] == minutos) {
                return i;
            }
        }
        return 0;
    }

    public static String antesTexto(int minutos) {
        if (minutos == 60) {
            return "1 hora";
        }
        if (minutos > 60 && minutos % 60 == 0) {
            return (minutos / 60) + " horas";
        }
        return minutos + " minutos";
    }
}
