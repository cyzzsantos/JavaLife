package pt.isec.pa.javalife.model.data;

import javafx.scene.image.Image;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public final class Fauna extends ElementoBase implements IElementoComForca, IElementoComImagem {
    private static int counter;
    private double forca;
    private String imagem;
    private FaunaState state;
    private Area targetFlora;
    private Area parceiro;
    private int ticksReproducao = 0;

    enum direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Elemento getType() {
        return Elemento.FAUNA;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public void evolve() {
        switch(state) {
            case EM_MOVIMENTO:
                if(noEnergy()) {
                    handleEvent(FaunaEvents.MORTE);
                    break;
                }
                if(hungry()) {
                    handleEvent(FaunaEvents.INICIAR_PROCURA_ALIMENTO_FLORA);
                    break;
                }
                if(forcaAlta()) {
                    handleEvent(FaunaEvents.INICIAR_REPRODUCAO);
                }

                movimento();
                break;

            case A_ALIMENTAR:
                if(noEnergy()) {
                    handleEvent(FaunaEvents.MORTE);
                    break;
                }
                alimentar();
                break;

            case A_REPRODUZIR:
                if(noEnergy()) {
                    handleEvent(FaunaEvents.MORTE);
                    break;
                }
                procurarParceiro();
                reproduzir();
                break;

            case PERSEGUE_ALIMENTO_FLORA:
                if(noEnergy()) {
                    handleEvent(FaunaEvents.MORTE);
                    break;
                }
                perseguirFlora();
                break;

            case MORTO:
                morre();
                break;
        }
    }

    public void handleEvent(FaunaEvents event) {
        switch (state) {
            case EM_MOVIMENTO:
                switch (event) {
                    case MORTE:
                        state = FaunaState.MORTO;
                        break;

                    case INICIAR_PROCURA_ALIMENTO_FLORA:
                        procuraAlimentoFlora();
                        break;

                    case FLORA_ENCONTRADA:
                        state = FaunaState.PERSEGUE_ALIMENTO_FLORA;
                        break;

                    case INICIAR_REPRODUCAO:
                        state = FaunaState.A_REPRODUZIR;
                        break;
                }
                break;

            case A_ALIMENTAR:
                switch(event) {
                    case FIM_ALIMENTACAO:
                        state = FaunaState.EM_MOVIMENTO;
                        break;
                }
                break;

            case PERSEGUE_ALIMENTO_FLORA:
                switch (event) {
                    case FLORA_ALCANCADA:
                        state = FaunaState.A_ALIMENTAR;
                        break;

                    case FIM_ALIMENTACAO:
                        state = FaunaState.EM_MOVIMENTO;
                        break;
                }
                break;

            case A_REPRODUZIR:
                switch(event) {
                    case REPRODUCAO:
                        criarCria();
                        state = FaunaState.EM_MOVIMENTO;
                        break;

                    case FIM_REPRODUCAO:
                        state = FaunaState.EM_MOVIMENTO;
                        break;
                }
                break;
        }
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

    private void criarCria() {
        Area celulasAdjacentes = new Area(this.area.xi() - 1, this.area.yi() - 1,
                this.area.yf() + 2, this.area.xf() + 2);
        Set<Area> perimetro = getPerimetro(celulasAdjacentes);

        Area pos;
        do {
            pos = (Area) perimetro.toArray()[(int) (Math.random() * perimetro.size())];
        } while(!Ecossistema.getEcossistema().getElementosAt((int) pos.xi(), (int) pos.yi()).isEmpty());

        Ecossistema.getEcossistema().addElemento(new Fauna((int) pos.xi(), (int) pos.yi(), 1, 1));
        this.forca -= 25;

        handleEvent(FaunaEvents.FIM_REPRODUCAO);
    }

    private void reproduzir() {
        if(parceiro == null) {
            handleEvent(FaunaEvents.FIM_REPRODUCAO);
            return;
        }

        if(ticksReproducao >= 10) {
            ticksReproducao = 0;
            handleEvent(FaunaEvents.REPRODUCAO);
            return;
        }

        Set<IElemento> s1 = Ecossistema.getEcossistema().getElementosFauna();
        Set<Fauna> s2 = new HashSet<>();

        for(IElemento e: s1) {
            Fauna f = (Fauna) e;
            s2.add(f);
        }
        s2.remove(this);

        try {
            s2.stream().filter(e -> Ecossistema.calcularDistancia(this, e) <= 5)
                    .max((e1, e2) -> (int) (e1.getForca() - e2.getForca())).get();
        } catch(NoSuchElementException e) {
            return;
        }

        ticksReproducao++;


        boolean filledRight = false, filledLeft = false, filledDown = false, filledUp = false;
        if(Ecossistema.getEcossistema().getElementoAt(area.xi() + 1, area.yi()) != null)
            filledRight = true;

        if(Ecossistema.getEcossistema().getElementoAt(area.xi() - 1, area.yi()) != null)
            filledLeft = true;

        if(Ecossistema.getEcossistema().getElementoAt(area.xi(), area.yi() + 1) != null)
            filledDown = true;

        if(Ecossistema.getEcossistema().getElementoAt(area.xi(), area.yi() - 1) != null)
            filledUp = true;

        if(area.xi() < parceiro.xi()) {
            if (filledRight && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi() + 1, area.yi()).getType() != Elemento.INANIMADO) {
                moveRight();
                return;
            }
            if(!filledRight) {
                moveRight();
            }

        } else if(area.xi() > parceiro.xi()) {
            if (filledLeft && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi() - 1, area.yi()).getType() != Elemento.INANIMADO) {
                moveLeft();
                return;
            }
            if(!filledLeft) {
                moveLeft();
            }

        } else if(area.yi() < parceiro.yi()) {
            if (filledDown && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi(), area.yi() + 1).getType() != Elemento.INANIMADO) {
                moveDown();
                return;
            }
            if(!filledDown) {
                moveDown();
            }
        } else if(area.yi() > parceiro.yi()) {
            if (filledUp && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi(), area.yi() - 1).getType() != Elemento.INANIMADO) {
                moveUp();
                return;
            }
            if(!filledUp) {
                moveUp();
            }
        }
    }

    private void procurarParceiro() {
        Set<IElemento> s1 = Ecossistema.getEcossistema().getElementosFauna();
        Set<Fauna> s2 = new HashSet<>();
        Fauna temp;

        for(IElemento e: s1) {
            Fauna f = (Fauna) e;
            s2.add(f);
        }
        s2.remove(this);

        try {
            temp = s2.stream().max((e1, e2) -> (int) (e1.getForca() - e2.getForca())).get();
        } catch(NoSuchElementException e) {
            return;
        }

        parceiro = temp.getArea();

        handleEvent(FaunaEvents.INICIAR_REPRODUCAO);
    }

    private void movimento() {
        do {
            direction dir = direction.values()[(int) (Math.random() * 4)];
            boolean filledRight = false, filledLeft = false, filledDown = false, filledUp = false;
            if(Ecossistema.getEcossistema().getElementoAt(area.xi() + 1, area.yi()) != null)
                filledRight = true;

            if(Ecossistema.getEcossistema().getElementoAt(area.xi() - 1, area.yi()) != null)
                filledLeft = true;

            if(Ecossistema.getEcossistema().getElementoAt(area.xi(), area.yi() + 1) != null)
                filledDown = true;

            if(Ecossistema.getEcossistema().getElementoAt(area.xi(), area.yi() - 1) != null)
                filledUp = true;

            switch (dir) {
                case RIGHT:
                    if (filledRight && Ecossistema.getEcossistema()
                            .getElementoAt(area.xi() + 1, area.yi()).getType() != Elemento.INANIMADO) {
                        moveRight();
                        return;
                    }
                    if(!filledRight) {
                        moveRight();
                        return;
                    }
                case LEFT:
                    if (filledLeft && Ecossistema.getEcossistema()
                            .getElementoAt(area.xi() - 1, area.yi()).getType() != Elemento.INANIMADO) {
                        moveLeft();
                        return;
                    }
                    if(!filledLeft) {
                        moveLeft();
                        return;
                    }
                    break;
                case UP:
                    if (filledUp && Ecossistema.getEcossistema()
                            .getElementoAt(area.xi(), area.yi() - 1).getType() != Elemento.INANIMADO) {
                        moveUp();
                        return;
                    }
                    if(!filledUp) {
                        moveUp();
                        return;
                    }
                    break;
                case DOWN:
                    if(filledDown && Ecossistema.getEcossistema()
                            .getElementoAt(area.xi(), area.yi() + 1).getType() != Elemento.INANIMADO) {
                        moveDown();
                        return;
                    }
                    if(!filledDown) {
                        moveDown();
                        return;
                    }
                    break;
            }
        } while (true);
    }

    private void moveUp() {
        area = new Area(area.xi(), area.yi() - 1, area.xf(), area.yf());
        forca = Math.max(forca - Ecossistema.getForcaMovimentoFauna(), 0);
    }

    private void moveDown() {
        area = new Area(area.xi(), area.yi() + 1, area.xf(), area.yf());
        forca = Math.max(forca - Ecossistema.getForcaMovimentoFauna(), 0);
    }

    private void moveLeft() {
        area = new Area(area.xi() - 1, area.yi(), area.xf(), area.yf());
        forca = Math.max(forca - Ecossistema.getForcaMovimentoFauna(), 0);
    }

    private void moveRight() {
        area = new Area(area.xi() + 1, area.yi(), area.xf(), area.yf());
        forca = Math.max(forca - Ecossistema.getForcaMovimentoFauna(), 0);
    }

    private void alimentar() {
        if(forca >= 100)
            handleEvent(FaunaEvents.FIM_ALIMENTACAO);

        if(Ecossistema.getEcossistema().getElementosFlora().stream()
                .noneMatch(e -> e.getArea().contains(area))) {
            handleEvent(FaunaEvents.INICIAR_PROCURA_ALIMENTO_FAUNA);
        }

        forca = Math.min(forca + Ecossistema.getForcaPerdidaFlora(), 100);
    }

    private void morre() {
        Ecossistema.getEcossistema().removeElemento(this);
    }

    private boolean noEnergy() {
        return forca <= 0;
    }

    private boolean hungry() {
        return forca < 35;
    }

    private boolean forcaAlta() {
        return forca > 50;
    }

    private void procuraAlimentoFlora() {
        int iteration = 1;
        do {
            if(forca >= 80) {
                handleEvent(FaunaEvents.FIM_ALIMENTACAO);
                return;
            }

            Area areaProcura = new Area(area.xi() - iteration, area.yi() - iteration,
                    area.yf() + 2*iteration, area.xf() + 2*iteration);

            for(IElemento e : Ecossistema.getEcossistema().getElementosFlora()) {
                if(areaProcura.contains(e.getArea())) {
                    targetFlora = e.getArea();
                    handleEvent(FaunaEvents.FLORA_ENCONTRADA);
                    return;
                }
            }

            iteration++;
        } while (true);
    }

    private void perseguirFlora() {
        boolean filledRight = false, filledLeft = false, filledDown = false, filledUp = false;
        if(Ecossistema.getEcossistema().getElementoAt(area.xi() + 1, area.yi()) != null)
            filledRight = true;

        if(Ecossistema.getEcossistema().getElementoAt(area.xi() - 1, area.yi()) != null)
            filledLeft = true;

        if(Ecossistema.getEcossistema().getElementoAt(area.xi(), area.yi() + 1) != null)
            filledDown = true;

        if(Ecossistema.getEcossistema().getElementoAt(area.xi(), area.yi() - 1) != null)
            filledUp = true;

        if(area.contains(targetFlora)) {
            handleEvent(FaunaEvents.FLORA_ALCANCADA);
            return;
        }

        if(area.xi() < targetFlora.xi()) {
            if (filledRight && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi() + 1, area.yi()).getType() != Elemento.INANIMADO) {
                moveRight();
                return;
            }
            if(!filledRight) {
                moveRight();
                return;
            }
        }

        if(area.xi() > targetFlora.xi()) {
            if (filledLeft && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi() - 1, area.yi()).getType() != Elemento.INANIMADO) {
                moveLeft();
                return;
            }
            if(!filledLeft) {
                moveLeft();
                return;
            }
        }

        if(area.yi() < targetFlora.yi()) {
            if (filledDown && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi(), area.yi() + 1).getType() != Elemento.INANIMADO) {
                moveDown();
                return;
            }
            if(!filledDown) {
                moveDown();
                return;
            }
        }

        if(area.yi() > targetFlora.yi()) {
            if (filledUp && Ecossistema.getEcossistema()
                    .getElementoAt(area.xi(), area.yi() - 1).getType() != Elemento.INANIMADO) {
                moveUp();
                return;
            }
            if(!filledUp) {
                moveUp();
            }
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

    public Fauna(int x, int y, int xi, int yi) {
        id = counter++;
        area = new Area(x, y, xi, yi);
        imagem = Ecossistema.getImagemFauna();
        state = FaunaState.EM_MOVIMENTO;
        forca = Ecossistema.getForcaInicialFauna();
    }
}