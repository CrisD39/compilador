///[Error:=|5]
// Arreglo incompatible (spec 0009, D5): boolean[] no conforma con int[].

class A1 {
    void m1() { int[] a = new boolean[2]; }
}

class Init{
    static void main()
    { }
}
