package Client.Form;

import Client.Etc.RoundedButton;
import Client.Event.ChatRoomEnterEvent;
import Client.Event.PwResetEvent;
import Client.Event.SignOutEvent;
import Client.Event.LogoutEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

public class WaitingForm extends JFrame {
    private ChatRoomEnterEvent chatRoomEnterEvent;  // 채팅방 입장 이벤트 핸들러
    private PwResetEvent pwResetEvent;              // 비밀번호 변경 이벤트 핸들러
    private SignOutEvent signOutEvent;              // 회원 탈퇴 이벤트 핸들러
    private LogoutEvent logoutEvent;                // 로그 아웃 이벤트 핸들러

    public WaitingForm(Socket socket, String loggedInUserName, String loggedInId) {
        setTitle("Hong Talk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container c = getContentPane();

        // 채팅방 입장 버튼 생성 및 이벤트 핸들러 등록
        RoundedButton enterRoomButton = new RoundedButton("채팅방 입장하슈");
        chatRoomEnterEvent = new ChatRoomEnterEvent(this, socket, loggedInUserName);
        enterRoomButton.addActionListener(chatRoomEnterEvent);

        // 비밀번호 변경 버튼 생성 및 이벤트 핸들러 등록
        RoundedButton changingButton = new RoundedButton("비밀번호 변경");
        pwResetEvent = new PwResetEvent(this, socket, loggedInUserName, loggedInId);
        changingButton.addActionListener(pwResetEvent);

        // 회원 탈퇴 버튼 생성 및 이벤트 핸들러 등록
        RoundedButton signOutButton = new RoundedButton("회원 탈퇴");
        signOutEvent = new SignOutEvent(this, socket, loggedInId);
        signOutButton.addActionListener(signOutEvent);

        // 로그아웃 버튼 생성 및 이벤트 핸들러 등록
        RoundedButton logoutButton = new RoundedButton("로그 아웃");
        logoutEvent = new LogoutEvent(this, socket);
        logoutButton.addActionListener(logoutEvent);

        // 사용자 정보 표시 레이블 생성 및 설정
        JLabel info = new JLabel("<html>" + "<center>현재 접속된 계정의 이름<br>-" + loggedInUserName + "-</center></html>");
        info.setHorizontalAlignment(JLabel.CENTER);
        info.setForeground(Color.WHITE);

        // 버튼 패널 생성 및 설정
        JPanel btns = new JPanel();
        GridLayout grid = new GridLayout(5, 1);
        grid.setVgap(10);
        btns.setBorder(BorderFactory.createEmptyBorder(100, 70, 100, 70));
        btns.setLayout(grid);
        btns.add(enterRoomButton);
        btns.add(changingButton);
        btns.add(signOutButton);
        btns.add(logoutButton);
        btns.add(info);
        btns.setBackground(new Color(63, 61, 62));

        // 컨테이너에 버튼 패널 추가
        c.add(btns, BorderLayout.CENTER);
        c.setBackground(new Color(63, 61, 62));

        setResizable(false);
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
}