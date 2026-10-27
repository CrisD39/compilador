///[Error:b|8]
// Referencia adelantada: a lee b por su nombre suelto, y b esta declarado
// despues que a en la misma clase (ERR_REFERENCIA_ADELANTADA, sobre el nombre
// usado, en la linea del inicializador de a; no en la de la declaracion de
// b).

class A1 {
    int a = b;
    int b = 1;
}

class Init{
    static void main()
    { }
}
