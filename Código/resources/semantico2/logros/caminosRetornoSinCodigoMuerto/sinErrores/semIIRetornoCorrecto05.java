///[SinErrores]
// Condiciones que no son expresiones constantes: el bucle puede terminar, asi
// que el return de despues es alcanzable. null no es constante y una division
// por cero hace que la expresion no lo sea (igual que javac). El if nunca se
// trata como constante, ni con if (true): el return 2 es alcanzable.

class A1 {
    int m1(boolean b) {
        while (b) { }
        return 1;
    }

    int m2() {
        while (null == null) { }
        return 1;
    }

    int m3() {
        while (1 / 0 == 0) { }
        return 1;
    }

    int m4() {
        if (true) return 1;
        return 2;
    }
}

class Init{
    static void main()
    { }
}
