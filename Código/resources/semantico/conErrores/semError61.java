///[Error:get|13]
// ERR_REDEFINICION_INCOMPATIBLE: Dd<T> extiende Base sin argumento. El T de
// Dd y el T de Base son parametros de tipo distintos (origen distinto),
// aunque se llamen igual, y el de Base se conserva al heredar sin argumento
// (no se reemplaza por Object). "T get()" de Dd no redefine al de Base.
class Base<T> {
    T get(){
        return null;
    }
}

class Dd<T> extends Base {
    T get(){
        return null;
    }
}
