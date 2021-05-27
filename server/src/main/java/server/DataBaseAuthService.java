package server;

import java.sql.*;

public class DataBaseAuthService implements AuthService {

    private Connection connection;
    private Statement statement;

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        statement = connection.createStatement();
        System.out.println("DatabaseConnected\n");
    }

    public void disconnect() throws SQLException{
        statement.close();
        connection.close();
    }


    public boolean nicknameIsExist(String nickname) {
        boolean result = false;
        try{
            ResultSet resultSet = statement.executeQuery("SELECT nickname FROM clients WHERE nickname = \"" +
                    nickname + "\"");

            if(resultSet.next()) {
                result = resultSet.getString("nickname") != null;
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean changeNickname(String oldNickname, String newNickname) {
        boolean result = false;
        if( ! nicknameIsExist(newNickname) ) {

            try {
                statement.executeUpdate("UPDATE clients SET nickname = \"" + newNickname +
                        "\" WHERE nickname = \"" + oldNickname + "\"");
                result = true;
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {

        String result = null;
        try{
            ResultSet resultSet = statement.executeQuery("SELECT nickname FROM clients WHERE login = \"" +
                    login + "\" AND password = \"" + password + "\"");

            if(resultSet.next()) {
                result = resultSet.getString("nickname");
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        boolean status = false;
        if( getNicknameByLoginAndPassword(login, password) == null){
            try {
                statement.executeUpdate("INSERT INTO clients (login, nickname, password) VALUES ('" + login +
                        "', '" + nickname + "', '" + password + "');");

                status = true;
            } catch ( SQLException e) {
                e.printStackTrace();
            }

        }

        return status;
    }
}
