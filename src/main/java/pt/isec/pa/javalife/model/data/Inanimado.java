package pt.isec.pa.javalife.model.data;

public final class Inanimado extends ElementoBase {

    private static int counter;
    private final String cor;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Elemento getType() {
        return Elemento.INANIMADO;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public void evolve() {

    }

    public String getCor() {
        return cor;
    }

    public Inanimado(int x, int y, int yi, int xi) {
        id = counter++;
        area = new Area(x, y, yi, xi);
        cor = "#555555";
    }
}
