package Client.Event;

import Client.Form.WaitingForm;
import Client.Form.ChatRoomForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatRoomEnterEvent implements ActionListener {
    private WaitingForm waitingForm;      // 대기 화면을 나타내는 폼
    private String loggedInUserName;      // 현재 로그인한 사용자의 이름
    private Socket socket;                // 클라이언트 소켓
    private ChatRoomForm chatRoomForm;    // 채팅방 화면을 나타내는 폼

    // 생성자: 대기 화면, 소켓, 로그인한 사용자의 이름을 매개변수로 받음
    public ChatRoomEnterEvent(WaitingForm waitingForm, Socket socket, String loggedInUserName) {
        this.waitingForm = waitingForm;
        this.socket = socket;
        this.loggedInUserName = loggedInUserName;
    }

    // ActionListener 인터페이스의 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // 클라이언트 채팅방 폼 생성
            chatRoomForm = new ChatRoomForm(socket, loggedInUserName);

            // 서버에 입장 메시지 전송
            String enterChatRoomMessage = "ENTER_CHAT_ROOM " + loggedInUserName;
            sendMessage(socket, enterChatRoomMessage);

            // 서버로부터 사용자 목록을 받아와 채팅방 폼에 설정
            String message = receiveMessage(socket);
            chatRoomForm.setUserList(message);

            // 대기 화면 닫기
            waitingForm.dispose();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // 메시지를 소켓을 통해 서버에 전송하는 메서드
    private void sendMessage(Socket socket, String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    // 소켓을 통해 서버로부터 메시지를 받아오는 메서드
    private String receiveMessage(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }
}
