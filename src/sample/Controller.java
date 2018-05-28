package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import main.java.Algorithm;
import main.java.Position;
import main.java.gameUseful;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public TextArea logTextArea;
    public GridPane gameGround;
    public ToggleGroup playerSelect;
    public Button startGameBtn;
    public ChoiceBox gameLevelSelect;
    Algorithm algorithm;
    public gameUseful.player currentPlayer;
    public Position currentPlayerPosition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initGameState();
    }

    public void initGameState() {
        playerSelect.getToggles().get(0).setUserData(gameUseful.player.Wolf);
        playerSelect.getToggles().get(1).setUserData(gameUseful.player.Rabbit);

        ObservableList<Integer> levels = FXCollections.observableArrayList(1, 2, 3);
        gameLevelSelect.setItems(levels);
        gameLevelSelect.setValue(1);
        startGameBtn.setOnMouseClicked((MouseEvent event) -> {
            Integer gmLvl = (Integer) gameLevelSelect.getValue();
            logTextArea.clear();
            gameUseful.player player = (gameUseful.player) playerSelect.getSelectedToggle().getUserData();
            startGame(gmLvl, player);
        });
    }

    public void updateGround(Character[][] ground) {
        gameGround.getChildren().clear();
        for (int i = 0; i < ground.length; i++) {
            Character[] row = ground[i];
            for (int j = 0; j < row.length; j++) {
                Character item = row[j];

                Button btn = new Button();
                String styleClass;

                switch (item) {
                    case 'W': {
                        styleClass = "wolf";
                        break;
                    }
                    case 'R': {
                        styleClass = "rabbit";
                        break;
                    }
                    case '*': {
                        styleClass = "btn-black";
                        break;
                    }
                    case '0': {
                        styleClass = "btn-white";
                        break;
                    }
                    default: {
                        styleClass = "";
                        break;
                    }
                }


                btn.getStyleClass().add(styleClass);
                btn.setId(i + ";" + j);

                btn.setOnMouseClicked((MouseEvent event) -> {
                    String btnId = ((Control) event.getSource()).getId();
                    if (currentPlayer == gameUseful.player.Rabbit) {
                        currentPlayerPosition = algorithm.findRabbit();
                    }

                    if (currentPlayer == gameUseful.player.Rabbit || currentPlayerPosition != null) {
                        checkBtn(btnId);
                    } else {
                        selectPlayer(btnId);
                    }
                });
                gameGround.add(btn, j, i);
            }
        }

    }

    public void selectPlayer(String btnId) {
        if (!notTargetBtn(btnId, "wolf")) {
            currentPlayerPosition = parseBtnId(btnId);
            setClassTargetBtn(btnId, "wolf-selected");
        } else {
            currentPlayerPosition = null;
            unsetClassTargetBtn(btnId, "wolf-selected");
        }
    }

    public void startGame(Integer lvl, gameUseful.player player) {
        currentPlayer = player;
        algorithm = new Algorithm(lvl);
        updateGround(algorithm.ground);
    }

    public Position parseBtnId(String btnId) {
        String[] splitedId = btnId.split(";");
        Position btnPosition = new Position();

        btnPosition.i = Integer.parseInt(splitedId[0]);
        btnPosition.j = Integer.parseInt(splitedId[1]);

        return btnPosition;
    }

    public void checkBtn(String btnId) {
        Position btnPosition = parseBtnId(btnId);

        Position player = currentPlayerPosition.clone();
        Integer vecX = btnPosition.j - player.j;
        Integer vecY = btnPosition.i - player.i;
        if(vecX == 0 && vecY == 0 && currentPlayer == gameUseful.player.Rabbit) {
            return;
        }

        if (Math.abs(vecX) < 2 && Math.abs(vecY) < 2 && notTargetBtn(btnId, "btn-white")) {
            if (vecX < 0) {
                if (vecY > 0) {
                    leftBottom();
                } else if (currentPlayer != gameUseful.player.Wolf) {
                    leftUp();
                }
            } else {
                if (vecY > 0) {
                    rightBottom();
                } else if (currentPlayer != gameUseful.player.Wolf) {
                    rightUp();
                }
            }

            currentPlayerPosition = null;
            updateGround(algorithm.ground);
        }

    }

    public boolean notTargetBtn(String btnId, String cssClass) {
        final boolean[] notWhite = {true};

        gameGround.getChildren().forEach(child -> {
            if (child.getId().equals(btnId) && child.getStyleClass().contains(cssClass)) {
                notWhite[0] = false;
            }
        });

        return notWhite[0];
    }

    public void setClassTargetBtn(String btnId,String cssClass) {
        gameGround.getChildren().forEach(child -> {
            if (child.getId().equals(btnId)) {
                child.getStyleClass().add(cssClass);
            }
        });
    }

    public void unsetClassTargetBtn(String btnId,String cssClass) {
        gameGround.getChildren().forEach(child -> {
            if (child.getId().equals(btnId)) {
                child.getStyleClass().remove(cssClass);
            }
        });
    }

    public void leftUp() {
        String currentResult = algorithm.makeMove(gameUseful.move.leftUp.ordinal(), currentPlayerPosition);
        updateLog(currentResult);
    }

    public void rightBottom() {
        String currentResult = algorithm.makeMove(gameUseful.move.rightBottom.ordinal(), currentPlayerPosition);
        updateLog(currentResult);
    }

    public void leftBottom() {
        String currentResult = algorithm.makeMove(gameUseful.move.leftBottom.ordinal(), currentPlayerPosition);
        updateLog(currentResult);
    }

    public void rightUp() {
        String currentResult = algorithm.makeMove(gameUseful.move.rightUp.ordinal(), currentPlayerPosition);
        updateLog(currentResult);
    }

    public void updateLog(String text) {
        String t = logTextArea.getText() + "\n" + text;

        String[] lines = t.split("\n");
        t = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.equals("")) {
                t += line + "\n";
            }
        }

        logTextArea.setText(t);
    }
}
