package Client.Event;

import Client.Form.PwResetForm;
import Client.Form.WaitingForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class PwResetCancelEvent implements ActionListener {
    private PwResetForm pwResetForm;    // 비밀번호 재설정 폼
    private Socket socket;              // 클라이언트 소켓
    private String loggedInUserName;    // 현재 로그인한 사용자의 이름
    private String loggedInId;          // 현재 로그인한 사용자의 아이디

    // 생성자: 비밀번호 재설정 폼, 클라이언트 소켓, 로그인한 사용자의 이름과 아이디를 매개변수로 받음
    public PwResetCancelEvent(PwResetForm pwResetForm, Socket socket, String loggedInUserName, String loggedInId) {
        this.pwResetForm = pwResetForm;
        this.socket = socket;
        this.loggedInUserName = loggedInUserName;
        this.loggedInId = loggedInId;
    }

    // ActionListener 인터페이스의 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        // 비밀번호 재설정 취소 이벤트 발생 시, 대기 화면을 새로 생성하고 현재의 비밀번호 재설정 폼을 닫음
        new WaitingForm(socket, loggedInUserName, loggedInId);
        pwResetForm.dispose();
    }
}
