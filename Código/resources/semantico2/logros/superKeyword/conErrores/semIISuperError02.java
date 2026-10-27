///[Error:x|11]
// super.x con x declarado solo en la clase actual: la superclase no lo
// tiene (ERR_ATRIBUTO_NO_ENCONTRADO, sobre x).

class A1 { }

class B2 extends A1 {
    int x;

    void m1() {
        super.x = 1;
    }
}

class Init{
    static void main()
    { }
}
