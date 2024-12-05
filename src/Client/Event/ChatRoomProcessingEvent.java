package Client.Event;

import Client.Etc.RoundedButton;
import Client.Form.ChatRoomForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatRoomProcessingEvent implements ActionListener {
    private ChatRoomForm chatRoomForm;         // 채팅방 폼
    private Socket socket;                      // 클라이언트 소켓
    private String loggedInUserName;            // 현재 로그인한 사용자의 이름

    // 생성자: 채팅방 폼, 클라이언트 소켓, 로그인한 사용자의 이름을 매개변수로 받음
    public ChatRoomProcessingEvent(ChatRoomForm chatRoomForm, Socket socket, String loggedInUserName) {
        this.socket = socket;
        this.chatRoomForm = chatRoomForm;
        this.loggedInUserName = loggedInUserName;
    }

    // ActionListener 인터페이스의 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof RoundedButton) {
            // 이벤트 소스가 RoundedButton인 경우 실행
            RoundedButton sourceButton = (RoundedButton) e.getSource();

            // "보내기" 버튼인 경우 실행
            if (sourceButton.getText().equals("보내기")) {
                String message = chatRoomForm.getinputField().getText();

                // 입력된 메시지가 비어 있지 않은 경우 서버에 메시지 전송
                if (message != null && !message.isEmpty()) {
                    try {
                        sendMessage(socket, "CHATING " + loggedInUserName + " " + message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    // 메시지를 소켓을 통해 서버에 전송하는 메서드
    private void sendMessage(Socket socket, String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.newLine();
        writer.flush();
    }
}

