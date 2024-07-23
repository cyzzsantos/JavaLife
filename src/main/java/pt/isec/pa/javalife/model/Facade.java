package pt.isec.pa.javalife.model;

import pt.isec.pa.javalife.model.data.*;
import pt.isec.pa.javalife.model.gameengine.GameEngine;
import pt.isec.pa.javalife.model.gameengine.IGameEngine;
import pt.isec.pa.javalife.ui.JavaLifeUI;
import java.util.Set;

public class Facade {
    private static Facade facade;
    private Thread thread;
    private Ecossistema ecossistema;
    private static IGameEngine gameEngine;
    private boolean running = false;
    private double forcaInicialFlora;
    private double forcaCriaFlora;
    private double forcaPerdidaFlora;
    private double forcaInicialFauna;
    private double forcaMovimentoFauna;
    private String corFlora;

    private Facade() {

    }

    public static Facade getFacade() {
        if (facade == null) {
            facade = new Facade();
        }

        return facade;
    }

    public boolean start() {
        if(!running) {
            thread = new Thread(() -> {
                gameEngine.registerClient(ecossistema);
                gameEngine.start(500);
                gameEngine.waitForTheEnd();
            });

            thread.start();
            return running = true;
        } else {
            return false;
        }
    }

    public void stop() {
        if(gameEngine != null)
            gameEngine.stop();
        running = false;
    }

    public int getEcossistemaComprimento() {
        return ecossistema.getComprimento();
    }

    public int getEcossistemaLargura() {
        return ecossistema.getLargura();
    }

    public double getForcaCriaFlora() {
        return forcaCriaFlora;
    }

    public double getForcaPerdidaFlora() {
        return forcaPerdidaFlora;
    }

    public double getForcaMovimentoFauna() {
        return forcaMovimentoFauna;
    }

    public double getForcaInicialFauna() {
        return forcaInicialFauna;
    }

    public String getCorFlora() {
        return corFlora;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean ecossistemaExists() {
        return ecossistema != null;
    }

    public Set<IElemento> getElementos() {
        return ecossistema.getElementos();
    }

    public void updateUI() {
        JavaLifeUI.getInstance().updateEcossistema();
    }

    public void createUI() {
        JavaLifeUI.getInstance().createEcossistema();
    }

    public void addElementoInanimado() {
        int randX = (int) (1 + (Math.random() * (ecossistema.getComprimento() - 2)));
        int randY = (int) (1 + (Math.random() * (ecossistema.getLargura() - 2)));
        int randXi = (int) (1 + (Math.random() * 3));
        int randYi = (int) (1 + (Math.random() * 3));

        for(IElemento e : ecossistema.getElementos()) {
            if(e.getArea().contains(new Area(randX, randY, randYi, randXi))) {
                addElementoInanimado();
                return;
            }
        }

        ecossistema.addElemento(new Inanimado(randX, randY, randYi, randXi));
    }

    public void addElementoFlora() {
        int randX = (int) (1 + (Math.random() * (ecossistema.getComprimento() - 2)));
        int randY = (int) (1 + (Math.random() * (ecossistema.getLargura() - 2)));
        int randXi = (int) (1 + (Math.random() * 3));
        int randYi = (int) (1 + (Math.random() * 3));

        for(IElemento e : ecossistema.getElementos()) {
            if(e.getArea().contains(new Area(randX, randY, randYi, randXi))) {
                addElementoFlora();
                return;
            }
        }

        ecossistema.addElemento(new Flora(randX, randY, randXi, randYi, forcaInicialFlora));
    }

    public void addElementoFauna() {
        int randX = (int) (1 + (Math.random() * (ecossistema.getComprimento() - 2)));
        int randY = (int) (1 + (Math.random() * (ecossistema.getLargura() - 2)));

        for(IElemento e : ecossistema.getElementos()) {
            if(e.getArea().contains(new Area(randX, randY, 1, 1))) {
                addElementoFauna();
                return;
            }
        }

        ecossistema.addElemento(new Fauna(randX, randY, 1, 1));
    }

    public void buildEcossistema(int comprimento, int largura, double forcaInicialFlora,
                                 double forcaCriaFlora, double forcaPerdidaFlora, String corFlora, double forcaInicialFauna,
                                 double forcaMovimentoFauna, String imagemFauna) {
        gameEngine = new GameEngine();
        ecossistema = new Ecossistema(comprimento, largura, imagemFauna);

        for(int i = 0; i < comprimento; i++) {
            ecossistema.addElemento(new Inanimado(i, 0, 1, 1));
            ecossistema.addElemento(new Inanimado(i, largura - 1, 1, 1));
        }

        for(int i = 0; i < largura; i++) {
            ecossistema.addElemento(new Inanimado(0, i, 1, 1));
            ecossistema.addElemento(new Inanimado(comprimento - 1, i, 1, 1));
        }

        this.forcaInicialFlora = forcaInicialFlora;
        this.forcaCriaFlora = forcaCriaFlora;
        this.forcaPerdidaFlora = forcaPerdidaFlora;
        this.forcaInicialFauna = forcaInicialFauna;
        this.forcaMovimentoFauna = forcaMovimentoFauna;
        this.corFlora = corFlora;

        createUI();
    }
}
