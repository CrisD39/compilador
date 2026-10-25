///[SinErrores]
// Dentro de su clase, un private se usa de todas las formas: implicito,
// con this, a traves de otra instancia de la misma clase, como estatico y
// con new (el constructor private de una fabrica). En una clase generica,
// el receptor Caja<String> tambien es de la clase.

class A1 {
    private int x;
    private A1() { }

    private int doble() { return x * 2; }
    private static int uno() { return 1; }

    static A1 crear() {
        return new A1();
    }

    void copiar(A1 otra) {
        x = otra.x;
        this.x = otra.doble();
        otra.x = A1.uno() + doble() + this.doble();
    }
}

class Caja<T> {
    private T dato;

    void copiar(Caja<T> otra) {
        dato = otra.dato;
    }

    void ver(Caja<String> c) {
        var s = c.dato;
    }
}

class Init{
    static void main()
    { }
}
