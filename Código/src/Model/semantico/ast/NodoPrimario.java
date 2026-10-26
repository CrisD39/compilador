package Model.semantico.ast;

import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// Primer elemento de una cadena de accesos (this, v, m(), new C(), ...),
// con los encadenados de <ReferenciaResto> como lista enlazada (⚠2). El
// último eslabón decide esAsignable() y esLlamable() (§11.5): si hay
// encadenado se le delega, si no responde el primario.
public abstract class NodoPrimario extends NodoOperando {
    protected NodoEncadenado encadenado;

    // El parser lo setea después de construir el primario, porque
    // <ReferenciaResto> se parsea a continuación.
    public void setEncadenado(NodoEncadenado encadenado) {
        this.encadenado = encadenado;
    }

    @Override
    public boolean esAsignable() {
        return encadenado != null ? encadenado.esAsignable() : esAsignableSinEncadenado();
    }

    @Override
    public boolean esLlamable() {
        return encadenado != null ? encadenado.esLlamable() : esLlamableSinEncadenado();
    }

    protected boolean esAsignableSinEncadenado() {
        return false;
    }

    protected boolean esLlamableSinEncadenado() {
        return false;
    }

    // Le pasa al encadenado el tipo del primario; sin encadenado,
    // ése es el tipo de toda la expresión.
    protected Tipo chequearEncadenado(TablaSimbolos tabla, Tipo tipoPrimario) {
        return encadenado != null ? encadenado.chequear(tabla, tipoPrimario) : tipoPrimario;
    }
}
