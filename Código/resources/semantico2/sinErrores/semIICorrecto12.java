///[SinErrores]
// §5.1 -- Vista desde Caja<String>: atributo, parametro y retorno T se ven como
// String, y el resultado conforma con Object.

class Caja<T> {
    T contenido;

    T obtener() {
        return contenido;
    }

    void guardar(T p1) {
        contenido = p1;
    }
}

class A1 {
    String s;
    Object o;

    void m1(Caja<String> c) {
        s = c.obtener();
        c.guardar("hola");
        c.contenido = s;
        o = c.obtener();
    }
}

class Init{
    static void main()
    { }
}
