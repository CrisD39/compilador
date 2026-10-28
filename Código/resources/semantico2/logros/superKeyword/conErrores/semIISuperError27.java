///[Error:this|17]
///[Error:=|20]
// La marca del contexto previo a la construccion se apaga aunque super(...)
// termine con un error: super(this) da ERR_REFERENCIA_ANTES_DE_SUPER sobre
// this, y despues x y m() se usan normalmente. El error siguiente es de
// tipos (this.y = 1 con boolean y: ERR_ASIGNACION_INCOMPATIBLE, sobre =).

class A1 {
    int x;
    A1(A1 a) { }
}

class B2 extends A1 {
    boolean y;

    B2() {
        super(this);
        x = 1;
        m();
        this.y = 1;
    }

    void m() { }
}

class Init{
    static void main()
    { }
}
