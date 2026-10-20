///[Error:m|7]
// ERR_METODO_DUPLICADO por clave en una interfaz: m(int x) y m(char c)
// tienen la misma clave m/1. Misma regla que semError56 para clases. Se
// reporta el segundo.
interface Ii {
    void m(int x);
    void m(char c);
}
