package Server.Function;

import Server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PaintHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Server server;

    // 생성자
    public PaintHandler(Socket clientSocket, Server server, PrintWriter out) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        this.out = out;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    // 스레드 실행 메서드
    public void run() {
        try {
            String clientMessage;
            // 클라이언트로부터 메시지를 읽어서 서버에 브로드캐스트
            while ((clientMessage = in.readLine()) != null) {
                server.broadcastPaintMessage(clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 클라이언트 소켓 및 입출력 스트림 닫기
                clientSocket.close();
                in.close();
                out.close();
                System.out.println("클라이언트와의 연결이 해제되었습니다.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 클라이언트에게 메시지 전송
    public void sendMessage(String message) {
        out.println(message);
    }
}
