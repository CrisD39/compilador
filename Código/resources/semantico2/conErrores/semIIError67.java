///[Error:=|8]
// §11.8 -- length es de solo lectura.
// Se reporta '=' en la linea 8.

class A1 {
    void m1() {
        var v = new int[10];
        v.length = 3;
    }
}

class Init{
    static void main()
    { }
}
