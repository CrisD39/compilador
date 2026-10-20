///[Error:f|10]
// ERR_METODO_DUPLICADO: misma clave f/0 con distinto retorno. El retorno no
// forma parte de la clave, asi que es una declaracion repetida. Se reporta
// el segundo.
class Aa {
    int f(){
        return 0;
    }

    void f(){
    }
}
