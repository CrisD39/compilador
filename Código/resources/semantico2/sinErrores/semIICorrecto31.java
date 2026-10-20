///[SinErrores]
// §12.5 -- Doc. base: no se exige retorno en todos los caminos ni se rechaza codigo
// inalcanzable (se chequea igual). Choca con el logro de caminos de retorno.

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
