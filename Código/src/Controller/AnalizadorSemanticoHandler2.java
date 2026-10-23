package Controller;

import Model.lexico.*;
import Model.semantico.TablaSimbolos;
import Model.sintactico.AnalizadorSintactico;
import Model.sintactico.AnalizadorSintacticoImpl;

import java.io.IOException;

public interface AnalizadorSemanticoHandler2 {
    ResultadoAnalisisSemantico analizar(String rutaArchivo, ResultadoLexicoListener listener) throws IOException;
}

