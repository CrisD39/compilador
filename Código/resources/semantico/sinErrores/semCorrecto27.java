///[SinErrores]
// Sobrecarga por aridad: mostrar() y mostrar(int n) tienen claves distintas
// (mostrar/0 y mostrar/1), asi que conviven en la misma clase. Lo mismo para
// dos constructores con distinta cantidad de parametros.
class Pantalla {
    Pantalla(){
    }

    Pantalla(int ancho){
    }

    void mostrar(){
    }

    void mostrar(int n){
    }
}
