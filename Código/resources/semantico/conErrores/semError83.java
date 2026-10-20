///[Error:m|6]
// ERR_METODO_INTERFAZ_NOIMPLEMENTADO: un metodo estatico no cumple un
// contrato (Parte Ii, S6.2). Aa solo tiene "static void m()". Como en
// semError32, se reporta sobre el metodo de la interfaz.
interface Ii {
    void m();
}

class Aa implements Ii {
    static void m(){
    }
}
