///[Error:=|15]
///[Error:secreto|16]
///[Error:noHay|17]
// Acceso con super en inicializadores de atributos (logro Atributos
// inicializados), uno por atributo: int a = super.nombre con String nombre
// (ERR_ASIGNACION_INCOMPATIBLE, sobre el = del atributo), super.secreto
// private (ERR_ACCESO_PRIVADO) y super.noHay (ERR_ATRIBUTO_NO_ENCONTRADO).

class A1 {
    String nombre;
    private int secreto;
}

class B2 extends A1 {
    int a = super.nombre;
    int b = super.secreto;
    int c = super.noHay;
}

class Init{
    static void main()
    { }
}
