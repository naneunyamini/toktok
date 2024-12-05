package Client.Event;

import Client.Form.SignUpForm;
import Client.Form.LoginForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class SignUpCancelEvent implements ActionListener {
    private SignUpForm signUpForm;   // 회원가입 폼
    private Socket socket;           // 클라이언트 소켓

    // 생성자: 회원가입 폼과 클라이언트 소켓을 매개변수로 받음
    public SignUpCancelEvent(SignUpForm signUpForm, Socket socket) {
        this.socket = socket;
        this.signUpForm = signUpForm;
    }

    // ActionListener 인터페이스의 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        // 회원가입 취소 이벤트 발생 시, 새로운 로그인 폼을 생성하고 현재의 회원가입 폼을 닫음
        new LoginForm(socket);
        signUpForm.dispose();
    }
}

