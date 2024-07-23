package pt.isec.pa.javalife.model.data;

import pt.isec.pa.javalife.model.Facade;
import pt.isec.pa.javalife.model.gameengine.IGameEngine;
import pt.isec.pa.javalife.model.gameengine.IGameEngineEvolve;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Ecossistema implements Serializable, IGameEngineEvolve {
    private Set<IElemento> elementos;
    private int comprimento;
    private int largura;
    private static String imagemFauna;
    private static Ecossistema ecossistema;

    @Override
    public void evolve(IGameEngine gameEngine, long currentTime) {
        Set.copyOf(elementos).forEach(
                IElemento::evolve
        );
        Facade.getFacade().updateUI();
    }

    public static Ecossistema getEcossistema() {
        return ecossistema;
    }

    public void addElemento(IElemento elemento) {
        elementos.add(elemento);
        Facade.getFacade().updateUI();
    }

    public void removeElemento(IElemento elemento) {
        elementos.remove(elemento);
    }

    public int countSobreposicoes(IElemento elemento) {
        return (int) elementos.stream()
                .filter(e -> e instanceof Fauna)
                .filter(e -> e.getArea().contains(elemento.getArea()))
                .count();
    }

    public int getComprimento() {
        return comprimento;
    }

    public int getLargura() {
        return largura;
    }

    public static String getImagemFauna() {
        return imagemFauna;
    }

    public Set<IElemento> getElementos() {
        return elementos;
    }

    public Set<IElemento> getElementosFauna() {
        return elementos.stream()
                .filter(e -> e instanceof Fauna)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    public Set<IElemento> getElementosFlora() {
        return elementos.stream()
                .filter(e -> e instanceof Flora)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    public IElemento getElementoAt(double x, double y) {
        if(elementos == null)
            return null;

        return elementos.stream()
                .filter(e -> e.getArea().contains(x, y))
                .findFirst()
                .orElse(null);
    }

    public static double calcularDistancia(Fauna e1, Fauna e2) {
        return Math.sqrt(Math.pow(e1.getArea().xi() - e2.getArea().xi(), 2) +
                Math.pow(e1.getArea().yi() - e2.getArea().yi(), 2));
    }

    public static double getForcaCriaFlora() {
        return Facade.getFacade().getForcaCriaFlora();
    }

    public static double getForcaPerdidaFlora() {
        return Facade.getFacade().getForcaPerdidaFlora();
    }

    public static double getForcaMovimentoFauna() {
        return Facade.getFacade().getForcaMovimentoFauna();
    }

    public static double getForcaInicialFauna() {
        return Facade.getFacade().getForcaInicialFauna();
    }

    public Set<IElemento> getElementosAt(double x, double y) {
        if(elementos == null)
            return null;

        return elementos.stream()
                .filter(e -> e.getArea().contains(x, y))
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    public Ecossistema(int comprimento, int largura, String imagemFauna) {
        this.comprimento = comprimento;
        this.largura = largura;
        this.imagemFauna = imagemFauna;
        ecossistema = this;
        elementos = new HashSet<>();
    }
}