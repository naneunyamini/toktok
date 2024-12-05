package Client.Event;

import Client.Form.LoginForm;
import Client.Form.WaitingForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class LogoutEvent implements ActionListener {
    private WaitingForm waitingForm;   // 대기 화면 폼
    private Socket socket;              // 클라이언트 소켓

    // 생성자: 대기 화면 폼과 클라이언트 소켓을 매개변수로 받음
    public LogoutEvent(WaitingForm waitingForm, Socket socket) {
        this.socket = socket;
        this.waitingForm = waitingForm;
    }

    // ActionListener 인터페이스의 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        // 로그아웃 이벤트 발생 시, 새로운 로그인 폼을 생성하고 대기 화면을 닫음
        new LoginForm(socket);
        waitingForm.dispose();
    }
}

