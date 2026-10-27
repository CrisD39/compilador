package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Operando> ::= super <AccesoSuper>   (logro Super!, spec 0006, D4)
// Acceso con super: siempre tiene encadenado (.x o .m(...) como primer
// eslabón, lo exige el parser), porque super suelto no es un valor. Queda
// como receptor en el AST para que la Etapa 5 despache super.m() de forma
// estática, a la versión de la superclase, y no virtual (D12).
public class NodoSuper extends NodoPrimario {
    private final Token tokenSuper;

    public NodoSuper(Token tokenSuper) {
        this.tokenSuper = tokenSuper;
    }

    @Override
    public Token primerToken() {
        return tokenSuper;
    }

    // D6: sólo vale donde vale this (un método estático no tiene objeto, y
    // en los argumentos de super(...) todavía no está construido, D9). De
    // ahí en más es un receptor de tipo superclase y el encadenado de siempre
    // hace el resto: miembros heredados, sustitución genérica, visibilidad
    // (un private de la superclase no es de la clase actual) y
    // asignabilidad. Un miembro que sólo declara la clase actual o una
    // interfaz implementada no está en la superclase: miembro inexistente.
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        if (tabla.enContextoEstatico()) {
            throw new ExcepcionSemantica(tokenSuper, "ERR_SUPER_EN_ESTATICO");
        }
        if (tabla.enContextoPrevioAConstruccion()) {
            throw new ExcepcionSemantica(tokenSuper, "ERR_REFERENCIA_ANTES_DE_SUPER");
        }
        return chequearEncadenado(tabla, tabla.getClaseActual().getTipoSuperclase());
    }
}
