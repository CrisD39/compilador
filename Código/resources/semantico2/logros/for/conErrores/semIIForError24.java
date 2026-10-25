///[Error:Inexistente|11]
// El tipo de la variable se valida primero, porque es lo primero que aparece
// en el fuente: se informa que Inexistente no esta declarado y no que el
// int de la expresion no es iterable (ERR_TIPO_NO_DECLARADO sobre el tipo).

class A1 {
    int a1;
    Object o;

    void m1() {
        for (Inexistente x : 10) {
            o = x;
        }
    }
}

class Init{
    static void main()
    { }
}
