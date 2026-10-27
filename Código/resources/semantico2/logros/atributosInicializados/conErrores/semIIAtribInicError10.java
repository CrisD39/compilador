///[Error:b|7]
// Referencia adelantada con los dos atributos en la misma linea: el orden de
// declaracion no sale de la linea sino del orden en el fuente
// (ERR_REFERENCIA_ADELANTADA, sobre b).

class A1 {
    int a = b; int b = 1;
}

class Init{
    static void main()
    { }
}
