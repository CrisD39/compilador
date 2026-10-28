///[Error:x|6]
// int x = x sin atributo x (spec 0009, D5): el inicializador no ve el
// nombre que se declara, asi que el x de la derecha no esta declarado.

class A1 {
    void m1() { int x = x; }
}

class Init{
    static void main()
    { }
}
