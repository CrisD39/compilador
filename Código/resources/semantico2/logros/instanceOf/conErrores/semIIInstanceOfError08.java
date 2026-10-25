///[Error:instanceof|7]
// Un arreglo solo conforma con Object: probarlo contra otra clase es
// error (ERR_OPERADOR_INCOMPATIBLE, sobre instanceof).

class A1 {
    void m1(int[] arr) {
        var r = arr instanceof A1;
    }
}

class Init{
    static void main()
    { }
}
