package com.mycompany.golite;

import java.util.HashMap;
import java.util.Map;

/**
 * Maneja los scoups de variables durante la ejecución.
 */
public class Entorno {

    // ─────────────────────────────────────────────────────────────────
    // SEÑALES DE CONTROL DE FLUJO

    // ─────────────────────────────────────────────────────────────────

    /** Clase base de todas las señales */
    public static abstract class Senal {}

    /** Señal lanzada por NodoBreak */
    public static class BreakSignal extends Senal {}

    /** Señal lanzada por NodoContinue */
    public static class ContinueSignal extends Senal {}

    /** Señal lanzada por NodoReturn */
    public static class ReturnSignal extends Senal {
        public final Object valor;
        public ReturnSignal(Object valor) { this.valor = valor; }
    }

    // ─────────────────────────────────────────────────────────────────
    // ESTRUCTURA DE VARIABLE
    // ─────────────────────────────────────────────────────────────────

    private static class Variable {
        String tipo;
        Object valor;

        Variable(String tipo, Object valor) {
            this.tipo  = tipo;
            this.valor = valor;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // CAMPOS DEL ENTORNO
    // ─────────────────────────────────────────────────────────────────

    /** Tabla de variables en este scope */
    private final Map<String, Variable> tabla = new HashMap<>();

    /** Entorno padre si es global null */
    private final Entorno padre;

    // ─────────────────────────────────────────────────────────────────
    // CONSTRUCTORES
    // ─────────────────────────────────────────────────────────────────

    /** Constructor para el scope global */
    public Entorno() {
        this.padre = null;
    }

    /** Constructor para un scope hijo */
    public Entorno(Entorno padre) {
        this.padre = padre;
    }

    // ─────────────────────────────────────────────────────────────────
    // DECLARAR VARIABLE
    // Solo se declara en el scope actual
    // ─────────────────────────────────────────────────────────────────

    /**
     * Declara una nueva variable en el scope actual.
     * @param nombre nombre de la variable
     * @param tipo   tipo de la variable
     * @param valor  valor inicial
     */
    public void declarar(String nombre, String tipo, Object valor) {
        tabla.put(nombre, new Variable(tipo, valor));
    }

    // ─────────────────────────────────────────────────────────────────
    // ASIGNAR VARIABLE
    // Busca la variable en el scope actual y papas si hay
    // ─────────────────────────────────────────────────────────────────

    /**
     * Asigna un nuevo valor a una variable ya declarada.
     * Busca en el scope actual y sube hasta el global.
     * @param nombre nombre de la variable
     * @param valor  nuevo valor
     */
    public void asignar(String nombre, Object valor) {
        if (tabla.containsKey(nombre)) {
            tabla.get(nombre).valor = valor;
        } else if (padre != null) {
            padre.asignar(nombre, valor);
        } else {
            throw new RuntimeException(
                "[Error Semántico]: Variable '" + nombre + "' no declarada."
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // OBTENER VALOR
    // ─────────────────────────────────────────────────────────────────

    /**
     * Obtiene el valor de una variable.
     * Busca en el scope actual y sube hasta el global.
     * @param nombre nombre de la variable
     * @return valor de la variable
     */
    public Object obtener(String nombre) {
        if (tabla.containsKey(nombre)) {
            return tabla.get(nombre).valor;
        }
        if (padre != null) {
            return padre.obtener(nombre);
        }
        throw new RuntimeException(
            "[Error Semántico]: Variable '" + nombre + "' no declarada."
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // OBTENER TIPO
    // ─────────────────────────────────────────────────────────────────

    /**
     * Obtiene el tipo declarado de una variable.
     * Busca en el scope actual y sube hasta el global.
     * @param nombre nombre de la variable
     * @return tipo de la variable como String
     */
    public String obtenerTipo(String nombre) {
        if (tabla.containsKey(nombre)) {
            return tabla.get(nombre).tipo;
        }
        if (padre != null) {
            return padre.obtenerTipo(nombre);
        }
        throw new RuntimeException(
            "[Error Semántico]: Variable '" + nombre + "' no declarada."
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // VERIFICAR EXISTENCIA
    // ─────────────────────────────────────────────────────────────────

    /**
     * Verifica si una variable existe en CUALQUIER scope (actual o padres).
     * Se usa para validar acceso a variables.
     * @param nombre nombre de la variable
     * @return true si existe en algún scope
     */
    public boolean existe(String nombre) {
        if (tabla.containsKey(nombre)) return true;
        if (padre != null) return padre.existe(nombre);
        return false;
    }

    /**
     * Verifica si una variable existe SOLO en el scope actual.
     * Se usa para evitar redeclaración en el mismo ámbito.
     * @param nombre nombre de la variable
     * @return true si existe en el scope actual
     */
    public boolean existeEnScopeActual(String nombre) {
        return tabla.containsKey(nombre);
    }

    // ─────────────────────────────────────────────────────────────────
    // UTILIDAD
    // ─────────────────────────────────────────────────────────────────

    /**
     * Muestra el contenido del scope actual
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Entorno{\n");
        for (Map.Entry<String, Variable> entry : tabla.entrySet()) {
            sb.append("  ")
              .append(entry.getKey())
              .append(" (")
              .append(entry.getValue().tipo)
              .append(") = ")
              .append(entry.getValue().valor)
              .append("\n");
        }
        if (padre != null) {
            sb.append("  padre → ").append(padre).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}