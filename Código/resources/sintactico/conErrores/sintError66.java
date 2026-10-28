///[Error:3.|7]
// "3.a" es floatLiteral(3.) e idMetVar(a) (spec 0008, D2 revisada): la "a"
// no continúa el float, así que lo cierra. El "3." no es una expresión de
// MiniJava, y el error cae ahí y no sobre la "a".
class Prueba {
    void m() {
        x = 1 + 3.a * 2;
    }
}
