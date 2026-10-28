///[Error:=|5]
// Referencia incompatible (spec 0009, D5): Object no conforma con A1.

class A1 {
    void m1() { A1 a = new Object(); }
}

class Init{
    static void main()
    { }
}
