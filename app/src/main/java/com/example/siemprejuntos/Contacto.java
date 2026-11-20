package com.example.siemprejuntos;

/** Contacto de confianza (familiar, amigo o cuidador). */
public class Contacto {
    public final String nombre;
    public final String relacion;
    public final String telefono;

    public Contacto(String nombre, String relacion, String telefono) {
        this.nombre = nombre;
        this.relacion = relacion;
        this.telefono = telefono;
    }
}
