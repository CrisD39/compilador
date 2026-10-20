///[SinErrores]
// §7 -- Entidades predefinidas: System y Object.debugPrint, toString() heredado en
// String.

class A1 {
    String t;

    void m1() {
        var n = System.read();
        System.printIln(n);
        System.printSln("hola");
        System.printBln(true);
        System.printCln('c');
        System.println();
        Object.debugPrint(3);
        var s = "texto".toString();
        t = this.toString();
    }
}

class Init{
    static void main()
    { }
}
