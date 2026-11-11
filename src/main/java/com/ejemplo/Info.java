package com.ejemplo;

/**
 * Representa la información de una variable almacenada en la Tabla de Símbolos.
 * Guarda su nombre, su tipo de dato ("int" o "bool") y su valor actual durante la ejecución.
 */
public class Info {
    private final String nombre; // Nombre del identificador (ej: "x")
    private final String tipo;   // Tipo de dato declarado ("int" o "bool")
    private Object valor;        // Valor actual de la variable en el intérprete (Integer o Boolean)

    public Info(String nombre, String tipo, Object valor) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + " = " + valor + ")";
    }
}
