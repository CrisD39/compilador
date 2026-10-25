///[Error:return|10]
///[Error:=|13]
// Dentro de una clase generica, T no instanciado no tiene cota implicita:
// devolver un T donde se espera Object (ERR_RETORNO_INCORRECTO, linea 10) y
// asignar un Object a un T (ERR_ASIGNACION_INCOMPATIBLE, linea 13) fallan los
// dos. El primer error no corta el chequeo: se reportan ambos.
class Caja<T>{
    T contenido;
    T obtener() { return contenido;}
    Object comoObjeto() {return contenido;}

    void guardarObjeto(Object objeto){
         contenido = objeto;
    }
}

class Init{
    static void main()
    { }
}
