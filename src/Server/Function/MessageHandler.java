package Server.Function;
import Server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MessageHandler {
    private BufferedReader in;
    private PrintWriter out;
    private DatabaseManager databaseManager;
    private ClientHandler clientHandler;
    private Server server;
    private static Set<String> chatRoomUsers = Collections.synchronizedSet(new HashSet<>());

    // 생성자
    public MessageHandler(BufferedReader in, PrintWriter out, ClientHandler clientHandler) throws IOException, SQLException {
        this.in = in;
        this.out = out;
        this.clientHandler = clientHandler;
        this.databaseManager = new DatabaseManager();
        this.server = clientHandler.getChatServer();
    }

    // 클라이언트 메시지 처리 메서드
    public void processClientMessage(String message) throws IOException, SQLException {
        String[] tokens = message.split("\\s+");

        if (tokens.length > 0) {
            String command = tokens[0];

            switch (command) {
                // REGISTER: 사용자 등록
                case "REGISTER":
                    if (tokens.length == 4) {
                        String name = tokens[1];
                        String id = tokens[2];
                        String pw = tokens[3];
                        registerUser(name, id, pw);
                    }
                    break;

                // LOGIN: 사용자 로그인
                case "LOGIN":
                    if (tokens.length >= 3) {
                        String id = tokens[1];
                        String pw = tokens[2];
                        loginUser(id, pw);
                    }
                    break;

                // CHANGE_PASSWORD: 비밀번호 변경
                case "CHANGE_PASSWORD":
                    if (tokens.length == 3) {
                        String id = tokens[1];
                        String newPassword = tokens[2];
                        changePassword(id, newPassword);
                    }
                    break;

                // SIGN_OUT: 사용자 로그아웃
                case "SIGN_OUT":
                    if (tokens.length == 2) {
                        String id = tokens[1];
                        signOutUser(id);
                    }
                    break;

                // ENTER_CHAT_ROOM: 채팅방 입장
                case "ENTER_CHAT_ROOM":
                    if (tokens.length == 2) {
                        String userName = tokens[1];
                        enterChatRoom(userName);
                    }
                    break;

                // CHATING: 채팅 메시지 전송
                case "CHATING":
                    if (tokens.length >= 2) {
                        String username = tokens[1];
                        String chat = tokens[2];
                        chating(username, chat);
                    }
                    break;

                // EXIT_CHAT_ROOM: 채팅방 나가기
                case "EXIT_CHAT_ROOM":
                    if (tokens.length == 2) {
                        String userName = tokens[1];
                        exitChatRoom(userName);
                    }
                    break;

                // DRAWING: 그림 전송
                case "DRAWING":
                    if (tokens.length == 2) {
                        String drawingInfo = tokens[1];
                        drawing(drawingInfo);
                    }
                    break;

                // 기타: 알 수 없는 명령 처리
                default:
                    sendMessageToClient("Unknown command: " + command);
                    out.flush();
                    break;
            }
        }
    }

    // 사용자 등록
    private void registerUser(String name, String id, String pw) throws IOException {
        boolean isDuplicate = databaseManager.isIdDuplicate(id);

        if (isDuplicate) {
            sendMessageToClient("REGISTER_FAIL_DUPLICATE");
        } else {
            boolean isSuccess = databaseManager.registerUser(name, id, pw);

            if (isSuccess) {
                sendMessageToClient("REGISTER_SUCCESS");
            } else {
                sendMessageToClient("REGISTER_FAIL");
            }
        }
    }

    // 사용자 로그인
    private void loginUser(String id, String password) throws IOException {
        boolean userExists = databaseManager.doesUserExist(id);
        if (!userExists) {
            sendMessageToClient("LOGIN_ERROR_USER_NOT_FOUND");
            return;
        }

        boolean isLoginSuccessful = databaseManager.checkLogin(id, password);

        if (isLoginSuccessful) {
            String userName = databaseManager.getUserName(id);
            sendMessageToClient("LOGIN_SUCCESS " + userName);
        } else {
            sendMessageToClient("LOGIN_ERROR_INCORRECT_CREDENTIALS");
        }
    }

    // 비밀번호 변경
    private void changePassword(String id, String newPassword) throws IOException {
        String currentPassword = databaseManager.getCurrentPassword(id);

        if (currentPassword != null) {
            if (currentPassword.equals(newPassword)) {
                sendMessageToClient("CHANGE_PASSWORD_EQUAL");
            } else {
                boolean isPasswordChanged = databaseManager.changePassword(id, newPassword);

                if (isPasswordChanged) {
                    sendMessageToClient("CHANGE_PASSWORD_SUCCESS");
                } else {
                    sendMessageToClient("CHANGE_PASSWORD_FAIL");
                }
            }
        } else {
            sendMessageToClient("CHANGE_PASSWORD_GET_FAIL");
        }
    }

    // 사용자 로그아웃
    private void signOutUser(String id) {
        boolean isSignOutSuccessful = databaseManager.signOutUser(id);

        if (isSignOutSuccessful) {
            sendMessageToClient("SIGN_OUT_SUCCESS");
        } else {
            sendMessageToClient("SIGN_OUT_FAIL");
        }
    }

    // 채팅방 입장
    private void enterChatRoom(String userName) {
        chatRoomUsers.add(userName);
        updateChatRoomUsers();
        server.broadcastMessage(userName + "님이 채팅방에 입장하셨습니다.]");
    }

    // 채팅 메시지 전송
    private void chating(String username, String chat) {
        server.broadcastMessage(username + "] " + chat);
    }

    // 채팅방 나가기
    private void exitChatRoom(String userName) {
        chatRoomUsers.remove(userName);
        updateChatRoomUsers();
        server.broadcastMessage(userName + "님이 채팅방에서 나가셨습니다.]");
        clientHandler.removeClientFromChat();
    }

    // 채팅방 사용자 목록 업데이트
    private void updateChatRoomUsers() {
        StringBuilder userNames = new StringBuilder("USER_LIST ");
        for (String userName : chatRoomUsers) {
            userNames.append(userName).append(",");
        }
        userNames.setLength(userNames.length() - 1);
        server.broadcastMessage(userNames.toString());
    }

    // 그림 전송
    private void drawing(String drawingInfo) {
        server.broadcastMessage("DRAWING " + drawingInfo);
    }

    // 클라이언트에 메시지 전송
    private void sendMessageToClient(String message) {
        out.println(message);
        out.flush();
    }

    // 데이터베이스 연결 종료
    public void closeDatabaseConnection() throws SQLException {
        databaseManager.closeConnection();
    }
}
