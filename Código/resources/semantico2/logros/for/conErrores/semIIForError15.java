///[Error:for|22]
// La conformidad va del elemento a la variable, no al reves: Lista<Persona>
// no se recorre como Alumno aunque Alumno sea subclase de Persona
// (ERR_FOREACH_TIPO_INCOMPATIBLE, sobre el for).

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

    void m1(Lista<Persona> personas) {
        for (Alumno a : personas) {
            a1 = a.edad;
        }
    }
}

class Init{
    static void main()
    { }
}
