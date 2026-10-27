///[Error:=|12]
///[Error:=|15]
///[Error:d|18]
// Multi-deteccion: los inicializadores se chequean mezclados con los metodos,
// en el orden del programa. Primero el atributo a mal inicializado
// (ERR_ASIGNACION_INCOMPATIBLE), despues el error de tipos de m1, escrito
// antes del siguiente atributo (ERR_ASIGNACION_INCOMPATIBLE), y por ultimo la
// referencia adelantada en el inicializador de c (ERR_REFERENCIA_ADELANTADA,
// sobre d). Ninguno corta el chequeo de los demas.

class A1 {
    int a = true;

    void m1() {
        a = false;
    }

    int c = d;
    int d;
}

class Init{
    static void main()
    { }
}
