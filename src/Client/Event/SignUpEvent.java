package Client.Event;

import Client.Form.LoginForm;
import Client.Form.SignUpForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class SignUpEvent implements ActionListener {
    private LoginForm loginForm;   // 로그인 폼
    private Socket socket;         // 클라이언트 소켓

    // 생성자: 로그인 폼과 클라이언트 소켓을 매개변수로 받음
    public SignUpEvent(LoginForm loginForm, Socket socket) {
        this.loginForm = loginForm;
        this.socket = socket;
    }

    // ActionListener 인터페이스의 메서드 구현
    public void actionPerformed(ActionEvent e) {
        // 회원가입 이벤트 발생 시, 새로운 회원가입 폼을 생성하고 현재의 로그인 폼을 닫음
        new SignUpForm(socket);
        loginForm.dispose();
    }
}
