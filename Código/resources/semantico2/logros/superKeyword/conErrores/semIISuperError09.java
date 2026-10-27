///[Error:.|12]
// La llamada al constructor de la superclase es una sentencia: despues de
// super(1) se espera el ; (error sintactico, sobre el . que sigue).

class A1 {
    A1(int v) { }
    void m() { }
}

class B2 extends A1 {
    B2() {
        super(1).m();
    }
}

class Init{
    static void main()
    { }
}
