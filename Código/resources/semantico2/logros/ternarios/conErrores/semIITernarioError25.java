///[Error:;|11]
// D5: un ternario suelto no es una sentencia valida. La forma se controla
// antes de tipar, como en javac, asi que con las dos ramas void el error es
// ERR_SENTENCIA_INVALIDA sobre el ";" y no ERR_TERNARIO_RAMA_VOID sobre el ?.

class A1 {
    void v() {}

    void m1(boolean c) {
        c ? v()
          : v();
    }
}

class Init{
    static void main()
    { }
}
