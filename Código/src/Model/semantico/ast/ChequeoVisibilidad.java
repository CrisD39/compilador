package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Clase;
import Model.semantico.EntidadDeclarada;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;

// Logro Visibilidad chequeada: lo comparten los seis accesos a un miembro
// (x, m(), e.x, e.m(), C.m() y new C()). Cada nodo lo llama apenas resuelve
// el miembro, antes de mirar static o argumentos: como en Java, un private
// inaccesible es un error de resolución, y gana sobre los que vienen después.
// La Etapa 3 no cambia: los private se copian a las subclases y cuentan en
// redefiniciones y contratos, y acá sólo se controla quién los usa.
final class ChequeoVisibilidad {
    private ChequeoVisibilidad() {
    }

    // Un miembro private sólo se usa desde el código de su clase declarante,
    // y a través de un receptor de esa misma clase. Lo segundo es porque en
    // Java un private no se hereda: dentro de A1, b.x con b de tipo B2
    // (subclase de A1) es error, aunque this.x y otraA1.x valgan. En un
    // acceso implícito (x, m()) el receptor es la clase actual, y en C.m()
    // y new C() es C. El error va sobre el nombre del miembro o, en new, de
    // la clase.
    static void exigirAcceso(TablaSimbolos tabla, boolean privado, Clase declarante,
                             EntidadDeclarada receptor, Token nombre) {
        exigirAcceso(tabla, privado, declarante, receptor, nombre, null);
    }

    // Para el constructor de new y de super(...): 'detalle' va en el error y
    // elige su descripción (ver ChequeoLlamada.chequearConstructor()).
    static void exigirAcceso(TablaSimbolos tabla, boolean privado, Clase declarante,
                             EntidadDeclarada receptor, Token nombre, String detalle) {
        if (privado && (tabla.getClaseActual() != declarante || receptor != declarante)) {
            throw new ExcepcionSemantica(nombre, "ERR_ACCESO_PRIVADO", detalle);
        }
    }
}
