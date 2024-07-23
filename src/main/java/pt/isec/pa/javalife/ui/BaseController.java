package pt.isec.pa.javalife.ui;

import pt.isec.pa.javalife.model.Facade;

public abstract class BaseController {
    protected Facade facade;

    public void setFacade(Facade facade) {
        this.facade = facade;
    }
}