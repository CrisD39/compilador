///[SinErrores]
// Logro 2: una interfaz puede extender varias interfaces a la vez
// (<ExtensionOpcional> con lista separada por coma). Una clase implementa
// una sola interfaz (ADR 0005), asi que Pato implementa la interfaz
// intermedia AnfibioVolador, que junta a Volador y Nadador.
interface Volador{

    void volar();

}

interface Nadador{

    void nadar();

}

interface AnfibioVolador extends Volador, Nadador{

    void saltar();

}

class Pato implements AnfibioVolador{

    void volar(){

    }

    void nadar(){

    }

    void saltar(){

    }

}
