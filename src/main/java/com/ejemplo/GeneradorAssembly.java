package com.ejemplo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generador de Código Assembly (Punto 7).
 * Toma la lista de instrucciones de Código de Tres Direcciones (C3D) y genera
 * código Assembly x86-64 (sintaxis AT&T/GAS), asignando posiciones en la pila (stack frame)
 * para las variables del programa y los registros temporales.
 */
public class GeneradorAssembly {
    private final List<InstruccionC3D> instrucciones;
    private final StringBuilder asm = new StringBuilder();
    // Mapa que asocia cada variable o temporal con su desplazamiento (offset negativo) respecto a %rbp
    private final Map<String, Integer> offsets = new LinkedHashMap<>();
    private int espacioPila = 0;

    public GeneradorAssembly(List<InstruccionC3D> instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String obtenerAssembly() {
        return asm.toString();
    }

    // Punto 7: Método principal que coordina las 4 etapas de generación de código Assembly
    public void generar() {
        // 1. Preparar offsets de variables y temporales en la pila
        prepararVariables();

        // 2. Prólogo de la función main (armado del stack frame)
        emitirPrologo();

        // 3. Traducir cada instrucción C3D a sus instrucciones equivalentes en Assembly
        for (InstruccionC3D inst : instrucciones) {
            traducirInstruccion(inst);
        }

        // 4. Epílogo de la función main (liberación del stack frame y retorno)
        emitirEpilogo();
    }

    private void prepararVariables() {
        // Asignamos un offset de 4 bytes en la pila (-4(%rbp), -8(%rbp), ...) a cada variable o temporal
        for (InstruccionC3D inst : instrucciones) {
            registrarOperando(inst.getResultado(), inst.getOperacion());
            registrarOperando(inst.getArg1(), inst.getOperacion());
            registrarOperando(inst.getArg2(), inst.getOperacion());
        }
        // Alineamos el tamaño total de la pila a múltiplo de 16 bytes (convención estándar x86-64)
        if (espacioPila % 16 != 0) {
            espacioPila += 16 - (espacioPila % 16);
        }
    }

    /**
     * Si el operando es una variable o un temporal (y no un número constante ni una etiqueta),
     * le reserva 4 bytes nuevos en la pila.
     */
    private void registrarOperando(String op, OperacionC3D tipoOp) {
        if (op == null || esConstante(op)) return;
        if (tipoOp == OperacionC3D.ETIQUETA || tipoOp == OperacionC3D.SALTO_INCONDICIONAL) return;
        if (tipoOp == OperacionC3D.SALTO_FALSO && op.startsWith("L")) return;

        if (!offsets.containsKey(op)) {
            espacioPila += 4;
            offsets.put(op, -espacioPila);
        }
    }

    /**
     * Indica si un operando es una constante numérica entera.
     */
    private boolean esConstante(String op) {
        return op.matches("-?\\d+");
    }

    /**
     * Devuelve la representación en Assembly de un operando:
     * - Si es constante: "$valor" (ej: "$1")
     * - Si es variable o temporal: "-offset(%rbp)" (ej: "-4(%rbp)")
     */
    private String ubicacion(String op) {
        if (esConstante(op)) {
            return "$" + op;
        }
        return offsets.get(op) + "(%rbp)";
    }

    /**
     * Emite el encabezado estándar de la función main y reserva espacio en la pila (%rsp).
     */
    private void emitirPrologo() {
        asm.append(".globl main\n");
        asm.append("main:\n");
        asm.append("    pushq %rbp\n");
        asm.append("    movq %rsp, %rbp\n");
        if (espacioPila > 0) {
            asm.append("    subq $").append(espacioPila).append(", %rsp\n");
        }
    }

    /**
     * Traduce una instrucción individual de C3D a instrucciones Assembly utilizando %eax como acumulador.
     */
    private void traducirInstruccion(InstruccionC3D inst) {
        switch (inst.getOperacion()) {
            case ASIGNACION -> {
                // Cargar arg1 en %eax y guardarlo en la posición de 'resultado'
                asm.append("    movl ").append(ubicacion(inst.getArg1())).append(", %eax\n");
                asm.append("    movl %eax, ").append(ubicacion(inst.getResultado())).append("\n");
            }
            case SUMA -> {
                // Cargar arg1 en %eax, sumarle arg2 y guardar el resultado
                asm.append("    movl ").append(ubicacion(inst.getArg1())).append(", %eax\n");
                asm.append("    addl ").append(ubicacion(inst.getArg2())).append(", %eax\n");
                asm.append("    movl %eax, ").append(ubicacion(inst.getResultado())).append("\n");
            }
            case MULTIPLICACION -> {
                // Cargar arg1 en %eax, multiplicarlo por arg2 y guardar el resultado
                asm.append("    movl ").append(ubicacion(inst.getArg1())).append(", %eax\n");
                asm.append("    imull ").append(ubicacion(inst.getArg2())).append(", %eax\n");
                asm.append("    movl %eax, ").append(ubicacion(inst.getResultado())).append("\n");
            }
            case ETIQUETA -> {
                // Emitir etiqueta de salto (usada en ciclos while)
                asm.append(inst.getResultado()).append(":\n");
            }
            case SALTO_FALSO -> {
                // Comparar la condición con 0 (false); si es igual a 0, saltar a la etiqueta destino
                asm.append("    cmpl $0, ").append(ubicacion(inst.getArg1())).append("\n");
                asm.append("    je ").append(inst.getResultado()).append("\n");
            }
            case SALTO_INCONDICIONAL -> {
                // Salto incondicional al inicio del ciclo while
                asm.append("    jmp ").append(inst.getResultado()).append("\n");
            }
            case RETURN -> {
                // En x86, el valor de retorno de una función se deja en el registro %eax
                if (inst.getArg1() != null) {
                    asm.append("    movl ").append(ubicacion(inst.getArg1())).append(", %eax\n");
                }
                asm.append("    jmp .L_return\n");
            }
        }
    }

    /**
     * Emite el cierre de la función main restaurando la pila con 'leave' y retornando con 'ret'.
     */
    private void emitirEpilogo() {
        asm.append(".L_return:\n");
        asm.append("    leave\n");
        asm.append("    ret\n");
    }

    /**
     * Guarda el código Assembly generado en un archivo de texto (por ejemplo, "salida.s").
     */
    public void guardarArchivo(String ruta) throws IOException {
        try (FileWriter writer = new FileWriter(ruta)) {
            writer.write(asm.toString());
        }
    }
}
