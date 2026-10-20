///[SinErrores]
// El contrato se compara despues de instanciar el argumento de 'implements':
// desde Caja, el "T obtener()" de Proveedor<String> es "String obtener()",
// y Caja lo cumple con String.
interface Proveedor<T> {
    T obtener();
}

class Caja implements Proveedor<String> {
    String obtener(){
        return null;
    }
}
