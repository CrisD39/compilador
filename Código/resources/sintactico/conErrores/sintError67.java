///[Error:.|7]
// "1234567890.abc": el entero tiene más de 9 dígitos (error léxico, sin token)
// y el "." queda en reserva (spec 0008, D3). El sintáctico tiene que recibir el
// "." antes que "abc", así que el error cae sobre el "." y no sobre el ";".
class Prueba {
    void m() {
        x = 1234567890.abc;
    }
}
