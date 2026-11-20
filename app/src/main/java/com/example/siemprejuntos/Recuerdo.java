package com.example.siemprejuntos;

/** Un recuerdo: una foto o un audio elegido por el familiar. */
public class Recuerdo {
    public static final String FOTO = "FOTO";
    public static final String AUDIO = "AUDIO";

    public final String tipo;
    public final String uri;
    public final String titulo;

    public Recuerdo(String tipo, String uri, String titulo) {
        this.tipo = tipo;
        this.uri = uri;
        this.titulo = titulo;
    }
}
