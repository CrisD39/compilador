///[Error:getValor|10]
// ERR_REDEFINICION_INCOMPATIBLE: Alfa exige "int getValor()" y Gama declara
// "char getValor()". Misma clave getValor/0, otra signatura: no cumple el
// contrato. Se reporta sobre el metodo de Gama (como semError64).
interface Alfa {
    int getValor();
}

class Gama implements Alfa {
    char getValor(){
        return 'x';
    }
}
