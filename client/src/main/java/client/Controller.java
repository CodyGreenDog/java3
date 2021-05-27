package client;

import commands.Command;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox authPanel;
    @FXML
    public VBox msgPanel;
    @FXML
    public ListView<String> clientList;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final int PORT = 8189;
    private final String IP_ADDRESS = "localhost";

    private boolean authenticated;
    private String nickname;
    private String newNickname;
    private Stage stage;
    private Stage regStage;
    private Stage changeNicknameStage;
    private RegController regController;
    private ChangeNickController chnController;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);

        if (!authenticated) {
            nickname = "";
        }
        textArea.clear();
        setTitle(nickname);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = (Stage) textArea.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("bye");
                if (socket != null && !socket.isClosed()) {
                    try {
                        out.writeUTF(Command.END);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        setAuthenticated(false);
    }

    private void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    // цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals(Command.END)) {
                                throw new RuntimeException("Сервак нас отключает");
                            }
                            if (str.startsWith(Command.AUTH_OK)) {
                                String[] token = str.split("\\s");
                                nickname = token[1];
                                setAuthenticated(true);
                                break;
                            }

                            if(str.equals(Command.REG_OK)){
                                regController.setResultTryToReg(Command.REG_OK);
                            }

                            if(str.equals(Command.REG_NO)){
                                regController.setResultTryToReg(Command.REG_NO);
                            }
                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }
                    //цикл работы
                    while (true) {
                        String str = in.readUTF();

                        if(str.equals(Command.CHNG_NICK_OK)){
                            chnController.setResultTryToChangeNickname(Command.CHNG_NICK_OK);
                            nickname = newNickname;
                            setTitle(nickname);
                        }

                        if(str.equals(Command.CHNG_NICK_NO)){
                            chnController.setResultTryToChangeNickname(Command.CHNG_NICK_NO);

                        }

                        if (str.startsWith("/")) {
                            if (str.equals(Command.END)) {
                                System.out.println("Client disconnected");
                                break;
                            }
                            if (str.startsWith(Command.CLIENT_LIST)) {
                                String[] token = str.split("\\s");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }

                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    setAuthenticated(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF(String.format("%s %s %s", Command.AUTH, loginField.getText().trim(), passwordField.getText().trim()));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            passwordField.clear();
        }
    }

    private void setTitle(String nickname) {
        Platform.runLater(() -> {
            if (nickname.equals("")) {
                stage.setTitle("Best chat of World");
            } else {
                stage.setTitle(String.format("Best chat of World - [ %s ]", nickname));
            }
        });
    }

    public void clientListMouseReleased(MouseEvent mouseEvent) {
        System.out.println(clientList.getSelectionModel().getSelectedItem());
        String msg = String.format("%s %s ", Command.PRIVATE_MSG, clientList.getSelectionModel().getSelectedItem());
        textField.setText(msg);
    }

    public void showRegWindow(ActionEvent actionEvent) {
        if (regStage == null) {
            initRegWindow();
        }
        regStage.show();
    }

    private void initRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();

            regController = fxmlLoader.getController();
            regController.setController(this);

            regStage = new Stage();
            regStage.setTitle("Best chat of World registration");
            regStage.setScene(new Scene(root, 450, 350));
            regStage.initStyle(StageStyle.UTILITY);
            regStage.initModality(Modality.APPLICATION_MODAL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registration(String login, String password, String nickname){
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF(String.format("%s %s %s %s", Command.REG, login, password, nickname));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showChangeNicknameWindow(ActionEvent actionEvent) {
        if (changeNicknameStage == null) {
            initChangeNicknameWindow();
        }
        changeNicknameStage.show();
    }

    private void initChangeNicknameWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chng_nick.fxml"));
            Parent root = fxmlLoader.load();

            chnController = fxmlLoader.getController();
            chnController.setController(this);

            changeNicknameStage = new Stage();
            changeNicknameStage.setTitle("Enter new nickname");
            changeNicknameStage.setScene(new Scene(root, 450, 350));
            changeNicknameStage.initStyle(StageStyle.UTILITY);
            changeNicknameStage.initModality(Modality.APPLICATION_MODAL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void changeNickname(String nickname) {
        try {
            out.writeUTF(String.format("%s %s %s", Command.CHNG_NICK, this.nickname, nickname));
            newNickname = nickname;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
