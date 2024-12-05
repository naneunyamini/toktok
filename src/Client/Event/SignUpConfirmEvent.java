package Client.Event;

import Client.Form.LoginForm;
import Client.Form.SignUpForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class SignUpConfirmEvent implements ActionListener {
    private SignUpForm signUpForm;   // 회원가입 폼
    private Socket socket;           // 클라이언트 소켓

    // 생성자: 회원가입 폼과 클라이언트 소켓을 매개변수로 받음
    public SignUpConfirmEvent(SignUpForm signUpForm, Socket socket) {
        this.signUpForm = signUpForm;
        this.socket = socket;
    }

    // ActionListener 인터페이스의 메서드 구현
    public void actionPerformed(ActionEvent e) {
        // 회원가입 정보 가져오기
        String name = signUpForm.getNameField().getText();
        String id = signUpForm.getIdField().getText();
        String password = signUpForm.getPwField().getText();

        // 입력 필드 중 하나라도 비어있는 경우 오류 메시지 표시
        if (name.isEmpty() || id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(signUpForm, "모든 필드를 채워주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                // 서버에 회원가입 메시지 전송
                String message = "REGISTER " + name + " " + id + " " + password;
                sendMessage(socket, message);
                String response = receiveMessage(socket);

                // 서버 응답에 따라 처리
                if ("REGISTER_SUCCESS".equals(response)) {
                    // 회원가입 성공 시, 성공 메시지 표시하고 회원가입 폼 닫고 로그인 폼 열기
                    JOptionPane.showMessageDialog(signUpForm, "회원가입 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
                    signUpForm.dispose();

                    SwingUtilities.invokeLater(() -> {
                        new LoginForm(socket);
                    });
                } else if ("REGISTER_FAIL_DUPLICATE".equals(response)) {
                    // 중복된 아이디로 회원가입 시도 시, 오류 메시지 표시
                    JOptionPane.showMessageDialog(signUpForm, "이미 사용 중인 아이디입니다. 다른 아이디를 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else if ("REGISTER_FAIL".equals(response)) {
                    // 회원가입 실패 시, 오류 메시지 표시
                    JOptionPane.showMessageDialog(signUpForm, "회원가입 실패. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 알 수 없는 응답이 온 경우, 오류 메시지 표시
                    JOptionPane.showMessageDialog(signUpForm, "알 수 없는 응답: " + response, "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // 서버 연결 실패 시, 오류 메시지 표시
                JOptionPane.showMessageDialog(signUpForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
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

    // 소켓을 통해 서버로부터 메시지를 받아오는 메서드
    private String receiveMessage(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }
}
