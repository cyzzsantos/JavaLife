package pt.isec.pa.javalife;

import javafx.application.Application;
import pt.isec.pa.javalife.model.Facade;
import pt.isec.pa.javalife.model.gameengine.GameEngine;
import pt.isec.pa.javalife.model.gameengine.IGameEngine;
import pt.isec.pa.javalife.ui.JavaLifeUI;

public class JavaLife {
    public static void main(String[] args) {
        Application.launch(JavaLifeUI.class, args);
    }
}