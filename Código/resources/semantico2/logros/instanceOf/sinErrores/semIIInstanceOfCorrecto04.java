///[SinErrores]
// El resultado es boolean: va en condiciones, en un return, en un
// argumento y en la cadena de operadores (sin precedencia, asociativa a
// izquierda: a instanceof B2 && c es (a instanceof B2) && c, por eso un
// segundo instanceof despues de || va entre parentesis). El operando
// puede ser cualquier expresion de referencia: una llamada, un this, un
// parametro de tipo T (se borra a Object) o un arreglo contra Object.

class A1 {
    boolean ok;

    A1 dar() { return this; }

    boolean es(A1 a) {
        return a instanceof B2;
    }

    void usar(boolean x) { }

    void m1(A1 a, int[] arr) {
        if (a instanceof B2 && ok) {
            ok = !(a instanceof B2);
        }
        while (dar() instanceof B2 || (this instanceof B2)) {
            usar(a.dar() instanceof B2);
        }
        ok = a instanceof B2 == true;
        ok = arr instanceof Object;
    }
}

class B2 extends A1 {}

class Caja<T> {
    boolean m1(T t) {
        return t instanceof A1;
    }
}

class Init{
    static void main()
    { }
}
