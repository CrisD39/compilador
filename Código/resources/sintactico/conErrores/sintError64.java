///[Error:.|5]
// Un literal seguido de "." y un nombre son tres tokens (4 . metodo1), no un
// error léxico (spec 0008, D2): el parser rechaza el "." porque un literal
// no admite encadenamiento.
class Prueba { void m() { x = 4.metodo1(); } }
