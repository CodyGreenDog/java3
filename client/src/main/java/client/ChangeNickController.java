package client;

import commands.Command;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChangeNickController {

    @FXML
    private TextField nicknameField;
    @FXML
    private TextArea textArea;

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setResultTryToChangeNickname(String command) {
        if (command.equals(Command.CHNG_NICK_OK)) {
            textArea.appendText("Смена никнейма прошла успешно\n");
        }
        if (command.equals(Command.CHNG_NICK_NO)) {
            textArea.appendText("Никнейм уже занят\n");
        }
    }

    public void tryToChangeNickname(ActionEvent actionEvent) {

        String nickname = nicknameField.getText().trim();

       controller.changeNickname(nickname);
    }
}
