///[Error:?|13]
// Rama incompatible en un argumento con formal generico: con cs de tipo
// Caja<String>, el tipo esperado del argumento es String y la rama 1 no
// conforma (ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre el ?), sin un
// ERR_ARGUMENTO_INCOMPATIBLE sobre poner.

class Caja<T> {
    void poner(T t) { }
}

class A1 {
    void m1(boolean c, Caja<String> cs) {
        cs.poner(c ? 1 : "a");
    }
}

class Init{
    static void main()
    { }
}
