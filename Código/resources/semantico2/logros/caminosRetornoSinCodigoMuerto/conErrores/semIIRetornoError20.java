///[Error:sinRetornoDeclarado|21]
///[Error:parcial|24]
// (Ticket 02 de la spec 0004 agrega aca la etiqueta ///[Error:var|32].)
// Viene de semIICorrecto72 de la base, que es el CP-SII-38 de la catedra
// ("variantes de return; no se exige retorno total ni se rechaza codigo
// inalcanzable"). Con el logro deja de dar [SinErrores]: ERR_FALTA_RETORNO en
// sinRetornoDeclarado y en parcial, y ERR_SENTENCIA_INALCANZABLE en el var
// despues del return. El constructor y los metodos void siguen siendo validos.
class Retornos {
    public Retornos() {
        return;
    }

    void vacio() {
    }

    void soloReturn() {
        return;
    }

    int sinRetornoDeclarado() {
    }

    int parcial(boolean elegir) {
        if (elegir) {
            return 1;
        }
    }

    int conInalcanzable() {
        return 1;
        var numero = 2;
    }
}
