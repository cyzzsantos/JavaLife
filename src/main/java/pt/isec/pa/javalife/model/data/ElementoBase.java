package pt.isec.pa.javalife.model.data;

public abstract sealed class ElementoBase implements IElemento permits Inanimado, Flora, Fauna {
    protected int id;
    protected Area area;
}