package Client.Event;

import Client.Form.WaitingForm;
import Client.Form.PwResetForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class PwResetEvent implements ActionListener {
    private WaitingForm waitingForm;    // 대기 화면 폼
    private String loggedInUserName;    // 현재 로그인한 사용자의 이름
    private String loggedInId;          // 현재 로그인한 사용자의 아이디
    private Socket socket;              // 클라이언트 소켓

    // 생성자: 대기 화면 폼, 클라이언트 소켓, 로그인한 사용자의 이름과 아이디를 매개변수로 받음
    public PwResetEvent(WaitingForm waitingForm, Socket socket, String loggedInUserName, String loggedInId) {
        this.waitingForm = waitingForm;
        this.socket = socket;
        this.loggedInId = loggedInId;
        this.loggedInUserName = loggedInUserName;
    }

    // ActionListener 인터페이스의 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        // 비밀번호 재설정 이벤트 발생 시, 새로운 비밀번호 재설정 폼을 생성하고 현재의 대기 화면을 닫음
        new PwResetForm(socket, loggedInUserName, loggedInId);
        waitingForm.dispose();
    }
}

