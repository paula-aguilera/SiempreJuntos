package com.example.siemprejuntos;

/** Un video que el familiar agrega (título y enlace, por ejemplo de YouTube). */
public class VideoLink {
    public final String titulo;
    public final String url;

    public VideoLink(String titulo, String url) {
        this.titulo = titulo;
        this.url = url;
    }
}
