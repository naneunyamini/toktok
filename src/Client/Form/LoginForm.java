package Client.Form;

import Client.Etc.RoundedButton;
import Client.Event.SignUpEvent;
import Client.Event.LoginEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

public class LoginForm extends JFrame {
    private JTextField idField;  // 사용자 ID를 입력하는 텍스트 필드
    private JPasswordField pwField;  // 사용자 비밀번호를 입력하는 패스워드 필드
    private RoundedButton signUpButton;  // 회원가입 버튼
    private RoundedButton loginButton;  // 로그인 버튼
    private SignUpEvent signUpEvent;  // 회원가입 이벤트 핸들러
    private LoginEvent loginEvent;  // 로그인 이벤트 핸들러

    public LoginForm(Socket socket) {
        loginEvent = new LoginEvent(this, socket);  // 로그인 이벤트 핸들러 생성
        signUpEvent = new SignUpEvent(this, socket);  // 회원가입 이벤트 핸들러 생성
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 프레임이 닫힐 때 프로그램 종료

        // 사용자 ID, 비밀번호, 회원가입, 로그인을 입력하는 패널을 생성하고 설정
        class IdPwSu extends JPanel {
            IdPwSu() {
                GridLayout grid = new GridLayout(3, 2);
                grid.setVgap(10);
                grid.setHgap(10);
                setLayout(grid);

                // 사용자 ID 입력 레이블 및 텍스트 필드 생성 및 설정
                JLabel idLabel = new JLabel("ID ");
                idLabel.setHorizontalAlignment(JLabel.CENTER);
                idLabel.setForeground(Color.WHITE);
                idField = new JTextField(20);
                add(idLabel);
                add(idField);

                // 사용자 비밀번호 입력 레이블 및 패스워드 필드 생성 및 설정
                JLabel pwLabel = new JLabel("PW ");
                pwField = new JPasswordField(20);
                pwLabel.setHorizontalAlignment(JLabel.CENTER);
                pwLabel.setForeground(Color.WHITE);
                add(pwLabel);
                add(pwField);

                // 회원가입, 로그인 버튼 생성 및 설정
                signUpButton = new RoundedButton("회원가입");
                loginButton = new RoundedButton("로그인");
                signUpButton.addActionListener(signUpEvent);
                loginButton.addActionListener(loginEvent);
                add(signUpButton);
                add(loginButton);

                setBackground(new Color(63, 61, 62));
                setSize(300, 100);
            }
        }

        setTitle("HongTalk");  // 프레임 제목 설정

        Container c = getContentPane();
        c.setBackground(new Color(63, 61, 62));
        c.setLayout(new BorderLayout());

        // 상단 이미지 생성 및 설정
        ImageIcon icon1 = new ImageIcon("img/Login1.jpg");
        Image img1 = icon1.getImage();
        Image changeImg = img1.getScaledInstance(270, 370, Image.SCALE_SMOOTH);
        ImageIcon changeIcon1 = new ImageIcon(changeImg);
        JLabel label1 = new JLabel(changeIcon1);
        c.add(label1, BorderLayout.NORTH);

        // 사용자 ID, 비밀번호 입력 패널 생성 및 설정
        IdPwSu set = new IdPwSu();
        set.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        c.add(set, BorderLayout.CENTER);

        // 하단 이미지 생성 및 설정
        ImageIcon icon2 = new ImageIcon("img/Login2.jpg");
        Image img2 = icon2.getImage();
        Image changeImg2 = img2.getScaledInstance(300, 100, Image.SCALE_SMOOTH);
        ImageIcon changeIcon2 = new ImageIcon(changeImg2);
        JLabel label = new JLabel(changeIcon2);
        c.add(label, BorderLayout.SOUTH);

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

    public JTextField getIdField() {
        return idField;
    }

    public JPasswordField getPwField() {
        return pwField;
    }
}