package com.ejemplo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tabla de Símbolos del compilador/intérprete (Punto 6).
 * Mantiene un diccionario con todas las variables declaradas en el programa,
 * permitiendo verificar que no se declaren dos veces y que toda variable usada exista.
 */
public class TablaSimbolos {
    // Usamos LinkedHashMap para mantener el orden en que fueron declaradas las variables
    private final Map<String, Info> simbolos = new LinkedHashMap<>();

    /**
     * Registra una nueva variable en la tabla de símbolos.
     * Si la variable ya existe, lanza un error semántico.
     */
    public Info declarar(String nombre, String tipo) {
        if (simbolos.containsKey(nombre)) {
            throw new RuntimeException("Error semántico: La variable '" + nombre + "' ya fue declarada.");
        }
        // Valor inicial por defecto: 0 para enteros y false para booleanos
        Object valorInicial = "int".equals(tipo) ? 0 : Boolean.FALSE;
        Info info = new Info(nombre, tipo, valorInicial);
        simbolos.put(nombre, info);
        return info;
    }

    /**
     * Busca y retorna la información de una variable por su nombre.
     * Si la variable no fue declarada previamente, lanza un error semántico.
     */
    public Info buscar(String nombre) {
        Info info = simbolos.get(nombre);
        if (info == null) {
            throw new RuntimeException("Error semántico: La variable '" + nombre + "' no se encuentra declarada.");
        }
        return info;
    }

    /**
     * Verifica si una variable está registrada en la tabla.
     */
    public boolean existe(String nombre) {
        return simbolos.containsKey(nombre);
    }

    /**
     * Devuelve todas las entradas almacenadas en la tabla de símbolos.
     */
    public Collection<Info> obtenerTodos() {
        return simbolos.values();
    }

    /**
     * Imprime por consola el estado actual de todas las variables de la tabla.
     */
    public void imprimir() {
        for (Info info : simbolos.values()) {
            System.out.println("  " + info.getNombre() + " : " + info.getTipo() + " = " + info.getValor());
        }
    }
}
