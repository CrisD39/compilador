///[Error:Punto|9]
// ERR_CONSTRUCTOR_DUPLICADO por clave: dos constructores con la misma
// cantidad de parametros (1), aunque difieran los tipos. Se reporta el
// segundo.
class Punto {
    Punto(int x){
    }

    Punto(char c){
    }
}
