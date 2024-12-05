package Client.Form;

import Client.Etc.RoundedButton;
import Client.Event.SignUpConfirmEvent;
import Client.Event.SignUpCancelEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

public class SignUpForm extends JFrame {
    private JTextField nameField;  // 사용자 이름을 입력하는 텍스트 필드
    private JTextField idField;    // 사용자 ID를 입력하는 텍스트 필드
    private JTextField pwField;    // 사용자 비밀번호를 입력하는 텍스트 필드
    private RoundedButton signUpButton;           // 가입 확인 버튼
    private RoundedButton signUpCancelButton;     // 가입 취소 버튼
    private SignUpConfirmEvent signUpConfirmEvent; // 가입 확인 이벤트 핸들러
    private SignUpCancelEvent signUpCancelEvent;   // 가입 취소 이벤트 핸들러

    public SignUpForm(Socket socket) {
        signUpConfirmEvent = new SignUpConfirmEvent(this, socket);
        signUpCancelEvent = new SignUpCancelEvent(this, socket);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 프레임이 닫힐 때만 닫히도록 설정

        // 정보를 입력받는 패널 생성
        class Info extends JPanel {
            Info() {
                GridLayout grid = new GridLayout(4, 2);
                setLayout(grid);
                grid.setVgap(10);
                grid.setHgap(10);

                // 이름 입력 레이블 및 텍스트 필드 생성 및 설정
                JLabel nameLabel = new JLabel("이름 ");
                nameLabel.setHorizontalAlignment(JLabel.CENTER);
                nameLabel.setForeground(Color.WHITE);
                nameField = new JTextField(20);
                add(nameLabel);
                add(nameField);

                // ID 입력 레이블 및 텍스트 필드 생성 및 설정
                JLabel idLabel = new JLabel("ID ");
                idLabel.setHorizontalAlignment(JLabel.CENTER);
                idLabel.setForeground(Color.WHITE);
                idField = new JTextField(20);
                add(idLabel);
                add(idField);

                // 비밀번호 입력 레이블 및 텍스트 필드 생성 및 설정
                JLabel pwLabel = new JLabel("PW ");
                pwLabel.setHorizontalAlignment(JLabel.CENTER);
                pwLabel.setForeground(Color.WHITE);
                pwField = new JTextField(20);
                add(pwLabel);
                add(pwField);

                // 가입 취소 버튼 생성 및 이벤트 핸들러 등록
                signUpCancelButton = new RoundedButton("가입 취소");
                signUpCancelButton.addActionListener(signUpCancelEvent);
                add(signUpCancelButton);

                // 가입 확인 버튼 생성 및 이벤트 핸들러 등록
                signUpButton = new RoundedButton("가입");
                signUpButton.addActionListener(signUpConfirmEvent);
                add(signUpButton);

                setBackground(new Color(63, 61, 62));
            }
        }

        setTitle("HongTalk");

        Container c = getContentPane();
        c.setBackground(new Color(63, 61, 62));
        c.setLayout(new BorderLayout());

        // 상단 이미지 레이블 생성 및 설정
        ImageIcon icon1 = new ImageIcon("img/Login1.jpg");
        Image img1 = icon1.getImage();
        Image changeImg = img1.getScaledInstance(270, 370, Image.SCALE_SMOOTH);
        ImageIcon changeIcon1 = new ImageIcon(changeImg);
        JLabel label1 = new JLabel(changeIcon1);
        c.add(label1, BorderLayout.NORTH);

        // 정보 입력 패널 생성 및 설정
        Info info = new Info();
        info.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        c.add(info, BorderLayout.CENTER);

        // 하단 여백 패널 생성 및 설정
        JPanel empty = new JPanel();
        empty.setPreferredSize(new Dimension(300, 65));
        empty.setBackground(new Color(63, 61, 62));
        c.add(empty, BorderLayout.SOUTH);

        setResizable(false);
        setVisible(true);
        setSize(300, 600);
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

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getIdField() {
        return idField;
    }

    public JTextField getPwField() {
        return pwField;
    }
}
