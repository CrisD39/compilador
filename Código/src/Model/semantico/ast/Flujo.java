package Model.semantico.ast;

// Estado del flujo de ejecución en un punto de la unidad (spec 0004, D3),
// copiado de la Liveness de javac. VIVO: alguna ejecución llega. MUERTO:
// ninguna llega, así que la próxima sentencia es inalcanzable. RECUPERANDO:
// ninguna llega, pero eso ya se reportó en una sentencia anterior del mismo
// tramo; sirve para no reportar en cascada (otra inalcanzable, o la falta de
// retorno de D4).
public enum Flujo {
    VIVO, MUERTO, RECUPERANDO;

    // ⊔ de D3, para juntar dos caminos (las ramas de un if): vivo si alguno
    // es vivo; si no, recuperando si alguno lo es; si no, muerto.
    public Flujo unir(Flujo otro) {
        if (this == VIVO || otro == VIVO) {
            return VIVO;
        }
        if (this == RECUPERANDO || otro == RECUPERANDO) {
            return RECUPERANDO;
        }
        return MUERTO;
    }
}
