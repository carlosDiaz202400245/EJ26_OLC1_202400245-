package com.mycompany.golite.ast;

import java.util.List;

/**
 * Nodo que representa la función embebida fmt.Println.
 *
 * Características del archivo del aux
 *   - Acepta 0 o más expresiones separadas por coma
 *   - Los elementos se imprimen separados por un espacio
 *   - Siempre imprime un salto de línea al final
 *   - Si no hay argumentos imprime solo un salto de línea

 */
public class NodoPrintln extends Nodo {

    /** Lista de expresiones a imprimir */
    public List<Nodo> argumentos;

    /** Referencia a la consola de la GUI para imprimir ahí */
    public static com.mycompany.golite.gui.ConsolePanel consola;

    /**
     * @param argumentos lista de expresiones a imprimir
     * @param linea      línea en el código fuente
     * @param columna    columna en el código fuente
     */
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

    // ─────────────────────────────────────────────────────────────────
    // FORMATEAR VALOR PARA IMPRIMIR
    // ─────────────────────────────────────────────────────────────────
    private String formatear(Object valor) {
        if (valor == null)            return "nil";
        if (valor instanceof Boolean) return valor.toString();
        if (valor instanceof Integer) return valor.toString();
        if (valor instanceof Double)  return formatearDouble((Double) valor);
        if (valor instanceof String)  return (String) valor;
        return valor.toString();
    }

    /**
     * Formatea un Double igual que Go
     */
    private String formatearDouble(Double d) {
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            // Es un número entero guardado como double: 1.0 = "1"
            // Pero si fue declarado float64 lo mostramos con decimales: 1.00001 segun la regla
            return String.valueOf(d);
        }
        return String.valueOf(d);
    }

    @Override
    public String toString() {
        return "Println(" + argumentos + ")";
    }
}