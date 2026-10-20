///[Error:if|10]
// §12.4 -- Una asignacion como condicion vale solo si su resultado es boolean; aca es
// int.
// Se reporta 'if' en la linea 10.

class A1 {
    int a1;

    void m1() {
        if (a1 = 3) { }
    }
}

class Init{
    static void main()
    { }
}
