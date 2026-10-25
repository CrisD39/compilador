package Model.semantico.ast;

import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;

import java.util.ArrayList;
import java.util.List;

// <Bloque>: abre un scope de locales y chequea cada sentencia por separado
// (spec 0003, D7). Un error corta sólo su sentencia: se agrega a la tabla y
// se sigue con la próxima (logro Multi-detección). El scope se cierra en un
// finally: si no, una excepción que no sea ExcepcionSemantica dejaría la
// pila desbalanceada y las locales visibles afuera del bloque.
public class NodoBloque extends NodoSentencia {
    private final List<NodoSentencia> sentencias = new ArrayList<>();

    public void agregarSentencia(NodoSentencia sentencia) {
        sentencias.add(sentencia);
    }

    public List<NodoSentencia> getSentencias() {
        return sentencias;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        tabla.abrirBloque();
        try {
            for (NodoSentencia sentencia : sentencias) {
                try {
                    sentencia.chequear(tabla);
                } catch (ExcepcionSemantica e) {
                    tabla.registrarError(e);
                }
            }
        } finally {
            tabla.cerrarBloque();
        }
    }
}
