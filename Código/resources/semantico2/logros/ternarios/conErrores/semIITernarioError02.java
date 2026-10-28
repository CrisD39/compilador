///[Error:?|9]
// La condicion de un ternario tiene que ser boolean, como la de un if
// (ERR_CONDICION_NO_BOOLEAN, sobre el ?).

class A1 {
    int i;

    void m1() {
        i = 1 ? 2 : 3;
    }
}

class Init{
    static void main()
    { }
}
