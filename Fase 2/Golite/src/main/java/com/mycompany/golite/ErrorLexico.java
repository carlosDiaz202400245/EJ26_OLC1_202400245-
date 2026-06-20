package com.mycompany.golite;

public class ErrorLexico {
    public String mensaje;
    public int linea;
    public int columna;

    public ErrorLexico(String mensaje, int linea, int columna) {
        this.mensaje = mensaje;
        this.linea = linea;
        this.columna = columna;
    }

    @Override
    public String toString() {
        return String.format("Línea %d, col %d: %s", linea, columna, mensaje);
    }
}