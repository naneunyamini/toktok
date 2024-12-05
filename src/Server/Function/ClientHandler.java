package Server.Function;

import Server.Server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler extends Thread {
    private Socket clientSocket;        // 클라이언트 소켓
    private BufferedReader in;         // 클라이언트로부터의 입력 스트림
    private PrintWriter out;            // 클라이언트로의 출력 스트림
    private MessageHandler messageHandler;  // 클라이언트로부터의 메시지를 처리하는 핸들러
    private Server server;              // 서버 객체

    public ClientHandler(Socket clientSocket, Server server, PrintWriter out) throws IOException, SQLException {
        this.clientSocket = clientSocket;
        this.server = server;
        this.out = out;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.messageHandler = new MessageHandler(in, out, this);
    }

    public void run() {
        try {
            String clientMessage;
            // 클라이언트로부터의 메시지를 읽어오고 처리합니다.
            while ((clientMessage = in.readLine()) != null) {
                messageHandler.processClientMessage(clientMessage);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // 클라이언트와의 연결이 해제되면 소켓 및 스트림을 닫고 데이터베이스 연결을 종료한다.
                clientSocket.close();
                in.close();
                out.close();
                messageHandler.closeDatabaseConnection();
                System.out.println("클라이언트와의 연결이 해제되었습니다.");

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public Server getChatServer() {
        return server;
    }

    public void removeClientFromChat() {
        try {
            server.removeClientHandler(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}