package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <For> en su forma clásica (⚠9). La inicialización es una sentencia
// (NodoVarLocal, NodoVarLocalClasica o NodoSentenciaExpresion), y las tres
// cláusulas pueden ser null (for (;;)). El cuerpo se setea aparte porque el
// parser lo lee después de cerrar las cláusulas.
public class NodoFor extends NodoSentencia {
    private final Token tokenFor;
    private final NodoSentencia inicializacion;
    private final NodoExpresion condicion;
    private final NodoExpresion actualizacion;
    private NodoSentencia cuerpo;

    public NodoFor(Token tokenFor, NodoSentencia inicializacion, NodoExpresion condicion,
                   NodoExpresion actualizacion) {
        this.tokenFor = tokenFor;
        this.inicializacion = inicializacion;
        this.condicion = condicion;
        this.actualizacion = actualizacion;
    }

    public void setCuerpo(NodoSentencia cuerpo) {
        this.cuerpo = cuerpo;
    }

    // Logro For: la inicialización es una asignación o un var (no una local
    // clásica, ni una llamada), la condición es boolean y la actualización es
    // una asignación (no un postfijo ni una llamada). Una cláusula vacía no
    // es un componente mal puesto: for (;;) vale, como en Java. Los errores
    // propios del for van sobre "for", como la condición de if/while.
    // La variable de la inicialización vive en un scope propio que cubre las
    // cláusulas y el cuerpo, y se cierra en el finally: si no, una excepción
    // a mitad de camino deja la local visible después del for (⚠9).
    @Override
    public void chequear(TablaSimbolos tabla) {
        tabla.abrirBloque();
        try {
            if (inicializacion != null) {
                if (!(inicializacion instanceof NodoVarLocal || esAsignacion(inicializacion))) {
                    throw new ExcepcionSemantica(tokenFor, "ERR_FOR_INICIALIZACION");
                }
                inicializacion.chequear(tabla);
            }
            if (condicion != null && !Tipo.BOOLEAN.equals(condicion.chequear(tabla))) {
                throw new ExcepcionSemantica(tokenFor, "ERR_CONDICION_NO_BOOLEAN");
            }
            if (actualizacion != null) {
                if (!(actualizacion instanceof NodoExpAsignacion)) {
                    throw new ExcepcionSemantica(tokenFor, "ERR_FOR_ACTUALIZACION");
                }
                actualizacion.chequear(tabla);
            }
            chequearCuerpo(cuerpo, tabla);
        } finally {
            tabla.cerrarBloque();
        }
    }

    // Una asignación como sentencia llega envuelta en NodoSentenciaExpresion.
    private static boolean esAsignacion(NodoSentencia sentencia) {
        return sentencia instanceof NodoSentenciaExpresion sentenciaExpresion
                && sentenciaExpresion.getExpresion() instanceof NodoExpAsignacion;
    }
}
