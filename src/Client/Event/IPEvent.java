package Client.Event;

import Client.Form.IPForm;
import Client.Form.LoginForm;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class IPEvent implements ActionListener {
    private String ipAddress;   // 서버 IP 주소를 저장하는 변수
    private IPForm ipForm;      // IP 입력 폼
    private Socket socket;      // 클라이언트 소켓

    // 생성자: IP 입력 폼을 매개변수로 받음
    public IPEvent(IPForm ipForm) {
        this.ipForm = ipForm;
    }

    // ActionListener 인터페이스의 메서드 구현
    public void actionPerformed(ActionEvent e) {
        // "확인" 버튼이 눌렸을 때
        if (e.getActionCommand().equals("확인")) {
            // IP 입력 폼에서 입력된 IP 주소를 가져옴
            String enteredIpAddress = ipForm.getIpAddress();

            if (enteredIpAddress != null) {
                // 입력된 IP 주소가 유효한지 확인
                if (isValidIpAddress(enteredIpAddress)) {
                    this.ipAddress = enteredIpAddress;
                    try {
                        // 소켓을 생성하고 서버에 연결 시도
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(ipAddress, 8888), 1000);
                        JOptionPane.showMessageDialog(ipForm, "서버와 연결되었습니다.");
                        System.out.println("Client Connected");

                        // 로그인 폼을 생성하고 IP 입력 폼을 닫음
                        SwingUtilities.invokeLater(() -> {
                            new LoginForm(socket);
                            ipForm.dispose();
                        });
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(ipForm, "유효한 IP 주소를 입력하세요.");
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(ipForm, "올바르지 않은 IP 주소입니다.");
                }
            } else {
                JOptionPane.showMessageDialog(ipForm, "IP 주소를 입력하세요.");
            }
        }
    }

    // 유효한 IP 주소인지 확인하는 메서드
    private boolean isValidIpAddress(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length == 4) {
            for (String part : parts) {
                try {
                    int octet = Integer.parseInt(part);
                    if (octet < 0 || octet > 255) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
