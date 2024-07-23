package pt.isec.pa.javalife.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import pt.isec.pa.javalife.model.Facade;

import java.io.IOException;

public class CriarController extends BaseController {
    @FXML
    private Button ButtonRetornar, ButtonCriar;
    @FXML
    private TextField comprimento, largura,
            forcaInicialFlora, forcaCriaFlora, forcaPerdidaFlora,
            forcaInicialFauna, forcaMovimentoFauna;
    @FXML
    private ColorPicker corFlora;
    @FXML
    private ImageView img1Fauna, img2Fauna, img3Fauna, imgSelected;
    @FXML
    private Rectangle img1Selected, img2Selected, img3Selected;
    @FXML
    private Text errorText;


    @FXML
    void retornar() throws IOException {
        JavaLifeUI.getInstance().changeStage("ui.fxml");
    }

    @FXML
    void criar() throws IOException {
        int comprimentoVal, larguraVal;
        double forcaInicialFloraVal, forcaCriaFloraVal, forcaPerdidaFloraVal, forcaInicialFaunaVal, forcaMovimentoFaunaVal;

        if(comprimento.getText().isEmpty()) {
            comprimento.setText("500");
        }

        if(largura.getText().isEmpty()) {
            largura.setText("500");
        }

        if(forcaInicialFlora.getText().isEmpty()) {
            forcaInicialFlora.setText("50");
        }

        if(forcaCriaFlora.getText().isEmpty()) {
            forcaCriaFlora.setText("10");
        }

        if(forcaPerdidaFlora.getText().isEmpty()) {
            forcaPerdidaFlora.setText("1");
        }

        if(forcaInicialFauna.getText().isEmpty()) {
            forcaInicialFauna.setText("50");
        }

        if(forcaMovimentoFauna.getText().isEmpty()) {
            forcaMovimentoFauna.setText("0.5");
        }

        try {
            comprimentoVal = Integer.parseInt(comprimento.getText());
            larguraVal = Integer.parseInt(largura.getText());
            forcaInicialFloraVal = Double.parseDouble(forcaInicialFlora.getText());
            forcaCriaFloraVal = Double.parseDouble(forcaCriaFlora.getText());
            forcaPerdidaFloraVal = Double.parseDouble(forcaPerdidaFlora.getText());
            forcaInicialFaunaVal = Double.parseDouble(forcaInicialFauna.getText());
            forcaMovimentoFaunaVal = Double.parseDouble(forcaMovimentoFauna.getText());
        } catch (NumberFormatException e) {
            errorText.setVisible(true);
            return;
        }

        if(imgSelected == null) {
            imgSelected = img1Fauna;
        }

        JavaLifeUI.getInstance().changeStage("ui.fxml");

        facade.buildEcossistema(
                comprimentoVal,
                larguraVal,
                forcaInicialFloraVal,
                forcaCriaFloraVal,
                forcaPerdidaFloraVal,
                corFlora.getValue().toString(),
                forcaInicialFaunaVal,
                forcaMovimentoFaunaVal,
                imgSelected.getImage().getUrl()
        );
    }

    @FXML
    void img1Clicked() {
        img1Selected.setVisible(true);
        img2Selected.setVisible(false);
        img3Selected.setVisible(false);
        imgSelected = img1Fauna;
    }

    @FXML
    void img2Clicked() {
        img1Selected.setVisible(false);
        img2Selected.setVisible(true);
        img3Selected.setVisible(false);
        imgSelected = img2Fauna;
    }

    @FXML
    void img3Clicked() {
        img1Selected.setVisible(false);
        img2Selected.setVisible(false);
        img3Selected.setVisible(true);
        imgSelected = img3Fauna;
    }

}