package com.yanmir.applacteossanjose;
public class Producto {
    private int id;
    private String tipoProducto;
    private String fechaProduccion;
    private String lote;            // si quieres mostrar más info
    // private String conservantes;  // opcional

    public Producto(int id, String tipoProducto, String fechaProduccion, String lote) {
        this.id = id;
        this.tipoProducto = tipoProducto;
        this.fechaProduccion = fechaProduccion;
        this.lote = lote;
    }

    public int getId() { return id; }
    public String getTipoProducto() { return tipoProducto; }
    public String getFechaProduccion() { return fechaProduccion; }
    public String getLote() { return lote; }

    @Override
    public String toString() {
        return tipoProducto;  // para el Spinner o logs
    }
}
