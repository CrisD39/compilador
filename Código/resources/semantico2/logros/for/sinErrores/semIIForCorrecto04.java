///[SinErrores]
// Las tres clausulas son opcionales, como en Java: una clausula vacia no es
// un componente mal puesto. El cuerpo puede ser una sentencia sin llaves.

class A1 {
    int a1;

    void m1() {
        for (;;) {
            a1 = 1;
        }
    }

    void m2() {
        for (var i = 0; ; i = i + 1)
            a1 = i;
    }

    // En su propio metodo: despues del for infinito de m2 seria inalcanzable
    // (logro caminosRetornoSinCodigoMuerto).
    void m3() {
        for (var i = 0; i < 3;) {
            i = i + 1;
        }
    }
}

class Init{
    static void main()
    { }
}
