///[Error:for|9]
// El elemento (String) no conforma con el tipo de la variable (int)
// (ERR_FOREACH_TIPO_INCOMPATIBLE, sobre el for).

class A1 {
    int a1;

    void m1(MiniIterable<String> it) {
        for (int x : it) {
            a1 = x;
        }
    }
}

class Init{
    static void main()
    { }
}
