package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import main.java.Algorithm;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public TextArea textArea;
    public TextArea result;
    Algorithm algorithm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithm = new Algorithm(3);
        doSymbols();
    }

    public void doSymbols() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                string.append(algorithm.ground[i][j] + "\t");
            }
            string.append("\n");
        }
        textArea.setText(String.valueOf(string));
    }

    public void leftUp(ActionEvent actionEvent) {
        String currentResult = algorithm.makeMove(3);
        if(currentResult != null)
            result.setText(currentResult);
        else result.clear();
        doSymbols();
    }

    public void rightBottom(ActionEvent actionEvent) {
        String currentResult = algorithm.makeMove(0);
        if(currentResult != null)
            result.setText(currentResult);
        else result.clear();
        doSymbols();

    }

    public void leftBottom(ActionEvent actionEvent) {
        String currentResult = algorithm.makeMove(2);
        if(currentResult != null)
            result.setText(currentResult);
        else result.clear();
        doSymbols();

    }

    public void rightUp(ActionEvent actionEvent) {
        String currentResult = algorithm.makeMove(1);
        if(currentResult != null)
            result.setText(currentResult);
        else result.clear();
        doSymbols();
    }
}
