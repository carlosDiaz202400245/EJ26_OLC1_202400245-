package com.mycompany.golite;
 
public class ErrorSintactico {
 
    public String descripcion;
    public int    linea;
    public int    columna;
 
    public ErrorSintactico(String descripcion, int linea, int columna) {
        this.descripcion = descripcion;
        this.linea       = linea;
        this.columna     = columna;
    }
 
    @Override
    public String toString() {
        return String.format("Línea %d, Col %d: %s", linea, columna, descripcion);
    }
}
 