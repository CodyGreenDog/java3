package server;

public interface AuthService {
    String getNicknameByLoginAndPassword(String login, String password);
    boolean changeNickname(String oldNickname, String newNickname);
    boolean registration(String login, String password, String nickname);
}
