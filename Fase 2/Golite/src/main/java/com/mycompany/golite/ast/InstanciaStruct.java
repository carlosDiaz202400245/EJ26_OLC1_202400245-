package com.mycompany.golite.ast;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Instancia de un struct en tiempo de ejecución: el nombre del tipo y un mapa
 * ordenado campo -> valor (el orden de inserción respeta el orden de declaración).
 */
public class InstanciaStruct {

    public String tipo;
    public Map<String, Object> campos = new LinkedHashMap<>();

    public InstanciaStruct(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean primero = true;
        for (Object v : campos.values()) {
            if (!primero) sb.append(" ");
            sb.append(v);
            primero = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
