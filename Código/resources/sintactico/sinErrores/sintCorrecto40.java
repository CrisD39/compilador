///[SinErrores]
// Locales clásicas como en Java (spec 0009, D2): cada nombre con su propio
// inicializador opcional (<Declarador> ::= idMetVar <InitLocalOpc>), en
// cualquier orden, y tipos arreglo de una y dos dimensiones sobre primitivo,
// clase, genérico instanciado y parámetro de tipo. El for sigue admitiendo
// un solo nombre sin dimensiones. Sólo exige que parsee.
class Caja<T> {
    void m() {
        int x, y = 2, z;
        int a = 1, b = 2;
        int p, q, r = 10;
        boolean c = true, d = c, e;
        int[] v = new int[3], w;
        int[][] matriz;
        A1[] objetos = null, otros;
        A1[][] tabla;
        Caja<A1>[] cajas;
        Caja<A1> caja = new Caja<A1>(), vacia;
        T[] elementos;
        T t, u = t;
        for (int i = 0; i < 10; i = i + 1) { }
        for (A1 o = null; o == null; o = new A1()) { }
    }
}
