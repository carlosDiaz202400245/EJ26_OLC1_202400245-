package com.mycompany.golite;

import java.util.HashMap;
import java.util.Map;

/** Maneja los scopes de variables durante la ejecución. */
public class Entorno {

    // ─── SEÑALES DE CONTROL DE FLUJO ───────────────────────────────────

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

    // ─── ESTRUCTURA DE VARIABLE ────────────────────────────────────────

    private static class Variable {
        String tipo;
        Object valor;

        Variable(String tipo, Object valor) {
            this.tipo  = tipo;
            this.valor = valor;
        }
    }

    // ─── CAMPOS DEL ENTORNO ────────────────────────────────────────────

    /** Tabla de variables de este scope */
    private final Map<String, Variable> tabla = new HashMap<>();

    /** Tabla de funciones declaradas (se registran en el scope global) */
    private final Map<String, Object> funciones = new HashMap<>();

    /** Tabla de definiciones de struct (se registran en el scope global) */
    private final Map<String, Object> structs = new HashMap<>();

    /** Tabla de métodos de struct, con clave "Tipo.metodo" */
    private final Map<String, Object> metodos = new HashMap<>();

    /** Entorno padre (null si es el global) */
    private final Entorno padre;

    // ─── CONSTRUCTORES ─────────────────────────────────────────────────

    /** Constructor para el scope global */
    public Entorno() {
        this.padre = null;
    }

    /** Constructor para un scope hijo */
    public Entorno(Entorno padre) {
        this.padre = padre;
    }

    // ─── DECLARAR VARIABLE ─────────────────────────────────────────────

    /** Declara una variable nueva en el scope actual. */
    public void declarar(String nombre, String tipo, Object valor) {
        tabla.put(nombre, new Variable(tipo, valor));
    }

    // ─── FUNCIONES ─────────────────────────────────────────────────────

    /** Registra una función (objeto NodoFuncion) en este entorno. */
    public void declararFuncion(String nombre, Object funcion) {
        funciones.put(nombre, funcion);
    }

    /** Busca una función en este scope y sube hasta el global; null si no existe. */
    public Object obtenerFuncion(String nombre) {
        if (funciones.containsKey(nombre)) return funciones.get(nombre);
        if (padre != null) return padre.obtenerFuncion(nombre);
        return null;
    }

    /** Registra la definición de un struct (objeto NodoStruct). */
    public void declararStruct(String nombre, Object definicion) {
        structs.put(nombre, definicion);
    }

    /** Busca la definición de un struct; sube hasta el global; null si no existe. */
    public Object obtenerStruct(String nombre) {
        if (structs.containsKey(nombre)) return structs.get(nombre);
        if (padre != null) return padre.obtenerStruct(nombre);
        return null;
    }

    /** Registra un método de struct, identificado por tipo + nombre. */
    public void declararMetodo(String tipo, String nombre, Object funcion) {
        metodos.put(tipo + "." + nombre, funcion);
    }

    /** Busca un método por tipo + nombre; sube hasta el global; null si no existe. */
    public Object obtenerMetodo(String tipo, String nombre) {
        String clave = tipo + "." + nombre;
        if (metodos.containsKey(clave)) return metodos.get(clave);
        if (padre != null) return padre.obtenerMetodo(tipo, nombre);
        return null;
    }

    /** Devuelve el entorno global (la raíz de la cadena de scopes). */
    public Entorno raiz() {
        Entorno e = this;
        while (e.padre != null) e = e.padre;
        return e;
    }

    // ─── ASIGNAR VARIABLE ──────────────────────────────────────────────

    /** Asigna valor a una variable ya declarada; busca desde el scope actual hasta el global. */
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

    // ─── OBTENER VALOR ─────────────────────────────────────────────────

    /** Obtiene el valor de una variable, buscando desde el scoup actual hasta el global. */
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

    // ─── OBTENER TIPO ──────────────────────────────────────────────────

    /** Obtiene el tipo declarado de una variable, buscando hasta el scope global. */
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

    // ─── VERIFICAR EXISTENCIA ──────────────────────────────────────────

    /** true si la variable existe en cualquier scope (actual o padres). */
    public boolean existe(String nombre) {
        if (tabla.containsKey(nombre)) return true;
        if (padre != null) return padre.existe(nombre);
        return false;
    }

    /** true si la variable existe solo en el scope actual; evita redeclaración. */
    public boolean existeEnScopeActual(String nombre) {
        return tabla.containsKey(nombre);
    }

    // ─── UTILIDAD ──────────────────────────────────────────────────────

    /** Muestra el contenido del scoup actual. */
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