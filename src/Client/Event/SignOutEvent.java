package Client.Event;

import Client.Form.LoginForm;
import Client.Form.WaitingForm;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class SignOutEvent implements ActionListener {
    private WaitingForm waitingForm;   // 대기 화면 폼
    private String loggedInUserId;      // 현재 로그인한 사용자의 아이디
    private Socket socket;              // 클라이언트 소켓

    // 생성자: 대기 화면 폼, 클라이언트 소켓, 로그인한 사용자의 아이디를 매개변수로 받음
    public SignOutEvent(WaitingForm waitingForm, Socket socket, String loggedInUserId) {
        this.waitingForm = waitingForm;
        this.socket = socket;
        this.loggedInUserId = loggedInUserId;
    }

    // ActionListener 인터페이스의 메서드 구현
    public void actionPerformed(ActionEvent e) {
        // 회원 탈퇴 여부를 확인하는 다이얼로그 표시
        int check = JOptionPane.showConfirmDialog(waitingForm, "회원탈퇴하시겠습니까?", "회원탈퇴 확인", JOptionPane.YES_NO_OPTION);

        // 사용자가 "예"를 선택한 경우
        if (check == JOptionPane.YES_OPTION) {
            try {
                // 서버에 회원 탈퇴 메시지 전송
                String message = "SIGN_OUT " + loggedInUserId;
                sendMessage(socket, message);
                String response = receiveMessage(socket);

                // 서버 응답에 따라 처리
                if (response.startsWith("SIGN_OUT_SUCCESS")) {
                    // 회원 탈퇴 성공 시, 새로운 로그인 폼을 생성하고 현재의 대기 화면을 닫음
                    SwingUtilities.invokeLater(() -> {
                        new LoginForm(socket);
                        waitingForm.dispose();
                    });
                } else if (response.equals("SIGN_OUT_FAIL")) {
                    // 회원 탈퇴 실패 시 오류 메시지 표시
                    JOptionPane.showMessageDialog(waitingForm, "회원 탈퇴에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // 서버 연결 실패 시 오류 메시지 표시
                JOptionPane.showMessageDialog(waitingForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
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
