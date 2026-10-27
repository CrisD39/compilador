///[Error:noExiste|16]
///[Error:=|17]
///[Error:+|18]
// Multi-deteccion: los errores de super se reportan mezclados con los demas,
// en el orden del programa. super.noExiste() (ERR_METODO_NO_ENCONTRADO),
// super.x = true con int x (ERR_ASIGNACION_INCOMPATIBLE) y this.y = 1 + true
// (ERR_OPERADOR_INCOMPATIBLE).

class A1 {
    int x;
}

class B2 extends A1 {
    int y;
    void m1() {
        super.noExiste();
        super.x = true;
        this.y = 1 + true;
    }
}

class Init{
    static void main()
    { }
}
