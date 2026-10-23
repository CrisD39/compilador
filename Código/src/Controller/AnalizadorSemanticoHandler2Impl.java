package Controller;

import Model.lexico.*;
import Model.semantico.TablaSimbolos;
import Model.sintactico.AnalizadorSintactico;
import Model.sintactico.AnalizadorSintacticoImpl;

import java.io.IOException;

public class AnalizadorSemanticoHandler2Impl {
    @Override
    public ResultadoAnalisisSemantico analizar(String rutaArchivo, ResultadoLexicoListener listener) throws IOException {
        SourceManager sourceManager = new SourceManagerMejorado();
        sourceManager.open(rutaArchivo);
        try {
            AnalizadorLexico lexico = new AnalizadorLexicoImpl(sourceManager, listener);
            TablaSimbolos tablaSimbolos = new TablaSimbolos();
            AnalizadorSintactico sintactico = new AnalizadorSintacticoImpl(lexico, tablaSimbolos);
            sintactico.start();
            // Chequeo de corrección: primero el control de declaraciones y
            // después la consolidación (el orden importa, ver
            // TablaSimbolos.estaBienDeclarado()). Corren siempre, aun si hubo
            // errores sintácticos -- operan sobre lo que efectivamente se
            // llegó a registrar en la tabla.
            tablaSimbolos.estaBienDeclarado();
            tablaSimbolos.consolidar();
            //Chequeo de sentencias.
            //TODO: configurar el handler para los reportes de errores
            return new ResultadoAnalisisSemantico(sintactico.getErrores(), tablaSimbolos.getErrores());
        } finally {
            try {
                sourceManager.close();
            } catch (IOException ignored) {
                // cerrar el fuente no debe tapar el resultado del análisis
            }
        }
    }
}
}
