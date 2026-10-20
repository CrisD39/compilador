///[SinErrores]
// §5.1 -- Las sustituciones se componen en la cadena Base<T> -> Intermedia<U> -> Final
// (U = String).

class Base<T> {
    T dato;

    T obtener() {
        return dato;
    }
}

class Intermedia<U> extends Base<U> {}

class Final extends Intermedia<String> {
    String s;

    void m1() {
        s = obtener();
        dato = "x";
    }
}

class Init{
    static void main()
    { }
}
