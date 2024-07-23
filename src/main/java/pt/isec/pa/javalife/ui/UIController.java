package pt.isec.pa.javalife.ui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import pt.isec.pa.javalife.model.data.Fauna;
import pt.isec.pa.javalife.model.data.Flora;
import pt.isec.pa.javalife.model.data.IElemento;
import pt.isec.pa.javalife.model.data.Inanimado;

import java.io.IOException;

public class UIController extends BaseController {
    @FXML
    private MenuItem MIcriar;

    @FXML
    private VBox scenePane;

    @FXML
    private GridPane ecossistemaGridPane;

    @FXML
    private Canvas ecossistemaCanvas;

    @FXML
    private Button inanimadoButton, floraButton, faunaButton, iniciarButton;

    @FXML
    private Text errorText;

    private Image image = null;

    private final int gridSize = 350;

    @FXML
    void criar() throws IOException {
        JavaLifeUI.getInstance().changeStage("criar.fxml");
    }

    @FXML
    void addInanimado() throws IOException {
        facade.addElementoInanimado();
    }

    @FXML
    void addFlora() {
        facade.addElementoFlora();
    }

    @FXML
    void addFauna() {
        facade.addElementoFauna();
    }

    @FXML
    void iniciar() {
        if(facade.ecossistemaExists()) {
            if(facade.isRunning()) {
                parar();
            } else {
                facade.start();
                iniciarButton.setText("Parar");
            }
            errorText.setVisible(false);
        } else {
            errorText.setVisible(true);
        }
    }

    void parar() {
        facade.stop();
        iniciarButton.setText("Iniciar");
    }

    public void updateCanvas() {
        double cellWidth = (double) gridSize / facade.getEcossistemaComprimento();
        double cellHeight = (double) gridSize / facade.getEcossistemaLargura();

        ecossistemaCanvas.setWidth(gridSize);
        ecossistemaCanvas.setHeight(gridSize);

        GraphicsContext gc = ecossistemaCanvas.getGraphicsContext2D();

        drawElements(cellWidth, cellHeight, gc);
    }

    private void drawElements(double cellWidth, double cellHeight, GraphicsContext gc) {
        drawBackground(cellWidth, cellHeight, gc);

        for(int i = 0; i < facade.getElementos().size(); i++) {
            IElemento elemento = (IElemento) facade.getElementos().toArray()[i];
            if (elemento instanceof Fauna) {
                Image image = new Image(((Fauna) elemento).getImagem());
                gc.drawImage(image, elemento.getArea().xi() * cellWidth, elemento.getArea().yi() * cellHeight,
                        elemento.getArea().xf() * cellWidth, elemento.getArea().yf() * cellHeight);
            } else if (elemento instanceof Flora) {
                gc.setFill(Color.web(facade.getCorFlora()).deriveColor(0, 1, 1, ((Flora) elemento).getForca() / 90));
                gc.fillRect(elemento.getArea().xi() * cellWidth, elemento.getArea().yi() * cellHeight,
                        elemento.getArea().xf() * cellWidth, elemento.getArea().yf() * cellHeight);
            } else
            if (elemento instanceof Inanimado) {
                gc.setFill(Paint.valueOf(((Inanimado) elemento).getCor()));
                gc.fillRect(elemento.getArea().xi() * cellWidth, elemento.getArea().yi() * cellHeight,
                        elemento.getArea().xf() * cellWidth, elemento.getArea().yf() * cellHeight);
            }
        }
    }

    private void drawBackground(double cellWidth, double cellHeight, GraphicsContext gc) {
        for (int i = 0; i < facade.getEcossistemaComprimento(); i++) {
            for (int j = 0; j < facade.getEcossistemaLargura(); j++) {
                gc.setFill(Paint.valueOf("#94c973"));
                gc.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    public void createCanvas() {
        ecossistemaCanvas.setWidth(gridSize);
        ecossistemaCanvas.setHeight(gridSize);

        double cellWidth = (double) gridSize / facade.getEcossistemaComprimento();
        double cellHeight = (double) gridSize / facade.getEcossistemaLargura();

        GraphicsContext gc = ecossistemaCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, gridSize, gridSize);

        drawBackground(cellWidth, cellHeight, gc);

        drawElements(cellWidth, cellHeight, gc);
    }

    private Color hexToColor(String hex) {
        return Color.web(hex);
    }
}
