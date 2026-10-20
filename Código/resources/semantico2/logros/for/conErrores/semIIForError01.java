///[Error:5|8]
// La condicion del for estandar debe ser booleana, aca es un int.

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
