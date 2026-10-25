///[Error:m1|12]
// El acceso se controla apenas se resuelve el metodo, antes que los
// argumentos: gana m1 y no la variable no declarada (ERR_ACCESO_PRIVADO,
// sobre el nombre del metodo).

class A1 {
    private void m1(int n) { }
}

class B2 {
    void m2(A1 a) {
        a.m1(noExiste);
    }
}

class Init{
    static void main()
    { }
}
