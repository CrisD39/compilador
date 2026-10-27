///[Error:=|15]
///[Error:=|16]
// Dos atributos mal inicializados en la misma clase, se reportan los dos
// (ERR_ASIGNACION_INCOMPATIBLE, sobre el =). Los argumentos genericos son
// invariantes: Caja<B2> no conforma con Caja<A1> aunque B2 extienda a A1. La
// cantidad de dimensiones es parte del tipo: int[][] no conforma con int[].

class A1 { }

class B2 extends A1 { }

class Caja<T> { }

class Usos {
    Caja<A1> c = new Caja<B2>();
    int[] v = new int[2][2];
}

class Init{
    static void main()
    { }
}
