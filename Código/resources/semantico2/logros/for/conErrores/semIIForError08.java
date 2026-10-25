///[Error:var|10]
// Una declaracion local no puede ser el cuerpo directo del for, igual que
// en if/while (ERR_DECLARACION_COMO_CUERPO sobre el var).

class A1 {
    int a1;

    void m1() {
        for (var i = 0; i < 10; i = i + 1)
            var x = i;
    }
}

class Init{
    static void main()
    { }
}
