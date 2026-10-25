///[Error:[|7]
// El destino no puede ser un arreglo: despues del tipo se espera el cierre
// de la expresion (error sintactico, sobre [).

class A1 {
    void m1(Object o) {
        var r = o instanceof A1[];
    }
}

class Init{
    static void main()
    { }
}
