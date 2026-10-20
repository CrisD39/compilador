///[SinErrores]
// Redefinicion instanciada: Dd extiende Base<String>, asi que, vistos desde Dd,
// "T get()" y "void poner(T valor)" de Base son "String get()" y
// "void poner(String valor)". Dd los redefine con String: misma signatura
// despues de instanciar. Ademas, el parametro se llama distinto (valor vs
// otro): los nombres de los parametros no cuentan en la signatura.
class Base<T> {
    T get(){
        return null;
    }

    void poner(T valor){
    }
}

class Dd extends Base<String> {
    String get(){
        return null;
    }

    void poner(String otro){
    }
}
