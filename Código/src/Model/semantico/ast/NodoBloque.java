package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;

import java.util.ArrayList;
import java.util.List;

// <Bloque>: abre un scope de locales y chequea cada sentencia por separado
// (spec 0003, D7). Un error corta sólo su sentencia: se agrega a la tabla y
// se sigue con la próxima (logro Multi-detección). El scope se cierra en un
// finally: si no, una excepción que no sea ExcepcionSemantica dejaría la
// pila desbalanceada y las locales visibles afuera del bloque. La "{" es el
// lexema del bloque inalcanzable (spec 0004, D7).
public class NodoBloque extends NodoSentencia {
    private final Token llave;
    private final List<NodoSentencia> sentencias = new ArrayList<>();

    public NodoBloque(Token llave) {
        this.llave = llave;
    }

    @Override
    public Token primerToken() {
        return llave;
    }

    public void agregarSentencia(NodoSentencia sentencia) {
        sentencias.add(sentencia);
    }

    public List<NodoSentencia> getSentencias() {
        return sentencias;
    }

    // Logro Caminos de retorno (spec 0004, D5): el bloque lleva el estado del
    // flujo mientras chequea. Una sentencia que llega MUERTO primero se
    // reporta como inalcanzable, después se chequea igual y el tramo sigue
    // RECUPERANDO, así el resto no se reporta en cascada. El estado arranca
    // VIVO aunque el bloque entero sea inalcanzable: que una sentencia salga
    // MUERTO no depende de si entra VIVO o RECUPERANDO, así que el bloque no
    // necesita el estado de quien lo contiene.
    @Override
    public void chequear(TablaSimbolos tabla) {
        tabla.abrirBloque();
        try {
            Flujo estado = Flujo.VIVO;
            for (NodoSentencia sentencia : sentencias) {
                if (estado == Flujo.MUERTO) {
                    reportarInalcanzable(sentencia, tabla);
                    estado = Flujo.RECUPERANDO;
                }
                try {
                    sentencia.chequear(tabla);
                } catch (ExcepcionSemantica e) {
                    tabla.registrarError(e);
                }
                estado = sentencia.salida(estado);
            }
        } finally {
            tabla.cerrarBloque();
        }
    }

    // D3: las sentencias se recorren en orden. Una que llega con el estado
    // MUERTO es inalcanzable, y se sigue desde ella con RECUPERANDO, así el
    // resto del tramo muerto no vuelve a contar. Un bloque vacío sale como
    // entró.
    @Override
    public Flujo salida(Flujo entrada) {
        Flujo estado = entrada;
        for (NodoSentencia sentencia : sentencias) {
            if (estado == Flujo.MUERTO) {
                estado = Flujo.RECUPERANDO;
            }
            estado = sentencia.salida(estado);
        }
        return estado;
    }
}
