///[SinErrores]
// Gemelo sano de semError95/96/97 (ADR 0006): la regla solo saca de alcance
// el T de la clase en los metodos estaticos.
// - identidad: metodo de instancia, el T de Caja si esta en alcance.
// - propio: estatico con su propio <T> (Logro 3). El T de la firma tiene
//   origen en el metodo, asi que es valido aunque la clase tambien declare T.
// - poner: estatico con argumento generico concreto, sin T.
class Caja<T> {
    T identidad(T valor) {
        return valor;
    }
    static <T> T propio(T x) {
        return x;
    }
    static void poner(Caja<String> c, String[] datos) {
    }
}
