///[Error:4.|5]
// "4.metodo1" es floatLiteral(4.) e idMetVar(metodo1), como en javac, y no un
// error léxico (spec 0008, D2 revisada). La gramática de MiniJava no tiene
// floats, así que el parser rechaza el "4." como expresión.
class Prueba { void m() { x = 4.metodo1(); } }
