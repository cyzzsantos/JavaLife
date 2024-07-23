package pt.isec.pa.javalife.model.data;

import java.util.HashSet;
import java.util.Set;

public final class Flora extends ElementoBase implements IElementoComForca, IElementoComImagem {
    private static int counter;
    private int nReproducoes;
    private double forca;
    private final double forcaPorTick;
    private String imagem;
    private FloraState state;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Elemento getType() {
        return Elemento.FLORA;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public double getForca() {
        return forca;
    }

    @Override
    public void setForca(double forca) {
        this.forca = forca;
    }

    @Override
    public String getImagem() {
        return imagem;
    }

    @Override
    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @Override
    public void evolve() {
        switch (state) {
            case VIVO:
                if(noEnergy()) {
                    handleEvent(FloraEvents.FORCA_ZERO);
                    break;
                }
                if(isSobreposto()) {
                    handleEvent(FloraEvents.SOBREPOSICAO_DETETADA);
                    break;
                }
                if(podeReproduzir()) {
                    handleEvent(FloraEvents.POSSIVEL_REPRODUZIR);
                    break;
                }

                this.forca = Math.min(forca + forcaPorTick, 100);
                break;
            case REPRODUZINDO:
                if(isSobreposto()) {
                    handleEvent(FloraEvents.SOBREPOSICAO_DETETADA);
                    break;
                }
                reproduzir();
                break;
            case SOBREPOSTO:
                if(noEnergy()) {
                    handleEvent(FloraEvents.FORCA_ZERO);
                    break;
                }
                sobreposto();
                break;
            case MORTO:
                morrer();
        }
    }

    private boolean noEnergy() {
        return forca <= 0;
    }

    private boolean podeReproduzir() {
        return forca >= 90 && nReproducoes < 2;
    }

    public void handleEvent(FloraEvents event) {
        switch (state) {
            case VIVO:
                switch(event) {
                    case POSSIVEL_REPRODUZIR:
                        setState(FloraState.REPRODUZINDO);
                        break;
                    case SOBREPOSICAO_DETETADA:
                        setState(FloraState.SOBREPOSTO);
                        break;
                    case FORCA_ZERO:
                        setState(FloraState.MORTO);
                        break;
                    default:
                        break;
                }
                break;
            case REPRODUZINDO:
                switch(event) {
                    case REPRODUCAO_TERMINADA:
                        setState(FloraState.VIVO);
                        break;
                    case SOBREPOSICAO_DETETADA:
                        setState(FloraState.SOBREPOSTO);
                        break;
                    case FORCA_ZERO:
                        setState(FloraState.MORTO);
                        break;
                    default:
                        break;
                }
                break;
            case SOBREPOSTO:
                switch(event) {
                    case SOBREPOSICAO_TERMINOU:
                        if(nReproducoes < 2 && forca >= 90)
                            setState(FloraState.REPRODUZINDO);
                        else
                            setState(FloraState.VIVO);
                        break;
                    case FORCA_ZERO:
                        setState(FloraState.MORTO);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void setState(FloraState state) {
        this.state = state;
    }

    private void reproduzir() {
        int n = 0;
        Area celulasAdjacentes = new Area(this.area.xi() - 1, this.area.yi() - 1,
                this.area.yf() + 2, this.area.xf() + 2);
        Set<Area> perimetro = getPerimetro(celulasAdjacentes);

        for(Area a : perimetro) {
            if(Ecossistema.getEcossistema().getElementosAt(a.xi(), a.yi()).isEmpty()) {
                n++;
            }
        }

        if(n > 0) {
            Area pos;
            do {
                pos = (Area) perimetro.toArray()[(int) (Math.random() * perimetro.size())];
            } while(!Ecossistema.getEcossistema().getElementosAt((int) pos.xi(), (int) pos.yi()).isEmpty());

            Ecossistema.getEcossistema().addElemento(new Flora((int) pos.xi(), (int) pos.yi(), 1, 1,
                    Ecossistema.getForcaCriaFlora()));

            nReproducoes++;
            this.forca = 60;
            handleEvent(FloraEvents.REPRODUCAO_TERMINADA);
        }
    }

    private Set<Area> getPerimetro(Area celulasAdjacentes) {
        Set<Area> perimetro = new HashSet<>();

        for(int x = (int) celulasAdjacentes.xi(); x < celulasAdjacentes.xi() + celulasAdjacentes.xf(); x++) {
            perimetro.add(new Area(x, celulasAdjacentes.yi(), 1, 1));
            perimetro.add(new Area(x, celulasAdjacentes.yi() + celulasAdjacentes.yf() - 1, 1, 1));
        }

        for(int y = (int) celulasAdjacentes.yi(); y < celulasAdjacentes.yf(); y++) {
            perimetro.add(new Area((int) celulasAdjacentes.xi(), y, 1, 1));
            perimetro.add(new Area((int) celulasAdjacentes.xi() + (int) celulasAdjacentes.xf() - 1, y, 1, 1));
        }

        return perimetro;
    }

    private boolean isSobreposto() {
        return Ecossistema.getEcossistema().countSobreposicoes(this) > 0;
    }

    private void sobreposto() {
        double energiaPerdida = Ecossistema.getEcossistema().countSobreposicoes(this)
                * Ecossistema.getForcaPerdidaFlora();
        if(energiaPerdida == 0) {
            handleEvent(FloraEvents.SOBREPOSICAO_TERMINOU);
        } else {
            this.forca -= energiaPerdida;
        }
    }

    private void morrer() {
        Ecossistema.getEcossistema().removeElemento(this);
    }

    public Flora(int x, int y, int xi, int yi, double forca) {
        id = counter++;
        area = new Area(x, y, yi, xi);
        setState(FloraState.VIVO);
        this.forca = forca;
        this.forcaPorTick = 0.5;
    }
}
