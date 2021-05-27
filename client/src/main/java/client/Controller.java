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

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    private String login;
    private String newNickname;
    private Stage stage;
    private Stage regStage;
    private Stage changeNicknameStage;
    private RegController regController;
    private ChangeNickController chnController;
    private File logFile;
    private OutputStream outStream;

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
            login = "";
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

                    logFile = new File("history_" + login + ".txt");
                    if(login != "" )
                    {

                        RandomAccessFile raFile = new RandomAccessFile( logFile, "r" );
                        long fPos = raFile.length() - 1;

                        if(fPos > 0)
                        {

                            final int MSG_VALUE = 100;
                            int counter = 0;

                            while( counter < MSG_VALUE + 1 && fPos >= 0) {
                                raFile.seek(fPos--);
                                byte ch = raFile.readByte();
                                if( 0xA == ch ) {
                                    counter++;
                                }
                            }

                            fPos = fPos < 0 ? 0 : fPos + 2; // go to position after: "<any_char>\n"
                            raFile.seek(fPos);

                            StringBuffer stringBuffer = new StringBuffer();
                            long size = raFile.length() - fPos;

                            byte[] bArray = new byte[(int)size];
                            raFile.read( bArray, (int)fPos, (int)size );
                            String latestMessages = new String( bArray, StandardCharsets.UTF_8 );

                            textArea.appendText( latestMessages );


                        }
                    }

                    if(outStream == null) {
                        outStream = new BufferedOutputStream( new FileOutputStream( logFile, true ));
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
                            String msg = str + "\n";
                            textArea.appendText(msg);

                            outStream.write( msg.getBytes(StandardCharsets.UTF_8), 0, msg.getBytes().length );

                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    setAuthenticated(false);
                    try {
                        if(outStream != null) {
                            outStream.flush();
                            outStream.close();
                        }
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
            login = loginField.getText();
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
