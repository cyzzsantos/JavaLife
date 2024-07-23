package pt.isec.pa.javalife.model.data;

import pt.isec.pa.javalife.model.gameengine.IGameEngine;

import java.io.Serializable;

public sealed interface IElemento extends Serializable permits ElementoBase {
        int getId (); // retorna o identificador
        Elemento getType (); // retorna o tipo
        Area getArea (); // retorna a área ocupada
        void evolve ();
}


