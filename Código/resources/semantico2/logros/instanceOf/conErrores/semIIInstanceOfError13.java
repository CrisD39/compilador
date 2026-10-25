///[Error:T|7]
// El destino no puede ser un parametro de tipo: la gramatica pide un
// idClase despues de instanceof (error sintactico, sobre T).

class Caja<T> {
    void m1(Object o) {
        var r = o instanceof T;
    }
}

class Init{
    static void main()
    { }
}
