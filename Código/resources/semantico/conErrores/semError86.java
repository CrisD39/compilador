///[Error:mover|13]
// ERR_METODO_DUPLICADO por clave (nombre, aridad): mover(int pasos) y
// mover(char pasos) tienen la misma clave mover/1 aunque difieran los
// tipos de sus parametros. Se reporta el segundo. Era semCorrecto14
// (sobrecarga por tipos) hasta la correccion de la Etapa 3: la Parte I
// solo admite sobrecarga por aridad (D4).
class Pato{

    void mover(int pasos){

    }

    void mover(char pasos){

    }

}
