package com.ejemplo;

/**
 * Analizador Semántico (Punto 6 - Nota).
 * Recorre el AST antes de la ejecución para:
 * 1. Registrar las variables declaradas en la Tabla de Símbolos.
 * 2. Verificar que toda variable usada esté previamente declarada.
 * 3. Guardar en cada nodo del AST la referencia (Info) a la Tabla de Símbolos.
 * 4. Comprobar la compatibilidad de tipos en asignaciones, expresiones, while y return.
 */
public class AnalizadorSemantico {
    private final TablaSimbolos tablaSimbolos;
    private String tipoRetornoMain; // Guarda si main es "int", "bool" o "void"

    public AnalizadorSemantico(TablaSimbolos tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }

    /**
     * Recorre recursivamente los nodos de sentencias y declaraciones del AST.
     */
    public void analizar(AST nodo) {
        if (nodo == null) return;

        switch (nodo.getTipoNodo()) {
            case PROGRAMA -> {
                // Guardamos el tipo de retorno declarado para la función main
                this.tipoRetornoMain = nodo.getTipoDato();
                for (AST hijo : nodo.getHijos()) {
                    analizar(hijo);
                }
            }
            case LISTA_DECLARACIONES, LISTA_SENTENCIAS -> {
                // Analizamos cada declaración o sentencia de la lista en orden
                for (AST hijo : nodo.getHijos()) {
                    analizar(hijo);
                }
            }
            case DECLARACION -> {
                // Damos de alta la variable en la tabla y guardamos la referencia en el nodo AST
                Info info = tablaSimbolos.declarar(nodo.getNombre(), nodo.getTipoDato());
                nodo.setInfoSimbolo(info);
            }
            case ASIGNACION -> {
                // Verificamos que la variable exista en la tabla y la referenciamos desde el AST
                Info info = tablaSimbolos.buscar(nodo.getNombre());
                nodo.setInfoSimbolo(info);

                // Verificamos que el tipo de la expresión coincida con el tipo de la variable
                String tipoExpr = inferirTipoExpresion(nodo.getIzq());
                if (!info.getTipo().equals(tipoExpr)) {
                    throw new RuntimeException(
                        "Error semántico: No se puede asignar una expresión de tipo '" + tipoExpr +
                        "' a la variable '" + nodo.getNombre() + "' de tipo '" + info.getTipo() + "'."
                    );
                }
            }
            case WHILE -> {
                // En un while, el hijo izquierdo es la condición y debe ser de tipo booleano (bool)
                String tipoCond = inferirTipoExpresion(nodo.getIzq());
                if (!"bool".equals(tipoCond)) {
                    throw new RuntimeException(
                        "Error semántico: La condición del 'while' debe ser de tipo 'bool', pero se encontró '" + tipoCond + "'."
                    );
                }
                // Analizamos semánticamente las sentencias dentro del cuerpo del while (hijo derecho)
                analizar(nodo.getDer());
            }
            case RETURN -> {
                if (nodo.getIzq() == null) {
                    // Caso "return;" sin expresión: solo válido si main es void
                    if (!"void".equals(tipoRetornoMain)) {
                        throw new RuntimeException(
                            "Error semántico: La función main debe retornar un valor de tipo '" + tipoRetornoMain + "'."
                        );
                    }
                } else {
                    // Caso "return <expr>;": validamos que el tipo coincida con el retorno de main
                    String tipoExpr = inferirTipoExpresion(nodo.getIzq());
                    if ("void".equals(tipoRetornoMain)) {
                        throw new RuntimeException(
                            "Error semántico: Una función 'void main' no puede retornar un valor."
                        );
                    }
                    if (!tipoRetornoMain.equals(tipoExpr)) {
                        throw new RuntimeException(
                            "Error semántico: El tipo de retorno '" + tipoExpr +
                            "' no coincide con el tipo de main ('" + tipoRetornoMain + "')."
                        );
                    }
                }
            }
            default -> {}
        }
    }

    /**
     * Evalúa estáticamente el tipo de dato ("int" o "bool") de una expresión
     * y vincula los identificadores usados con su entrada en la Tabla de Símbolos.
     */
    private String inferirTipoExpresion(AST expr) {
        return switch (expr.getTipoNodo()) {
            case NUMERO -> "int";
            case BOOLEANO -> "bool";
            case IDENTIFICADOR -> {
                // Verifica que la variable usada en la expresión haya sido declarada
                Info info = tablaSimbolos.buscar(expr.getNombre());
                // Guarda la referencia a la Tabla de Símbolos en el nodo del AST
                expr.setInfoSimbolo(info);
                yield info.getTipo();
            }
            case SUMA, MULTIPLICACION -> {
                // Tanto la suma como la multiplicación requieren que ambos operandos sean enteros
                String tIzq = inferirTipoExpresion(expr.getIzq());
                String tDer = inferirTipoExpresion(expr.getDer());
                if (!"int".equals(tIzq) || !"int".equals(tDer)) {
                    throw new RuntimeException(
                        "Error semántico: Los operadores aritméticos requieren operandos de tipo 'int'."
                    );
                }
                yield "int";
            }
            default -> throw new RuntimeException("Expresión no válida: " + expr.getTipoNodo());
        };
    }
}
