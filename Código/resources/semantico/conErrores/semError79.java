///[Error:get|12]
// ERR_REDEFINICION_INCOMPATIBLE: Dd extiende Base sin argumento, asi que el
// T de Base se conserva (no se borra a Object, Parte I S5). "Object get()"
// no redefine "T get()". Mismo criterio que semError71, por 'extends'.
class Base<T> {
    T get(){
        return null;
    }
}

class Dd extends Base {
    Object get(){
        return null;
    }
}
