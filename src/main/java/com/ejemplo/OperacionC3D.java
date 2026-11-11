package com.ejemplo;

/**
 * Enumeración de las operaciones posibles en el Código de Tres Direcciones (C3D).
 * Sirve como representación intermedia entre el AST y el código Assembly final.
 */
public enum OperacionC3D {
    ASIGNACION,          // Copia simple: resultado = arg1
    SUMA,                // Suma binaria: resultado = arg1 + arg2
    MULTIPLICACION,      // Multiplicación binaria: resultado = arg1 * arg2
    ETIQUETA,            // Define un punto de salto en el código (ej: L0:, L1:)
    SALTO_FALSO,         // Salto condicional: si arg1 es falso (0), salta a la etiqueta 'resultado'
    SALTO_INCONDICIONAL, // Salto directo (goto) hacia la etiqueta 'resultado'
    RETURN               // Retorno de la función (con o sin valor en arg1)
}
