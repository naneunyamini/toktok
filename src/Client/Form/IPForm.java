package Client.Form;

import Client.Etc.RoundedButton;
import Client.Event.IPEvent;

import javax.swing.*;
import java.awt.*;

public class IPForm extends JFrame {
    private JTextField ipTextField;  // IP 주소를 입력받는 텍스트 필드
    private IPEvent ipEvent;  // IP 입력 확인 이벤트를 처리하는 이벤트 핸들러

    public IPForm() {
        setTitle("서버 연결");  // 프레임의 제목 설정
        setSize(300, 100);  // 프레임의 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 프레임이 닫힐 때 프로그램 종료

        setLayout(new BorderLayout());  // 프레임의 레이아웃을 BorderLayout으로 설정

        // IP 주소를 입력받는 텍스트 필드 생성 및 설정
        ipTextField = new JTextField();
        ipTextField.setPreferredSize(new Dimension(200, 30));

        // IP 주소 입력 패널 생성 및 설정
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JLabel ipLabel = new JLabel("IP 주소:");  // IP 주소 레이블 생성
        panel.add(ipLabel);  // 레이블을 패널에 추가
        panel.add(ipTextField);  // 텍스트 필드를 패널에 추가

        // 확인 버튼 생성 및 설정
        RoundedButton submitButton = new RoundedButton("확인");
        ipEvent = new IPEvent(this);  // IP 입력 확인 이벤트 핸들러 생성
        submitButton.addActionListener(ipEvent);  // 확인 버튼에 이벤트 핸들러 등록

        // 패널과 버튼을 프레임에 추가
        add(panel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);  // 프레임을 화면 중앙에 배치
        setVisible(true);  // 프레임을 표시
    }

    /**
     * 현재 입력된 IP 주소를 반환합니다.
     *
     * @return 입력된 IP 주소
     */
    public String getIpAddress() {
        return ipTextField.getText();
    }
}