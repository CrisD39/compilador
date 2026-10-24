package Controller;

import Model.lexico.ResultadoLexicoListener;

import java.io.IOException;

// Puente entre ModuloPrincipalET4 y el análisis completo de la Etapa 4:
// declaraciones (como AnalizadorSemanticoHandler) y después el chequeo de
// sentencias.
public interface AnalizadorSemantico2Handler {

    ResultadoAnalisisSemantico analizar(String rutaArchivo, ResultadoLexicoListener listener) throws IOException;
}
