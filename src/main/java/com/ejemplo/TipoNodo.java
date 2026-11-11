package com.ejemplo;

/**
 * Enumeración que define todos los tipos posibles de nodos que puede tener
 * nuestro Árbol Sintáctico Abstracto (AST).
 */
public enum TipoNodo {
    PROGRAMA,             // Nodo raíz que representa la función principal main()
    LISTA_DECLARACIONES,  // Nodo contenedor de todas las declaraciones de variables
    DECLARACION,          // Declaración individual de una variable (ej: int x;)
    LISTA_SENTENCIAS,     // Nodo contenedor de una secuencia de sentencias
    ASIGNACION,           // Sentencia de asignación (ej: x = 1;)
    WHILE,                // Sentencia de iteración condicional (ej: while (cond) { ... })
    RETURN,               // Sentencia de retorno de la función (ej: return x; o return;)
    SUMA,                 // Operación aritmética de suma (+)
    MULTIPLICACION,       // Operación aritmética de multiplicación (*)
    IDENTIFICADOR,        // Uso de una variable dentro de una expresión
    NUMERO,               // Constante numérica entera (ej: 1, 2, 3)
    BOOLEANO              // Constante lógica booleana (true o false)
}
