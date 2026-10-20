///[SinErrores]
// Una clase sin 'extends' hereda de Object, y redefine su "String toString()"
// con exactamente la misma signatura -- es una redefinicion valida, no un
// ERR_REDEFINICION_INCOMPATIBLE ni un ERR_METODO_DUPLICADO.
class Punto {
    int x;

    String toString(){
        return "punto";
    }
}
