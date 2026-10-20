///[Error:length|7]
// §11.8 -- Con tipo estatico Object tampoco hay length.
// Se reporta 'length' en la linea 7.

class A1 {
    void m1(Object p1) {
        var v = p1.length;
    }
}

class Init{
    static void main()
    { }
}
