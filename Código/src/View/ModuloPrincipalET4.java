package View;

import Controller.AnalizadorSemantico2Handler;
import Controller.AnalizadorSemantico2HandlerImpl;
import Controller.ResultadoAnalisisSemantico;
import Model.lexico.ErrorLexico;
import Model.semantico.ErrorSemantico;
import Model.sintactico.ErrorSintactico;
import Model.lexico.ResultadoLexicoListener;
import Model.lexico.Token;

import java.io.IOException;
import java.io.UncheckedIOException;

// Módulo Principal de la etapa 4 (main del jar): corre léxico + sintáctico +
// el chequeo semántico de declaraciones y, si todo eso salió limpio, el de
// sentencias (TablaSimbolos.chequearSentencias(), spec 0003). El wiring vive
// en AnalizadorSemantico2HandlerImpl; acá sólo queda la presentación, con el
// mismo formato de salida que ModuloPrincipalET3, que queda congelado con
// sólo declaraciones para los testers de la Etapa 3.
public final class ModuloPrincipalET4 implements ResultadoLexicoListener {

    private boolean huboErroresLexicos = false;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java -jar Compilador.jar <archivo-fuente>");
            return;
        }
        new ModuloPrincipalET4().ejecutar(args[0]);
    }

    private void ejecutar(String rutaArchivo) {
        AnalizadorSemantico2Handler handler = new AnalizadorSemantico2HandlerImpl();
        try {
            ResultadoAnalisisSemantico resultado = handler.analizar(rutaArchivo, this);

            for (ErrorSintactico error : resultado.getErroresSintacticos()) {
                reportarErrorSintactico(error);
            }
            for (ErrorSemantico error : resultado.getErroresSemanticos()) {
                reportarErrorSemantico(error);
            }

            if (!huboErroresLexicos
                    && resultado.getErroresSintacticos().isEmpty()
                    && resultado.getErroresSemanticos().isEmpty()) {
                System.out.println("[SinErrores]");
            }
        } catch (IOException e) {
            System.out.println("No se pudo abrir el archivo fuente: " + rutaArchivo);
        } catch (UncheckedIOException e) {
            System.out.println("No se pudo leer el archivo fuente: " + rutaArchivo);
        }
    }

    // Formato análogo al error sintáctico de ModuloPrincipalET2: una línea
    // legible y la etiqueta [Error:<lexema>|<linea>] que consumen los testers.
    private void reportarErrorSintactico(ErrorSintactico error) {
        System.out.println("Error Sintáctico en línea " + error.getLinea()
                + ": se esperaba " + error.getEsperado()
                + " y se encontró " + error.getEncontrado() + ".");
        System.out.println("[Error:" + error.getLexema() + "|" + error.getLinea() + "]");
    }

    // Mismo formato que exigen las pautas para todo error: una línea legible y
    // la etiqueta [Error:<lexema>|<linea>]. El código ERR_* es interno (sólo
    // elige la descripción), no se imprime en la etiqueta.
    private void reportarErrorSemantico(ErrorSemantico error) {
        System.out.println("Error Semántico en línea " + error.getLinea()
                + ": " + error.getDescripcion() + ".");
        System.out.println("[Error:" + error.getLexema() + "|" + error.getLinea() + "]");
    }

    // En modo pull el sintáctico consume los tokens uno a uno; no se listan.
    @Override
    public void onToken(Token token) {
    }

    // El léxico sigue empujando sus errores por acá; se informan pero no cortan
    // el análisis (igual que en ModuloPrincipalET2).
    @Override
    public void onError(ErrorLexico error) {
        huboErroresLexicos = true;
        System.out.println("Error Léxico en línea " + error.getLinea() + ": "
                + error.getLexema() + " " + error.getRazon());
        System.out.println("[Error:" + error.getLexema() + "|" + error.getLinea() + "]");
    }
}
