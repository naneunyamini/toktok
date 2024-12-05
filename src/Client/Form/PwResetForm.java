package Client.Form;

import Client.Etc.RoundedButton;
import Client.Event.PwResetCancelEvent;
import Client.Event.PwResetConfirmEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

public class PwResetForm extends JFrame {
    private JTextField resetPwField;  // 새로운 비밀번호를 입력하는 텍스트 필드
    private PwResetConfirmEvent pwResetConfirmEvent;  // 비밀번호 변경 확인 이벤트 핸들러
    private PwResetCancelEvent pwResetCancelEvent;  // 비밀번호 변경 취소 이벤트 핸들러

    public PwResetForm(Socket socket, String loggedInUserName, String loggedInId) {
        setTitle("Hong Talk");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 프레임이 닫힐 때만 닫히도록 설정

        Container pwResetFrame = getContentPane();

        // 그리드 레이아웃 설정
        GridLayout grid = new GridLayout(3, 1);
        grid.setVgap(-60);

        JPanel colPanel = new JPanel();
        colPanel.setLayout(grid);
        colPanel.setBorder(BorderFactory.createEmptyBorder(0, 70, 0, 70));

        // 사용자 정보를 표시하는 레이블 생성 및 설정
        JLabel infoLabel = new JLabel("<html><center>로그인된 계정의 ID<br>" + loggedInId + "<br><br>로그인된 계정의 이름<br>" + loggedInUserName + "</center></html>");
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        colPanel.add(infoLabel);
        colPanel.setBackground(new Color(63, 61, 62));

        // 비밀번호 재설정을 위한 입력 패널 생성 및 설정
        resetPwField = new JTextField(20);

        // 비밀번호 변경 및 변경 취소 버튼 생성 및 이벤트 핸들러 등록
        RoundedButton summitButton = new RoundedButton("비밀번호 변경");
        pwResetConfirmEvent = new PwResetConfirmEvent(this, socket, loggedInUserName, loggedInId);
        summitButton.addActionListener(pwResetConfirmEvent);

        RoundedButton cancelButton = new RoundedButton("변경 취소");
        pwResetCancelEvent = new PwResetCancelEvent(this, socket, loggedInUserName, loggedInId);
        cancelButton.addActionListener(pwResetCancelEvent);

        // 입력 패널 생성 및 설정
        JPanel inputPanel = new JPanel();
        GridLayout grid1 = new GridLayout(3, 1);
        grid1.setVgap(10);
        inputPanel.setLayout(grid1);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 10, 0));
        inputPanel.setPreferredSize(new Dimension(300, 200));
        inputPanel.setBackground(new Color(63, 61, 62));
        inputPanel.add(resetPwField);
        inputPanel.add(summitButton);
        inputPanel.add(cancelButton);

        // 하단 여백 패널 생성 및 설정
        JPanel empty1 = new JPanel();
        empty1.setPreferredSize(new Dimension(300, 0));
        empty1.setBackground(new Color(63, 61, 62));
        pwResetFrame.add(empty1, BorderLayout.SOUTH);

        // 입력 패널 및 사용자 정보 패널을 추가
        colPanel.add(inputPanel);
        pwResetFrame.add(colPanel, BorderLayout.CENTER);

        // 상단 여백 패널 생성 및 설정
        JPanel empty2 = new JPanel();
        empty2.setPreferredSize(new Dimension(300, 150));
        empty2.setBackground(new Color(63, 61, 62));
        pwResetFrame.add(empty2, BorderLayout.NORTH);

        setBackground(new Color(63, 61, 62));
        setSize(300, 600);
        setVisible(true);
        setLocationRelativeTo(null);

        // 프레임이 닫힐 때 소켓을 닫고 프로그램 종료
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });
    }

    public JTextField getPwField() {
        return resetPwField;
    }
}