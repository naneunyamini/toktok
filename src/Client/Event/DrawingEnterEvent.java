package Client.Event;

import Client.Form.ChatRoomForm;
import Client.Form.DrawingForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class DrawingEnterEvent implements ActionListener {
    private ChatRoomForm chatRoomForm;   // 채팅방 폼
    private Socket socket;               // 클라이언트 소켓
    private DrawingForm drawingForm;   // 그림 그리기 폼

    // 생성자: 채팅방 폼과 클라이언트 소켓을 매개변수로 받음
    public DrawingEnterEvent(ChatRoomForm chatRoomForm, Socket socket) {
        this.socket = socket;
        this.chatRoomForm = chatRoomForm;
    }

    // ActionListener 인터페이스의 메서드 구현
    public void actionPerformed(ActionEvent e) {
        // 그림 그리기 폼이 생성되지 않은 경우
        if (drawingForm == null) {
            // 클라이언트 소켓의 IP 주소를 얻어와서 새로운 소켓을 생성하여 그림 그리기 폼을 염
            String ipAddress = socket.getInetAddress().getHostAddress();
            try {
                drawingForm = new DrawingForm(new Socket(ipAddress, 8889));
            } catch (IOException ex) {
                // IOException이 발생하면 런타임 예외로 감싸서 던짐
                throw new RuntimeException(ex);
            }
        } else {
            // 이미 그림 그리기 폼이 열려있는 경우 다시 열기
            drawingForm.reopen();
        }
    }
}

