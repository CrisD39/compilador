///[SinErrores]
// §10.1 / 12.6 -- Bloques hermanos pueden declarar el mismo nombre; los anidados ven
// parametros y locales del contenedor.

class A1 {
    void m1(int p1) {
        var valores = new int[3];
        {
            var indice = 0;
            valores[indice] = p1;
        }
        {
            var indice = 1;
            valores[indice] = p1 + indice;
        }
    }
}

class Init{
    static void main()
    { }
}
