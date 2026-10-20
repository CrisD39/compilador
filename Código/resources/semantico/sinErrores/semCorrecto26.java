///[SinErrores]
// Cadena de instanciacion: Intermedia<U> extiende Base<U>, y Final extiende
// Intermedia<String>. El argumento se compone en cadena (T -> U -> String),
// asi que desde Final el "T get()" de Base es "String get()" y Final lo
// puede redefinir con String.
class Base<T> {
    T get(){
        return null;
    }
}

class Intermedia<U> extends Base<U> {
}

class Final extends Intermedia<String> {
    String get(){
        return null;
    }
}
