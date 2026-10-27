///[Error:secreto|12]
// La visibilidad se controla en un inicializador como en cualquier cuerpo de
// la clase: secreto es private de B2 y se lee desde un atributo de A1
// (ERR_ACCESO_PRIVADO, sobre el nombre del atributo; logro Visibilidad
// chequeada).

class B2 {
    private int secreto;
}

class A1 {
    int a = new B2().secreto;
}

class Init{
    static void main()
    { }
}
