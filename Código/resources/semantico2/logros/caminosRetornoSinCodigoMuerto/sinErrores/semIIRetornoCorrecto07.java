///[SinErrores]
// Anidados: if/else con if/else adentro que retornan en todas las ramas,
// bloques anidados con el return adentro, y una rama con return y la otra con
// un bucle infinito (ninguna de las dos termina normalmente).

class A1 {
    int m1(boolean b, boolean c) {
        if (b) {
            if (c) return 1;
            else return 2;
        } else {
            return 3;
        }
    }

    int m2() {
        {
            {
                return 1;
            }
        }
    }

    int m3(boolean b) {
        if (b) return 1;
        else while (true) { }
    }
}

class Init{
    static void main()
    { }
}
