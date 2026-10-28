///[Error:[|11]
// super no es un arreglo: el primer eslabon despues de super tiene que ser
// .id, asi que super[0] es un error sintactico sobre el [.

class A1 {
    int[] v;
}

class B2 extends A1 {
    void m1() {
        var a = super[0];
    }
}

class Init{
    static void main()
    { }
}
