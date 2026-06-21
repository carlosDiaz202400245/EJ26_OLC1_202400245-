package com.mycompany.golite.ast;

import java.util.List;

/**
 * Nodo de la función embebida fmt.Println
 */
public class NodoPrintln extends Nodo {

    public List<Nodo> argumentos;

    /** Consola de la GUI donde se imprime. */
    public static com.mycompany.golite.gui.ConsolePanel consola;

    public NodoPrintln(List<Nodo> argumentos, int linea, int columna) {
        super(linea, columna);
        this.argumentos = argumentos;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < argumentos.size(); i++) {
            Object valor = argumentos.get(i).ejecutar(entorno);

            // Separar elementos con espacio
            if (i > 0) sb.append(" ");

            sb.append(formatear(valor));
        }

        String salida = sb.toString();

        // Imprimir en consola GUI 
        if (consola != null) {
            consola.println(salida);
        } else {
            System.out.println(salida);
        }

        return null;
    }

    // ─── FORMATEAR VALOR PARA IMPRIMIR (estilo Go) ─────────────────────
    private String formatear(Object valor) {
        if (valor == null)            return "nil";
        if (valor instanceof Boolean) return valor.toString();
        if (valor instanceof Integer) return valor.toString();
        if (valor instanceof Double)  return formatearDouble((Double) valor);
        if (valor instanceof String)  return (String) valor;
        if (valor instanceof List)    return formatearSlice((List<?>) valor);
        return valor.toString();
    }

    /** Formatea un slice estilo Go: [a b c], con espacios y recursivo para matrices. */
    private String formatearSlice(List<?> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(formatear(lista.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    /** Formatea un Double estilo Go: 1.0 -> "1", 1.5 -> "1.5". */
    private String formatearDouble(Double d) {
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return String.valueOf((long) d.doubleValue());
        }
        return String.valueOf(d);
    }

    @Override
    public String toString() {
        return "Println(" + argumentos + ")";
    }
}