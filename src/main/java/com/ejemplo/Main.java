package com.ejemplo;

import java_cup.runtime.Symbol;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Clase principal que integra todas las etapas del compilador:
 * - Puntos 3 y 4: Análisis Léxico (JFlex) y Sintáctico (CUP).
 * - Punto 5: Construcción e impresión del Árbol Sintáctico Abstracto (AST).
 * - Punto 6: Análisis Semántico, Tabla de Símbolos e Intérprete (Evaluador) sobre el AST.
 * - Punto 7: Generación de Código Intermedio (C3D) y Seudo-Assembly (salida.s).
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 1. Cargar el archivo de código fuente desde resources/entrada.txt
        InputStream input = Main.class
                .getClassLoader()
                .getResourceAsStream("entrada.txt");

        if (input == null) {
            input = Main.class.getClassLoader().getResourceAsStream("Entrada.txt");
        }

        if (input == null) {
            System.err.println("No se encontró el archivo entrada.txt");
            return;
        }

        // 2. Ejecutar el Lexer y el Parser para obtener la raíz del AST (Puntos 3, 4 y 5)
        Lexer lexer = new Lexer(new InputStreamReader(input));
        parser parser = new parser(lexer);
        Symbol resultado = parser.parse();
        AST ast = (AST) resultado.value;

        System.out.println("--- AST ---");
        ast.imprimir("");

        // 3. Validar variables declaradas, vincular la Tabla de Símbolos al AST y evaluar (Punto 6)
        TablaSimbolos tablaSimbolos = new TablaSimbolos();
        AnalizadorSemantico semantico = new AnalizadorSemantico(tablaSimbolos);
        semantico.analizar(ast);

        Interprete interprete = new Interprete(tablaSimbolos);
        Object retorno = interprete.evaluar(ast);

        System.out.println("\n--- TABLA DE SIMBOLOS ---");
        tablaSimbolos.imprimir();
        if (retorno != null) {
            System.out.println("Retorno: " + retorno);
        }

        // 4. Generar Código de Tres Direcciones y traducirlo a Seudo-Assembly (Punto 7)
        GeneradorC3D generadorC3D = new GeneradorC3D();
        List<InstruccionC3D> codigoC3D = generadorC3D.generar(ast);

        GeneradorAssembly generadorAssembly = new GeneradorAssembly(codigoC3D);
        generadorAssembly.generar();
        generadorAssembly.guardarArchivo("salida.s");

        System.out.println("\n--- ASSEMBLY ---");
        System.out.print(generadorAssembly.obtenerAssembly());
    }
}
