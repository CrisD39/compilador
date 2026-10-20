///[Error:Bb|9]
// ERR_CONSTRUCTOR_NOMBRE_INVALIDO: un constructor tiene el nombre de su
// clase (Parte I, S6.3). "Bb()" dentro de Aa no lo tiene. Bb esta declarada,
// asi que el error es el nombre, no un tipo no declarado.
class Bb {
}

class Aa {
    Bb(){
    }
}
