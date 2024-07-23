package pt.isec.pa.javalife.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import pt.isec.pa.javalife.model.Facade;

import java.io.IOException;

public class JavaLifeUI extends Application {
    private static JavaLifeUI instance;
    private Stage stg;
    private Facade facade;
    private UIController uiController;
    private Parent uiRoot;

    public JavaLifeUI() {
        instance = this;
    }

    @Override
    public void start(Stage stage) throws IOException {
        facade = Facade.getFacade();
        stg = stage;
        stg.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui.fxml"));
        uiRoot = loader.load();

        BaseController controller = loader.getController();
        controller.setFacade(facade);
        uiController = (UIController) controller;

        stg.setTitle("JavaLife");
        stg.setScene(new Scene(uiRoot));
        stg.setOnCloseRequest(evt -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Sair");
            alert.setHeaderText("Pretende mesmo sair?");
            uiController.parar();
            alert.showAndWait().filter(r -> r != ButtonType.OK).ifPresent(r->evt.consume());
        });

        stg.show();
    }

    protected void changeStage(String fxml) throws IOException {
        // se fxml for ui.fxml, muda para o ui.fxml ja criado
        if (fxml.equals("ui.fxml")) {
            stg.getScene().setRoot(uiRoot);
            return;
        }

        FXMLLoader loader = new FXMLLoader(JavaLifeUI.class.getClassLoader().getResource(fxml));
        Parent pane = loader.load();

        BaseController controller = loader.getController();
        controller.setFacade(facade);

        stg.getScene().setRoot(pane);
    }

    public static JavaLifeUI getInstance() {
        return instance;
    }

    private void setUIController(UIController uiController) {
        this.uiController = uiController;
    }

    public void createEcossistema() {
        if (uiController != null) {
            uiController.createCanvas();
        }
    }

    public void updateEcossistema() {
        if (uiController != null) {
            uiController.updateCanvas();
        }
    }
}
