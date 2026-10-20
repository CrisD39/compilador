///[Error:clone|8]
// §11.8 -- Los arreglos no tienen clone() ni otros miembros salvo length y toString().
// Se reporta 'clone' en la linea 8.

class A1 {
    void m1() {
        var v = new int[10];
        var w = v.clone();
    }
}

class Init{
    static void main()
    { }
}
