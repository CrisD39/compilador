///[Error:sinRetorno|9]
///[Error:parcial|11]
// (Ticket 02 de la spec 0004 agrega aca la etiqueta ///[Error:var|17].)
// Viene de semIICorrecto31 de la base (12.5), que con el logro deja de ser
// valido: ERR_FALTA_RETORNO en sinRetorno (cuerpo vacio) y en parcial (if sin
// else), y ERR_SENTENCIA_INALCANZABLE en el var despues del return.

class A1 {
    int sinRetorno() {}

    int parcial(boolean elegir) {
        if (elegir) return 1;
    }

    int conCodigoInalcanzable() {
        return 1;
        var numero = 2;
    }
}

class Init{
    static void main()
    { }
}
