package com.example.proyecto_das;

public class Ejercicio {
    private int imagen;
    private String imagenUri;
    private String nombre;
    private String descripcion;
    private float valoracion;

    public Ejercicio(String nombre, String descripcion, int imagen, String imagenUri) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.valoracion = 0;
        this.imagenUri = imagenUri;
    }

    public Ejercicio(String nombre, String descripcion, int imagen, String imagenUri, float valoracion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.imagenUri = imagenUri;
        this.valoracion = valoracion;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public String getNombre() {
        return nombre;
    }

    public void imasetNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }
}
