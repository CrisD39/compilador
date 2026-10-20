///[SinErrores]
// Dos interfaces con la misma forma de metodo generico (cada una devuelve su
// propio parametro de tipo), pero con nombres distintos (T vs U). Fabricas
// las hereda a las dos: deben reconocerse como la misma signatura, no como
// ERR_METODO_RETORNO_INCOMPATIBLE.
interface Fabrica1{
    <T> T crear(T semilla);
}

interface Fabrica2{
    <U> U crear(U semilla);
}

interface Fabricas extends Fabrica1, Fabrica2{
}

class FabricaDoble implements Fabricas{
    <T> T crear(T semilla){
        return semilla;
    }
}
