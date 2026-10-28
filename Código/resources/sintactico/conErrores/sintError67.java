///[Error:1234567890.|8]
// "1234567890.abc": con el "." el literal es un float, que no tiene límite de
// 9 dígitos (REQ-AL-21), así que no hay error léxico. Es floatLiteral(1234567890.)
// e idMetVar(abc) (spec 0008, D2 revisada), y el sintáctico rechaza el float
// como expresión.
class Prueba {
    void m() {
        x = 1234567890.abc;
    }
}
