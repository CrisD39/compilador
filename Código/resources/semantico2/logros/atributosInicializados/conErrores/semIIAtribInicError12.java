///[Error:b|11]
///[Error:v|12]
///[Error:o|13]
// Un atributo posterior que se lee por su nombre suelto es referencia
// adelantada aunque se use como argumento (m(b)), indexado (v[0]) o con
// encadenado (o.x): b, v y o son nombres sueltos que se leen. Un error por
// atributo, en el orden del programa (ERR_REFERENCIA_ADELANTADA, sobre cada
// nombre).

class A1 {
    int a = m(b);
    int c = v[0];
    int d = o.x;
    int b;
    int[] v;
    A1 o;
    int x;

    int m(int p) { return p; }
}

class Init{
    static void main()
    { }
}
