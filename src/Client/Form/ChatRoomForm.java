package Client.Form;
import Client.Etc.RoundedButton;
import Client.Event.ChatRoomProcessingEvent;
import Client.Event.DrawingEnterEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

// 채팅방 폼을 나타내는 클래스
public class ChatRoomForm extends JFrame {
    private JTextField inputField; // 메시지 입력 필드
    private JTextArea outputArea; // 채팅 내용 출력 영역
    private ChatRoomProcessingEvent chatRoomProcessingEvent; // 채팅방 처리 이벤트
    private DrawingEnterEvent drawingEnterEvent; // 그림판 진입 이벤트
    private DefaultListModel<String> listModel; // 유저 목록을 위한 리스트 모델
    private JList<String> userList; // 유저 목록을 표시하는 JList

    // 생성자
    public ChatRoomForm(Socket socket, String loggedInUserName) throws IOException {
        // 기본적인 프레임 설정
        setTitle("Hong Talk");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputField = new JTextField(15);
        outputArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputArea.setEditable(false);

        // 메시지 전송을 위한 보내기 버튼 설정
        RoundedButton sendButton = new RoundedButton("보내기");
        chatRoomProcessingEvent = new ChatRoomProcessingEvent(this, socket, loggedInUserName);
        sendButton.addActionListener(chatRoomProcessingEvent);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });

        // 입력 필드와 보내기 버튼을 포함하는 패널 설정
        JPanel p = new JPanel(new BorderLayout());
        p.add("Center", inputField);
        p.add("East", sendButton);

        // 프레임에 컴포넌트 추가
        Container c = this.getContentPane();
        c.add("Center", scroll);
        c.add("South", p);

        setSize(300, 600);
        setVisible(true);

        // 주기적으로 메시지를 수신하는 타이머 설정
        Timer timer = new Timer(10, e -> {
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return receiveMessage(socket);
                }

                @Override
                protected void done() {
                    try {
                        String[] tokens = get().split("\\s+");
                        if (tokens[0].startsWith("USER_LIST")) {
                            setUserList(get());
                        }
                        else if (tokens[0].startsWith("DRAWING")) {
                            // 그림 관련 처리 코드
                        }
                        else {
                            displayMessage(get());
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };
            worker.execute();
        });
        timer.start();

        // 창이 닫힐 때의 동작 설정
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendExitMessage(socket, loggedInUserName);
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });

        // 메뉴바 설정
        JMenuBar mb = new JMenuBar();
        JMenu jm1 = new JMenu("유저 목록");
        JMenu jm2 = new JMenu("그림판");
        JMenuItem drawingMenuItem = new JMenuItem("그림판 열기");
        drawingEnterEvent = new DrawingEnterEvent(this, socket);
        drawingMenuItem.addActionListener(drawingEnterEvent);
        setJMenuBar(mb);
        setLocationRelativeTo(null);

        // 유저 목록을 표시하는 JList 설정
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        mb.add(filler);
        mb.add(jm2);
        mb.add(jm1);
        jm1.add(new JScrollPane(userList));
        jm2.add(drawingMenuItem);
    }

    // 입력 필드 Getter 메서드
    public JTextField getinputField() {
        return inputField;
    }

    // 채팅 내용을 화면에 표시하는 메서드
    public void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> outputArea.append(getCurrentTime() + message + "\n"));
    }

    // 현재 시간을 문자열로 반환하는 메서드
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH시 mm분");
        return "[" + dateFormat.format(new Date()) + ", ";
    }

    // 소켓으로부터 메시지를 받는 메서드
    private String receiveMessage(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }

    // 채팅방에서 나가는 메시지를 소켓을 통해 전송하는 메서드
    private void sendExitMessage(Socket socket, String userName) {
        try {
            sendMessage(socket, "EXIT_CHAT_ROOM " + userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 소켓을 통해 메시지를 전송하는 메서드
    private void sendMessage(Socket socket, String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    // 서버로부터 받은 유저 목록을 업데이트하는 메서드
    public void setUserList(String list) {
        String[] users = list.substring("USER_LIST ".length()).split(",");
        updateUserList(users);
    }

    // 유저 목록을 업데이트하는 메서드
    public void updateUserList(String[] users) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listModel.clear();
                for (String user : users) {
                    listModel.addElement(user);
                }
            }
        });
    }
}
