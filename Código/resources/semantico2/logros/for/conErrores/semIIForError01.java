///[Error:for|9]
// La condicion del for estandar debe ser booleana, aca es un int
// (ERR_CONDICION_NO_BOOLEAN, sobre el for como en if/while).

class A1 {
    int a1;

    void m1() {
        for (var i = 0; 5; i = i + 1) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
