///[Error:a|5]
// "345a" es intLiteral(345) seguido de idMetVar(a), como en javac (spec 0008,
// D1): el error es sintáctico, sobre el nombre que sobra después del número.
class Prueba {
    void m() { x = 345a; }
}
