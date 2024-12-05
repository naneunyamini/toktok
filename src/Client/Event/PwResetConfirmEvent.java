package Client.Event;

import Client.Form.PwResetForm;
import Client.Form.WaitingForm;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class PwResetConfirmEvent implements ActionListener {
    private PwResetForm pwResetForm;       // 비밀번호 재설정 폼
    private Socket socket;                 // 클라이언트 소켓
    private String loggedInUserName;       // 현재 로그인한 사용자의 이름
    private String loggedInUserId;         // 현재 로그인한 사용자의 아이디

    // 생성자: 비밀번호 재설정 폼, 클라이언트 소켓, 로그인한 사용자의 이름과 아이디를 매개변수로 받음
    public PwResetConfirmEvent(PwResetForm pwResetForm, Socket socket, String loggedInUserName, String loggedInUserId) {
        this.pwResetForm = pwResetForm;
        this.socket = socket;
        this.loggedInUserName = loggedInUserName;
        this.loggedInUserId = loggedInUserId;
    }

    // ActionListener 인터페이스의 메서드 구현
    public void actionPerformed(ActionEvent e) {
        try {
            // 비밀번호 재설정 폼이 유효한 경우
            if (pwResetForm != null) {
                // 새로 입력한 비밀번호를 가져옴
                String newPassword = pwResetForm.getPwField().getText();

                // 새로 입력한 비밀번호가 비어 있지 않은 경우
                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(pwResetForm, "바꿀 비밀번호를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 서버에 비밀번호 변경 메시지 전송
                    String changePasswordMessage = "CHANGE_PASSWORD " + loggedInUserId + " " + newPassword;
                    sendMessage(socket, changePasswordMessage);
                    String response = receiveMessage(socket);

                    // 서버 응답에 따라 처리
                    if ("CHANGE_PASSWORD_EQUAL".equals(response)) {
                        JOptionPane.showMessageDialog(pwResetForm, "현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if ("CHANGE_PASSWORD_SUCCESS".equals(response)) {
                            JOptionPane.showMessageDialog(pwResetForm, "비밀번호가 성공적으로 변경되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);

                            // 비밀번호 변경 성공 시 대기 화면을 생성하고 현재의 비밀번호 재설정 폼을 닫음
                            SwingUtilities.invokeLater(() -> {
                                WaitingForm waitingForm = new WaitingForm(socket, loggedInUserName, loggedInUserId);
                                pwResetForm.dispose();
                            });
                        } else if ("CHANGE_PASSWORD_FAIL".equals(response) || "CHANGE_PASSWORD_GET_FAIL".equals(response)) {
                            JOptionPane.showMessageDialog(pwResetForm, "비밀번호 변경에 실패했습니다. 서버 응답: " + response, "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(pwResetForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
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
