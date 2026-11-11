package com.ejemplo;

import java.util.ArrayList;
import java.util.List;

/**
 * Generador de Código de Tres Direcciones (C3D).
 * Recorre el AST y lo aplana en una lista secuencial de instrucciones elementales,
 * creando variables temporales (t0, t1, ...) para subexpresiones y etiquetas (L0, L1, ...)
 * para controlar el flujo del ciclo 'while'.
 */
public class GeneradorC3D {
    private final List<InstruccionC3D> instrucciones = new ArrayList<>();
    private int contadorTemporales = 0; // Contador para generar t0, t1, t2...
    private int contadorEtiquetas = 0;  // Contador para generar L0, L1, L2...

    /**
     * Crea y devuelve el nombre de una nueva variable temporal única.
     */
    private String nuevoTemporal() {
        return "t" + (contadorTemporales++);
    }

    /**
     * Crea y devuelve el nombre de una nueva etiqueta de salto única.
     */
    private String nuevaEtiqueta() {
        return "L" + (contadorEtiquetas++);
    }

    /**
     * Recorre todo el AST desde la raíz y retorna la lista de instrucciones C3D generadas.
     */
    public List<InstruccionC3D> generar(AST raiz) {
        procesarNodo(raiz);
        return instrucciones;
    }

    /**
     * Traduce recursivamente los nodos de sentencias del AST a instrucciones C3D.
     */
    private void procesarNodo(AST nodo) {
        if (nodo == null) return;

        switch (nodo.getTipoNodo()) {
            case PROGRAMA, LISTA_DECLARACIONES, LISTA_SENTENCIAS -> {
                for (AST hijo : nodo.getHijos()) {
                    procesarNodo(hijo);
                }
            }
            case ASIGNACION -> {
                // Primero generamos el código para calcular la expresión derecha
                String valor = procesarExpresion(nodo.getIzq());
                // Luego asignamos el resultado a la variable de destino
                instrucciones.add(new InstruccionC3D(OperacionC3D.ASIGNACION, nodo.getNombre(), valor, null));
            }
            case WHILE -> {
                // Creamos dos etiquetas: una para volver a evaluar la condición y otra para salir del bucle
                String etiqInicio = nuevaEtiqueta();
                String etiqFin = nuevaEtiqueta();

                // 1. Etiqueta de inicio del ciclo while
                instrucciones.add(new InstruccionC3D(OperacionC3D.ETIQUETA, etiqInicio, null, null));
                // 2. Evaluar la condición del while
                String cond = procesarExpresion(nodo.getIzq());
                // 3. Si la condición es falsa (0), saltar a la etiqueta de fin
                instrucciones.add(new InstruccionC3D(OperacionC3D.SALTO_FALSO, etiqFin, cond, null));

                // 4. Generar las instrucciones del cuerpo del while
                procesarNodo(nodo.getDer());

                // 5. Al terminar el cuerpo, volver incondicionalmente al inicio para reevaluar la condición
                instrucciones.add(new InstruccionC3D(OperacionC3D.SALTO_INCONDICIONAL, etiqInicio, null, null));
                // 6. Etiqueta de salida del while
                instrucciones.add(new InstruccionC3D(OperacionC3D.ETIQUETA, etiqFin, null, null));
            }
            case RETURN -> {
                if (nodo.getIzq() != null) {
                    String val = procesarExpresion(nodo.getIzq());
                    instrucciones.add(new InstruccionC3D(OperacionC3D.RETURN, null, val, null));
                } else {
                    instrucciones.add(new InstruccionC3D(OperacionC3D.RETURN, null, null, null));
                }
            }
            default -> {}
        }
    }

    /**
     * Traduce recursivamente una expresión del AST y devuelve el operando (constante, variable o temporal)
     * donde quedó almacenado su resultado.
     */
    private String procesarExpresion(AST expr) {
        return switch (expr.getTipoNodo()) {
            case NUMERO -> String.valueOf(expr.getValor());
            // Representamos true como "1" y false como "0" para nivel de código intermedio/assembly
            case BOOLEANO -> Boolean.TRUE.equals(expr.getValor()) ? "1" : "0";
            case IDENTIFICADOR -> expr.getNombre();
            case SUMA -> {
                String izq = procesarExpresion(expr.getIzq());
                String der = procesarExpresion(expr.getDer());
                String temp = nuevoTemporal();
                instrucciones.add(new InstruccionC3D(OperacionC3D.SUMA, temp, izq, der));
                yield temp;
            }
            case MULTIPLICACION -> {
                String izq = procesarExpresion(expr.getIzq());
                String der = procesarExpresion(expr.getDer());
                String temp = nuevoTemporal();
                instrucciones.add(new InstruccionC3D(OperacionC3D.MULTIPLICACION, temp, izq, der));
                yield temp;
            }
            default -> throw new RuntimeException("Expresión no soportada en C3D: " + expr.getTipoNodo());
        };
    }

    public List<InstruccionC3D> getInstrucciones() {
        return instrucciones;
    }
}
