package server;

import commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nickname;
    private String login;

    public ClientHandler(Server server, Socket socket, Logger logger) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    // установка сокет тайм аут
//                    socket.setSoTimeout(5000);

                    // цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

                        //если команда отключиться
                        if (str.equals(Command.END)) {
                            out.writeUTF(Command.END);
                            logger.info("Клиент захотел отключиться");
                            throw new RuntimeException("Клиент захотел отключиться");
                        }

                        //если команда аутентификация
                        if (str.startsWith(Command.AUTH)) {
                            String[] token = str.split("\\s", 3);
                            if (token.length < 3) {
                                continue;
                            }
                            String newNick = server.getAuthService()
                                    .getNicknameByLoginAndPassword(token[1], token[2]);
                            login = token[1];
                            if (newNick != null) {
                                if (!server.isLoginAuthenticated(login)) {
                                    nickname = newNick;
                                    sendMsg(Command.AUTH_OK + " " + nickname);
                                    server.subscribe(this);
                                    System.out.println("client: " + socket.getRemoteSocketAddress() +
                                            " connected with nick: " + nickname);
                                    break;
                                } else {
                                    logger.info("Данная учетная запись уже используется");
                                    sendMsg("Данная учетная запись уже используется");
                                }
                            } else {
                                logger.info("Неверный логин/пароль");
                                sendMsg("Неверный логин / пароль");
                            }
                        }

                        //если команда регистрация
                        if (str.startsWith(Command.REG)) {
                            logger.info("Клиент реистрируется");
                            String[] token = str.split("\\s", 4);
                            if (token.length < 4) {
                                continue;
                            }
                            boolean regSuccess = server.getAuthService()
                                    .registration(token[1], token[2], token[3]);
                            if (regSuccess) {
                                logger.info("Регистрация прошла успешно");
                                sendMsg(Command.REG_OK);
                            } else {
                                logger.info("Не удалось пройти регистрацию");
                                sendMsg(Command.REG_NO);
                            }
                        }




                    }
                    //цикл работы
                    while (true) {
                        String str = in.readUTF();

                        //если команда смены никнейма
                        if (str.startsWith(Command.CHNG_NICK)) {
                            logger.info("Клиент запрашивает смену ника");
                            String[] token = str.split("\\s", 3);
                            if (token.length < 3) {
                                continue;
                            }
                            boolean regSuccess = server.getAuthService()
                                    .changeNickname(token[1], token[2]);
                            if (regSuccess) {
                                logger.info("Смена ника прошла успешно");
                                sendMsg(Command.CHNG_NICK_OK);
                            } else {
                                logger.info("Не удалось сменить ник");
                                sendMsg(Command.CHNG_NICK_NO);
                            }
                        }

                        if (str.startsWith("/")) {
                            if (str.equals(Command.END)) {
                                logger.info("Клиент отключается");
                                out.writeUTF(Command.END);
                                break;
                            }

                            if (str.startsWith(Command.PRIVATE_MSG)) {
                                String[] token = str.split("\\s", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                logger.info("Клиент отправил приватное сообщение");
                                server.privateMsg(this, token[1], token[2]);
                            }
                        } else {
                            logger.info("Клиент отправил сообщение");
                            server.broadcastMsg(this, str);
                        }
                    }
                    //SocketTimeoutException
                } catch (RuntimeException e) {
                    logger.severe("Клиент аварийно завершил соединение");
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    logger.severe("Клиент аварийно завершил соединение");
                    e.printStackTrace();
                } finally {
                    logger.info("Клиент отключился");
                    server.unsubscribe(this);
                    System.out.println("Client disconnected: " + nickname);
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

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
