package Controller;

import Model.lexico.AnalizadorLexico;
import Model.lexico.AnalizadorLexicoImpl;
import Model.lexico.ErrorLexico;
import Model.lexico.ResultadoLexicoListener;
import Model.lexico.SourceManager;
import Model.lexico.SourceManagerMejorado;
import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.sintactico.AnalizadorSintactico;
import Model.sintactico.AnalizadorSintacticoImpl;

import java.io.IOException;

// Pipeline de la Etapa 4 (spec 0003, D3): el mismo de ET3 y después la
// tercera pasada, TablaSimbolos.chequearSentencias(). Esa pasada corre sólo
// si no hubo errores léxicos, sintácticos ni de declaraciones: con tipos sin
// resolver, herencia cíclica o un AST incompleto por el modo pánico, los
// chequeos de cuerpos darían errores en cascada, y el primer error reportado
// tiene que ser el real.
public class AnalizadorSemantico2HandlerImpl implements AnalizadorSemantico2Handler {

    @Override
    public ResultadoAnalisisSemantico analizar(String rutaArchivo, ResultadoLexicoListener listener) throws IOException {
        SourceManager sourceManager = new SourceManagerMejorado();
        sourceManager.open(rutaArchivo);
        try {
            ListenerQueRegistraErrores listenerLexico = new ListenerQueRegistraErrores(listener);
            AnalizadorLexico lexico = new AnalizadorLexicoImpl(sourceManager, listenerLexico);
            TablaSimbolos tablaSimbolos = new TablaSimbolos();
            AnalizadorSintactico sintactico = new AnalizadorSintacticoImpl(lexico, tablaSimbolos);
            sintactico.start();
            // Igual que en ET3: control de declaraciones y después
            // consolidación (el orden importa, ver
            // TablaSimbolos.estaBienDeclarado()), aun con errores sintácticos.
            tablaSimbolos.estaBienDeclarado();
            tablaSimbolos.consolidar();
            if (!listenerLexico.huboErrores()
                    && sintactico.getErrores().isEmpty()
                    && tablaSimbolos.getErrores().isEmpty()) {
                tablaSimbolos.chequearSentencias();
            }
            return new ResultadoAnalisisSemantico(sintactico.getErrores(), tablaSimbolos.getErrores());
        } finally {
            try {
                sourceManager.close();
            } catch (IOException ignored) {
                // cerrar el fuente no debe tapar el resultado del análisis
            }
        }
    }

    // Los errores léxicos no llegan al resultado: el léxico los empuja
    // directo a la View. Este envoltorio los sigue reenviando sin cambios y
    // sólo anota que hubo alguno, para decidir si corre la tercera pasada.
    private static final class ListenerQueRegistraErrores implements ResultadoLexicoListener {
        private final ResultadoLexicoListener destino;
        private boolean huboErrores = false;

        ListenerQueRegistraErrores(ResultadoLexicoListener destino) {
            this.destino = destino;
        }

        boolean huboErrores() {
            return huboErrores;
        }

        @Override
        public void onToken(Token token) {
            destino.onToken(token);
        }

        @Override
        public void onError(ErrorLexico error) {
            huboErrores = true;
            destino.onError(error);
        }
    }
}
