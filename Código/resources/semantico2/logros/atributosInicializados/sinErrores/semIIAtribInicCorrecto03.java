///[SinErrores]
// El inicializador es un contexto de instancia: valen this, this.x, un
// atributo declarado antes, un atributo private propio, una llamada sin
// receptor a un metodo de instancia y un metodo estatico con A1.uno(). Un
// atributo heredado vale aunque el padre este escrito mas abajo en el archivo
// (lo declara otra clase), y uno heredado de un padre generico ya instanciado
// tiene el tipo instanciado: en C3, el T dato de Gen se ve como String.

class A1 {
    int x;
    private int p;
    A1 yo = this;
    int x2 = this.x;
    int y = x;
    int q = p;
    int z = doble();
    int w = A1.uno();

    int doble() { return 2; }
    static int uno() { return 1; }
}

class B2 extends Padre {
    int h = heredado;
}

class Padre {
    int heredado;
}

class Gen<T> {
    T dato;
}

class C3 extends Gen<String> {
    String s = dato;
}

class Init{
    static void main()
    { }
}
