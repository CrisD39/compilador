///[Error:crear|13]
// Dos interfaces con metodos genericos de la MISMA forma (mismo nombre,
// mismo parametro de tipo propio canonicalizado), pero con retorno
// REALMENTE incompatible: una devuelve su propio T, la otra devuelve
// literalmente "int" (no un parametro de tipo). Fabricas las hereda a las
// dos: debe detectarse como incompatible, no se volvio permisivo de mas.
// Se reporta sobre el crear de Fabrica2.
interface Fabrica1{
    <T> T crear(T semilla);
}

interface Fabrica2{
    <U> int crear(U semilla);
}

interface Fabricas extends Fabrica1, Fabrica2{
}

class FabricaRota implements Fabricas{
    <T> T crear(T semilla){
        return semilla;
    }
}
