///[Error:instanceof|13]
///[Error:instanceof|14]
// Lo mismo con una clase final propia que no implementa la interfaz, en
// las dos direcciones. Multi-deteccion: los dos errores, en orden
// (ERR_OPERADOR_INCOMPATIBLE, sobre instanceof).

interface I1 {}

final class F1 {}

class A1 {
    void m1(F1 f, I1 i) {
        var r1 = f instanceof I1;
        var r2 = i instanceof F1;
    }
}

class Init{
    static void main()
    { }
}
