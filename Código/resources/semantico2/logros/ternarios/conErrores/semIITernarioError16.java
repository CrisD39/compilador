///[Error:?|12]
// Rama incompatible en super(...): con A1(int), el tipo esperado del
// argumento es int y la rama "a" no conforma (ERR_TERNARIO_RAMA_INCOMPATIBLE,
// sobre el ?), sin un ERR_ARGUMENTO_INCOMPATIBLE sobre super.

class A1 {
    A1(int i) { }
}

class B2 extends A1 {
    B2(boolean c) {
        super(c ? 1 : "a");
    }
}

class Init{
    static void main()
    { }
}
