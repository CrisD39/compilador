///[Error:Circulo|9]
// Forma permite a Circulo, pero Circulo se declara 'class' lisa al
// implementarla -- la exhaustividad real de Java tambien aplica del
// lado 'implements', no solo 'extends'.
sealed interface Forma permits Circulo {
    int area();
}

class Circulo implements Forma {
    int area(){
        return 1;
    }
}
