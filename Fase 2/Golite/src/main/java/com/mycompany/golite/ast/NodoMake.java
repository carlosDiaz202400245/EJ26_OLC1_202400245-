package com.mycompany.golite.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Creación de slice con make: make([]int, n). Devuelve un slice de tamaño n
 * inicializado con el valor por defecto del tipo de elemento.
 */
public class NodoMake extends Nodo {

    public String tipoElemento;
    public Nodo tamano;

    public NodoMake(String tipoElemento, Nodo tamano, int linea, int columna) {
        super(linea, columna);
        this.tipoElemento = tipoElemento;
        this.tamano       = tamano;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object n = tamano.ejecutar(entorno);
        if (!(n instanceof Integer)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el tamaño de make debe ser int.");
        }
        int size = (Integer) n;
        if (size < 0) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el tamaño de make no puede ser negativo.");
        }
        List<Object> slice = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            slice.add(valorPorDefecto(tipoElemento));
        }
        return slice;
    }

    static Object valorPorDefecto(String tipo) {
        if (tipo == null) return null;
        switch (tipo) {
            case "int":     return 0;
            case "float64": return 0.0;
            case "string":  return "";
            case "bool":    return false;
            case "rune":    return 0;
            default:        return null;   // slices anidados u otros → nil
        }
    }

    @Override
    public String toString() {
        return "make([]" + tipoElemento + ", " + tamano + ")";
    }
}
