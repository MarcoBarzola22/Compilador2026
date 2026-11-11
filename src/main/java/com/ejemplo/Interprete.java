package com.ejemplo;

/**
 * Intérprete / Evaluador del lenguaje sobre el AST (Punto 6).
 * Recorre el AST ejecutando las asignaciones, los ciclos while y la sentencia return,
 * actualizando los valores de las variables directamente en la Tabla de Símbolos.
 */
public class Interprete {
    private static final int MAX_ITERACIONES_WHILE = 10000; // Protección contra bucles infinitos
    private final TablaSimbolos tablaSimbolos;
    private Object valorRetorno = null;     // Guarda el valor devuelto por la sentencia return
    private boolean retornoEjecutado = false; // Indica si ya se ejecutó un return para detener main

    public Interprete(TablaSimbolos tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }

    /**
     * Inicia la evaluación del programa desde la raíz del AST y devuelve el resultado de main.
     */
    public Object evaluar(AST raiz) {
        ejecutarNodo(raiz);
        return valorRetorno;
    }

    /**
     * Ejecuta recursivamente las sentencias del AST.
     */
    private void ejecutarNodo(AST nodo) {
        // Si no hay nodo o ya se ejecutó un return, no seguimos ejecutando más sentencias
        if (nodo == null || retornoEjecutado) return;

        switch (nodo.getTipoNodo()) {
            case PROGRAMA, LISTA_DECLARACIONES, LISTA_SENTENCIAS -> {
                for (AST hijo : nodo.getHijos()) {
                    ejecutarNodo(hijo);
                }
            }
            case DECLARACION -> {
                // Si el nodo aún no estaba vinculado a la tabla de símbolos, lo registramos
                if (nodo.getInfoSimbolo() == null) {
                    Info info = tablaSimbolos.declarar(nodo.getNombre(), nodo.getTipoDato());
                    nodo.setInfoSimbolo(info);
                }
            }
            case ASIGNACION -> {
                // Obtenemos la entrada de la variable desde la referencia guardada en el AST
                Info info = nodo.getInfoSimbolo() != null
                        ? nodo.getInfoSimbolo()
                        : tablaSimbolos.buscar(nodo.getNombre());
                // Evaluamos la expresión de la derecha y actualizamos el valor de la variable
                Object val = evaluarExpresion(nodo.getIzq());
                info.setValor(val);
            }
            case WHILE -> {
                int iteraciones = 0;
                // Mientras la condición (hijo izquierdo) se evalúe como true, ejecutamos el cuerpo (hijo derecho)
                while (Boolean.TRUE.equals(evaluarExpresion(nodo.getIzq()))) {
                    if (++iteraciones > MAX_ITERACIONES_WHILE) {
                        throw new RuntimeException("Límite de iteraciones alcanzado en sentencia while.");
                    }
                    ejecutarNodo(nodo.getDer());
                    if (retornoEjecutado) break;
                }
            }
            case RETURN -> {
                // Si el return tiene una expresión asociada, la evaluamos y guardamos su resultado
                if (nodo.getIzq() != null) {
                    valorRetorno = evaluarExpresion(nodo.getIzq());
                }
                retornoEjecutado = true;
            }
            default -> {}
        }
    }

    /**
     * Evalúa recursivamente un nodo de expresión y retorna su resultado (Integer o Boolean).
     */
    public Object evaluarExpresion(AST expr) {
        return switch (expr.getTipoNodo()) {
            case NUMERO, BOOLEANO -> expr.getValor(); // Hojas constantes: retornan su valor literal
            case IDENTIFICADOR -> {
                // Leemos el valor actual de la variable desde su referencia en la Tabla de Símbolos
                Info info = expr.getInfoSimbolo() != null
                        ? expr.getInfoSimbolo()
                        : tablaSimbolos.buscar(expr.getNombre());
                yield info.getValor();
            }
            case SUMA -> {
                // Evaluamos subárbol izquierdo y derecho, y sumamos sus resultados
                int izq = (Integer) evaluarExpresion(expr.getIzq());
                int der = (Integer) evaluarExpresion(expr.getDer());
                yield izq + der;
            }
            case MULTIPLICACION -> {
                // Evaluamos subárbol izquierdo y derecho, y multiplicamos sus resultados
                int izq = (Integer) evaluarExpresion(expr.getIzq());
                int der = (Integer) evaluarExpresion(expr.getDer());
                yield izq * der;
            }
            default -> throw new RuntimeException("Nodo de expresión desconocido: " + expr.getTipoNodo());
        };
    }
}
