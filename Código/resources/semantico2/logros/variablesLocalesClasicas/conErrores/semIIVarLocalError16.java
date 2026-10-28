///[Error:=|9]
///[Error:=|10]
// Multi-deteccion (spec 0009, D6): el primer error corta la sentencia, sin
// chequear el inicializador de y, pero y queda declarada int, asi que
// y = false da su propio error, y no uno de "no declarada".

class A1 {
    void m1() {
        int x = true, y = noExiste;
        y = false;
    }
}

class Init{
    static void main()
    { }
}
