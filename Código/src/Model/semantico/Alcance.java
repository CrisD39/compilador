package Model.semantico;

import java.util.HashMap;

public class Alcance {

    private Alcance alcancePadre;
    private HashMap<String,Atributo> atributos;

    public Alcance() {
        alcancePadre = null;
        atributos = new HashMap<String, Atributo>();
    }

    public void insertarAtributo(Atributo nuevoAtributo){
        atributos.put(nuevoAtributo.getClave(),nuevoAtributo);
    }

    public Atributo buscarAtributo(String nombre){
        if(atributos.containsKey(nombre))
            return atributos.get(nombre);
        //TODO: Tirar error, se busca un elemento que no pertenece al scope.
    }

    public boolean estaPresenteAtributo(String nombre){
        return  atributos.containsKey(nombre);
    }
    public void setAlcancePadre(Alcance alcancePadre) {
        this.alcancePadre = alcancePadre;
    }

    public Alcance getPadre(){
        return alcancePadre;
    }
}
