///[SinErrores]
// Logro 5: idGen como argumento anidado, resuelto contra el entorno vigente
// -- el T de la clase en el atributo, el U propio del metodo en su retorno y
// parametro.
class Lista<T>{
}

class Caja<T>{
    Lista<T> elementos;

    <U> Lista<U> envolver(Lista<U> origen){
        return origen;
    }
}
