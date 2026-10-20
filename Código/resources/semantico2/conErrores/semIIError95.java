///[Error:while|7]
// §12.4 -- La condicion del while debe ser boolean: una referencia no se convierte.
// Se reporta 'while' en la linea 7.

class A1 {
    void m1(A1 p1) {
        while (p1) { }
    }
}

class Init{
    static void main()
    { }
}
