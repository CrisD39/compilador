///[Error:.|6]
// "3.a" es intLiteral(3), "." e idMetVar(a) (spec 0008, D2): la "a" no
// continúa el float "3.", así que el "." no es parte del número.
class Prueba {
    void m() {
        x = 1 + 3.a * 2;
    }
}
