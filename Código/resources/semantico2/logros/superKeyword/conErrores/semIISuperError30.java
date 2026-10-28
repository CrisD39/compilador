///[Error:;|11]
// super suelto no es un valor, tampoco a la derecha de una asignacion:
// dentro de una expresion super exige un primer eslabon .id (error
// sintactico, sobre el ; que sigue a super).

class A1 { }

class B2 extends A1 {
    Object x;
    void m1() {
        x = super;
    }
}

class Init{
    static void main()
    { }
}
