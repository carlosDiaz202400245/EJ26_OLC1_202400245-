package com.mycompany.golite;

public class TokenReporte {
    public String lexema;
    public String tipo;
    public int linea;
    public int columna;

    public TokenReporte(String lexema, String tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    @Override
    public String toString() {
        return String.format("[%-20s] %-20s línea %d, col %d", tipo, lexema, linea, columna);
    }
}