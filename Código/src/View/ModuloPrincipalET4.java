package View;

import Controller.AnalizadorSemanticoHandler;
import Controller.AnalizadorSemanticoHandlerImpl;
import Controller.ResultadoAnalisisSemantico;
import Model.lexico.ErrorLexico;
import Model.lexico.Token;
import Model.semantico.ErrorSemantico;
import Model.sintactico.ErrorSintactico;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ModuloPrincipalET4 {
    private boolean huboErroresLexicos = false;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java -jar compilador.jar <archivo-fuente>");
            return;
        }
        new ModuloPrincipalET3().ejecutar(args[0]);
    }

    private void ejecutar(String rutaArchivo) {
        AnalizadorSemanticoHandler handler = new AnalizadorSemanticoHandlerImpl();
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
}
