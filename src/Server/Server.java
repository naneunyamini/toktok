package Server;

import Server.Function.PaintHandler;
import Server.Function.ClientHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class Server {
    private Socket clientSocket1;
    private Socket clientSocket2;
    private Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
    private Set<PaintHandler> paintHandlers = Collections.synchronizedSet(new HashSet<>());
    private PrintWriter out;

    // 채팅 서버 시작 메서드
    public void start1() {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("채팅 서버가 시작되었습니다. 클라이언트의 연결을 기다립니다.");

            while (true) {
                clientSocket1 = serverSocket.accept();
                out = new PrintWriter(clientSocket1.getOutputStream(), true);
                ClientHandler clientHandler = new ClientHandler(clientSocket1, this, out);
                clientHandlers.add(clientHandler);
                System.out.println("클라이언트가 연결되었습니다.");
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 그림판 서버 시작 메서드
    public void start2() {
        try (ServerSocket serverSocket = new ServerSocket(8889)) {
            System.out.println("그림판 서버가 시작되었습니다. 클라이언트의 연결을 기다립니다.");

            while (true) {
                clientSocket2 = serverSocket.accept();
                out = new PrintWriter(clientSocket2.getOutputStream(), true);
                PaintHandler paintHandler = new PaintHandler(clientSocket2, this, out);
                paintHandlers.add(paintHandler);
                System.out.println("클라이언트가 연결되었습니다.");
                paintHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 채팅 메시지 브로드캐스트 메서드
    public void broadcastMessage(String message) {
        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(message);
        }
    }

    // 그림 메시지 브로드캐스트 메서드
    public void broadcastPaintMessage(String message) {
        for (PaintHandler handler : paintHandlers) {
            handler.sendMessage(message);
        }
    }

    // 메인 메서드
    public static void main(String[] args) {
        // 채팅 서버와 그림판 서버를 각각 별도의 스레드로 실행
        new Thread(() -> {
            Server chatServer = new Server();
            chatServer.start1();
        }).start();

        new Thread(() -> {
            Server paintServer = new Server();
            paintServer.start2();
        }).start();
    }

    // 클라이언트 핸들러 제거 메서드
    public void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

