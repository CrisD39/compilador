///[Error:x|14]
///[Error:x|20]
///[Error:y|22]
// ERR_SENTENCIA_INALCANZABLE con recuperacion (como javac): se reporta una vez
// por tramo muerto. En m1, y = 3 no da error en cascada. En m2, el return 3
// vuelve a cortar el camino y empieza un segundo tramo, que se reporta en y.

class A1 {
    int x;
    int y;

    int m1() {
        return 1;
        x = 2;
        y = 3;
    }

    int m2() {
        return 1;
        x = 2;
        return 3;
        y = 4;
    }
}

class Init{
    static void main()
    { }
}
