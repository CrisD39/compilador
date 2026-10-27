///[Error:x|8]
// Autorreferencia: x se lee en su propia inicializacion. El atributo que se
// inicializa no esta declarado "antes" que si mismo, asi que tambien es una
// referencia adelantada (ERR_REFERENCIA_ADELANTADA, sobre x; javac la llama
// "self-reference in initializer").

class A1 {
    int x = x + 1;
}

class Init{
    static void main()
    { }
}
