///[SinErrores]
// Conformidad del inicializador: las mismas reglas que el lado derecho de una
// asignacion simple. char y String con sus literales, null en una referencia,
// una subclase en un atributo del padre, una clase en un atributo de su
// interfaz, un arreglo con la misma dimension y tipo base, una generica con el
// mismo argumento, y T t = null en Caja<T> (null conforma con un parametro de
// tipo).

interface I1 { }

class A1 implements I1 { }

class B2 extends A1 { }

class Caja<T> {
    T t = null;
}

class Usos {
    char c1 = 'x';
    String s1 = "hola";
    A1 a1 = null;
    A1 a2 = new B2();
    I1 i1 = new A1();
    int[] v1 = new int[3];
    Caja<A1> c2 = new Caja<A1>();
}

class Init{
    static void main()
    { }
}
