package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ErrorSemantico;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;

// Raíz de las sentencias del AST (requerimientos.md, "NODOS"). El estado del
// chequeo (scopes, clase y unidad actuales) vive en la tabla. Un error corta
// la sentencia con ExcepcionSemantica.
public abstract class NodoSentencia {
    public abstract void chequear(TablaSimbolos tabla);

    // Estado del flujo después de la sentencia, para una entrada VIVO o
    // RECUPERANDO, nunca MUERTO (logro Caminos de retorno, spec 0004, D3).
    // Es estructural: no mira tipos ni la tabla, así que se puede calcular
    // antes de chequear el cuerpo (D4). Por defecto la sentencia termina
    // normalmente (var, local clásica, asignación, llamada, ;, for-each).
    public Flujo salida(Flujo entrada) {
        return entrada;
    }

    // Token sobre el que se reporta la sentencia si es inalcanzable (D7): el
    // primero que el AST guarda de ella.
    public abstract Token primerToken();

    // Una condición de bucle que es el literal true (D3, D6): el bucle no
    // termina normalmente. Sin condición, el for cuenta como true.
    protected static boolean esConstanteTrue(NodoExpresion condicion) {
        return condicion == null || Boolean.TRUE.equals(condicion.valorConstante());
    }

    // Con la constante false el cuerpo del bucle es inalcanzable (D3). Sin
    // condición no: el for cuenta como true.
    protected static boolean esConstanteFalse(NodoExpresion condicion) {
        return condicion != null && Boolean.FALSE.equals(condicion.valorConstante());
    }

    // D5: se registra, no se lanza, así la sentencia se chequea igual (§12.5)
    // y sus errores salen después, en orden de programa.
    protected static void reportarInalcanzable(NodoSentencia sentencia, TablaSimbolos tabla) {
        Token token = sentencia.primerToken();
        tabla.agregarError(new ErrorSemantico("ERR_SENTENCIA_INALCANZABLE", token.getLinea(), token.getLexema()));
    }

    // Cuerpo de if, else, while o for (§12.3, ⚠9): una declaración local no puede ir
    // sola, sin bloque, porque su alcance no tendría sentido. Fuera de esa
    // regla, un cuerpo sin llaves no abre scope: no puede declarar nada. La
    // regla es base y vale también para una local clásica (int x = 1;), antes
    // de su "no soportado" de logro; sin "var", va sobre su primer nombre.
    protected static void chequearCuerpo(NodoSentencia cuerpo, TablaSimbolos tabla) {
        if (cuerpo instanceof NodoVarLocal declaracion) {
            throw new ExcepcionSemantica(declaracion.getVar(), "ERR_DECLARACION_COMO_CUERPO");
        }
        if (cuerpo instanceof NodoVarLocalClasica declaracion) {
            throw new ExcepcionSemantica(declaracion.getPrimerNombre(), "ERR_DECLARACION_COMO_CUERPO");
        }
        cuerpo.chequear(tabla);
    }
}
