package server;

import commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class Server {
    private final int PORT = 8189;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private ServerSocket server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private List<ClientHandler> clients;
    private DataBaseAuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new DataBaseAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started");
            logger.info("Сервер запущен");
            authService.connect();

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                System.out.println("client: " + socket.getRemoteSocketAddress());
                new ClientHandler(this, socket, logger);
            }

        } catch (IOException | SQLException | ClassNotFoundException e) {
            logger.warning("Произошел сбой при запуске/работе сервера");
            e.printStackTrace();
        } finally {
            logger.info("Начало корректного завершения работы сервера");
            try {
                logger.info("Disconnect");
                authService.disconnect();
            } catch (SQLException e) {
                logger.warning("Disconnect: процесс завершился некорректно");
                e.printStackTrace();
            }

            try {
                logger.info("Закрытие сокета");
                socket.close();
            } catch (IOException e) {
                logger.warning("Сокет закрыт некорректно");
                e.printStackTrace();
            }
            try {
                logger.info("Сервер завершает работу");
                server.close();
            } catch (IOException e) {
                logger.warning("Произошел сбой при завершении работы сервера");
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(ClientHandler sender, String msg){
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public void privateMsg(ClientHandler sender,String receiver, String msg){
        String message = String.format("[ %s ] to [ %s ]: %s", sender.getNickname(), receiver, msg);
        for (ClientHandler c : clients) {
            if(c.getNickname().equals(receiver)){
                c.sendMsg(message);
                if(!c.equals(sender)){
                    sender.sendMsg(message);
                }
                return;
            }
        }
        sender.sendMsg("not found user: "+ receiver);
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientlist();
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientlist();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isLoginAuthenticated(String login){
        for (ClientHandler c : clients) {
            if(c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public void broadcastClientlist(){
        StringBuilder sb = new StringBuilder(Command.CLIENT_LIST);

        for (ClientHandler c : clients) {
            sb.append(" ").append(c.getNickname());
        }

        String msg = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }
}
