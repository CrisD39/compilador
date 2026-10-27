///[SinErrores]
// Lo que NO es referencia adelantada (JLS 8.3.3): solo cuenta el nombre suelto
// que se lee. Valen los accesos con receptor a un atributo posterior (this.b,
// new A1().b y otro.b, con otro declarado antes), lo que lee un metodo llamado
// (leerE() lee e, declarado despues de d) y el destino directo de un = ((g = 3),
// con g posterior).

class A1 {
    A1 otro = null;
    int a = this.b;
    int c = new A1().b;
    int h = otro.b;
    int d = leerE();
    int f = (g = 3);
    int b;
    int e = 5;
    int g;

    int leerE() { return e; }
}

class Init{
    static void main()
    { }
}
