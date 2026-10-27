///[Error:super|12]
///[Error:super|18]
// Fuera de lugar en dos constructores (ERR_SUPER_FUERA_DE_LUGAR, sobre
// super): dentro de un bloque anidado y como cuerpo de un if. En ninguno es
// la primera sentencia del cuerpo del constructor.

class A1 { }

class B2 extends A1 {
    B2() {
        {
            super();
        }
    }

    B2(boolean c) {
        if (c)
            super();
    }
}

class Init{
    static void main()
    { }
}
