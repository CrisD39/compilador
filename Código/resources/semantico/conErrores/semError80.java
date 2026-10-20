///[Error:id|12]
// ERR_REDEFINICION_INCOMPATIBLE: un metodo generico y uno no generico con la
// misma clave tienen signaturas distintas. Hija hereda "Object id(Object x)"
// y declara "<T> T id(T x)".
class Base {
    Object id(Object x){
        return x;
    }
}

class Hija extends Base {
    <T> T id(T x){
        return x;
    }
}
