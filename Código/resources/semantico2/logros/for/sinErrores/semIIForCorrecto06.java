///[SinErrores]
// Genericos: Lista<E> implements MiniIterable<E>, asi que Lista<Persona>
// recorre Persona. El elemento tiene que conformar con la variable, no ser
// igual: Persona se recorre como Object, y Lista<Alumno> como Persona
// (Alumno es subclase). El cuerpo puede ser una sentencia sin llaves.

class Persona {
    int edad;
}

class Alumno extends Persona { }

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    int a1;
    Object o;

    void m1(Lista<Persona> personas, Lista<Alumno> alumnos) {
        for (Persona p : personas) {
            a1 = p.edad;
        }
        for (Object x : personas)
            o = x;
        for (Persona p : alumnos)
            a1 = p.edad;
    }
}

class Init{
    static void main()
    { }
}
