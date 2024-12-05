package Client.Form;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class DrawingForm extends JFrame {
    private Socket socket;

    public DrawingForm(Socket socket) throws IOException {
        this.socket = socket;
        createAndShowGUI();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }

    public void reopen() {
        if (!isVisible()) {
            setVisible(true);
        } else {
            toFront();
        }
    }

    private void createAndShowGUI() {
        // GUI 초기화 및 설정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        RectPanel rectPanel = new RectPanel(socket);
        add(rectPanel);

        Thread messageReceiverThread = new Thread(() -> {
            // 소켓으로부터 메시지를 수신하고, 수신된 메시지를 처리하는 스레드
            while (true) {
                try {
                    String message = receiveMessage(socket);
                    if (!message.isEmpty()) {
                        System.out.println(message);
                        updateDrawingFromMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        messageReceiverThread.start();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String receiveMessage(Socket socket) throws IOException {
        // 소켓으로부터 메시지를 수신하는 메서드
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }

    private void updateDrawingFromMessage(String message) {
        // 수신된 메시지를 해석하여 화면을 업데이트하는 메서드
        String[] parts = message.split(" ");
        if (parts.length == 6) {
            try {
                Color color = parseColor(parts[0]);
                float stroke = Float.parseFloat(parts[1]);
                int x1 = Integer.parseInt(parts[2]);
                int y1 = Integer.parseInt(parts[3]);
                int x2 = Integer.parseInt(parts[4]);
                int y2 = Integer.parseInt(parts[5]);

                Graphics2D g = ((RectPanel) getContentPane().getComponent(0)).bufferedImage.createGraphics();
                g.setColor(color);
                g.setStroke(new BasicStroke(stroke));
                g.drawLine(x1, y1, x2, y2);
                g.dispose();

                repaint();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    private Color parseColor(String colorName) {
        // 문자열 색상 이름을 Color 객체로 파싱하는 메서드
        switch (colorName) {
            case "검정색":
                return Color.black;
            case "빨간색":
                return Color.red;
            case "파란색":
                return Color.blue;
            case "초록색":
                return Color.green;
            case "노란색":
                return Color.yellow;
            case "분홍색":
                return Color.pink;
            case "자홍색":
                return Color.magenta;
            default:
                return Color.black;
        }
    }
}

class RectPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    String shapeString = "";
    Point firstPointer = new Point(0, 0);
    Point secondPointer = new Point(0, 0);
    BufferedImage bufferedImage;
    Color colors = Color.black;
    Float stroke = (float) 5;
    JComboBox<Color> colorComboBox;
    JComboBox<Float> strokeComboBox;
    int width;
    int height;
    int minPointx;
    int minPointy;
    private ImageIcon iconRectButton;
    private ImageIcon iconLineButton;
    private ImageIcon iconCircleButton;
    private ImageIcon iconPenButton;
    private ImageIcon iconEraseButton;
    private ImageIcon iconEraseAllButton;
    private ImageIcon iconOpenButton;
    private ImageIcon iconSaveButton;
    private JButton rectButton;
    private JButton lineButton;
    private JButton circleButton;
    private JButton penButton;
    private JButton eraseButton;
    private JButton saveButton;
    private JButton openButton;
    private JButton eraseAllButton;
    private Color originalRectButtonColor;
    private Color originalLineButtonColor;
    private Color originalCircleButtonColor;
    private Color originalPenButtonColor;
    private Color originalEraseButtonColor;
    private Color originalEraseAllButtonColor;
    private Color originalSaveButtonColor;
    private Color originalOpenButtonColor;
    private Socket socket;

    private ImageIcon ImageSetSize(ImageIcon icon, int width, int heigth) {
        Image xImage = icon.getImage();
        Image yImage = xImage.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
        ImageIcon xyImage = new ImageIcon(yImage);
        return xyImage;
    }
    public RectPanel(Socket socket) {
        // 패널 초기화 및 설정
        this.socket = socket;
        JToolBar toolBar=new JToolBar("tool");
        Color backgroundColor = new Color(63, 61, 62);
        toolBar.setBackground(backgroundColor);
        toolBar.setSize(40,20);

        iconRectButton=new ImageIcon("img/rectangle.png");
        iconLineButton=new ImageIcon("img/line.png");
        iconCircleButton=new ImageIcon("img/circle.png");
        iconPenButton=new ImageIcon("img/pen.png");
        iconEraseButton=new ImageIcon("img/eraser.png");
        iconEraseAllButton=new ImageIcon("img/eraseall.png");
        iconOpenButton=new ImageIcon("img/open.png");
        iconSaveButton=new ImageIcon("img/save.png");

        iconRectButton = ImageSetSize(iconRectButton , 30, 30);
        iconOpenButton = ImageSetSize(iconOpenButton , 30, 30);
        iconPenButton = ImageSetSize(iconPenButton , 30, 30);
        iconSaveButton = ImageSetSize(iconSaveButton , 30, 30);
        iconLineButton = ImageSetSize(iconLineButton , 30, 30);
        iconCircleButton = ImageSetSize(iconCircleButton , 30, 30);
        iconEraseButton = ImageSetSize(iconEraseButton , 30, 30);
        iconEraseAllButton = ImageSetSize(iconEraseAllButton , 30, 30);

        rectButton = new JButton(iconRectButton);
        lineButton = new JButton(iconLineButton);
        circleButton = new JButton(iconCircleButton);
        penButton = new JButton(iconPenButton);
        eraseButton = new JButton(iconEraseButton);
        eraseAllButton = new JButton(iconEraseAllButton);
        saveButton = new JButton(iconSaveButton);
        openButton = new JButton(iconOpenButton);

        rectButton.setActionCommand("네모");
        lineButton.setActionCommand("선");
        circleButton.setActionCommand("원");
        penButton.setActionCommand("펜");
        eraseButton.setActionCommand("지우개");
        eraseAllButton.setActionCommand("전체지우기");
        saveButton.setActionCommand("Save");
        openButton.setActionCommand("Open");

        rectButton.setBackground(new Color(63, 62, 61, 255));
        penButton.setBackground(new Color(63, 62, 61, 255));
        circleButton.setBackground(new Color(63, 62, 61, 255));
        eraseButton.setBackground(new Color(63, 62, 61, 255));
        eraseAllButton.setBackground(new Color(63, 62, 61, 255));
        saveButton.setBackground(new Color(63, 62, 61, 255));
        openButton.setBackground(new Color(63, 62, 61, 255));
        lineButton.setBackground(new Color(63, 62, 61, 255));

        rectButton.setBorderPainted(false);
        lineButton.setBorderPainted(false);
        circleButton.setBorderPainted(false);
        penButton.setBorderPainted(false);
        openButton.setBorderPainted(false);
        saveButton.setBorderPainted(false);
        eraseButton.setBorderPainted(false);
        eraseAllButton.setBorderPainted(false);

        originalRectButtonColor = rectButton.getBackground();
        originalLineButtonColor = lineButton.getBackground();
        originalCircleButtonColor = circleButton.getBackground();
        originalPenButtonColor = penButton.getBackground();
        originalEraseButtonColor = eraseButton.getBackground();
        originalEraseAllButtonColor = eraseAllButton.getBackground();
        originalSaveButtonColor = saveButton.getBackground();
        originalOpenButtonColor = openButton.getBackground();

        rectButton.setFocusPainted(false);
        penButton.setFocusPainted(false);
        lineButton.setFocusPainted(false);
        circleButton.setFocusPainted(false);
        openButton.setFocusPainted(false);
        saveButton.setFocusPainted(false);
        eraseButton.setFocusPainted(false);
        eraseAllButton.setFocusPainted(false);

        colorComboBox = new JComboBox<Color>();
        strokeComboBox = new JComboBox<Float>();

        colorComboBox.setModel(new DefaultComboBoxModel<Color>(new Color[] { Color.black, Color.red, Color.blue,
                Color.green, Color.yellow, Color.pink, Color.magenta }));

        // 색상 선택 콤보박스의 렌더러 설정
        colorComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    String name = "";
                    if (color.equals(Color.black)) {
                        name = "검정색";
                    } else if (color.equals(Color.red)) {
                        name = "빨간색";
                    } else if (color.equals(Color.blue)) {
                        name = "파란색";
                    } else if (color.equals(Color.green)) {
                        name = "초록색";
                    } else if (color.equals(Color.yellow)) {
                        name = "노란색";
                    } else if (color.equals(Color.pink)) {
                        name = "분홍색";
                    } else if (color.equals(Color.magenta)) {
                        name = "자홍색";
                    }
                    // 설정된 이름으로 렌더링
                    return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                } else {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            }
        });

        // 선 굵기 선택 콤보박스의 모델과 렌더러 설정
        strokeComboBox.setModel(new DefaultComboBoxModel<Float>(
                new Float[] { (float) 5, (float) 10, (float) 15, (float) 20, (float) 25 }));

        strokeComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Float) {
                    Float size = (Float) value;
                    String name = size + "pt";
                    // 굵기에 "pt"를 붙여서 렌더링
                    return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                } else {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            }
        });

        // 버튼 및 콤보박스 등의 UI 구성 요소를 패널에 추가
        add(eraseAllButton);
        add(penButton);
        add(lineButton);
        add(rectButton);
        add(circleButton);
        add(colorComboBox);
        add(strokeComboBox);
        add(eraseButton);
        add(saveButton);
        add(openButton);

        // 초기 이미지 생성
        Dimension d = getPreferredSize();
        bufferedImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        setImageBackground(bufferedImage); // 저장할 때 배경이 기본적으로 검정이므로 흰색으로 설정

        // 각종 이벤트 핸들러 등록
        rectButton.addActionListener(this);
        lineButton.addActionListener(this);
        circleButton.addActionListener(this);
        penButton.addActionListener(this);
        eraseButton.addActionListener(this);
        eraseAllButton.addActionListener(this);
        colorComboBox.addActionListener(this);
        strokeComboBox.addActionListener(this);
        saveButton.addActionListener(new SaveL(this, bufferedImage));
        openButton.addActionListener(new OpenL(this, bufferedImage));

        addMouseListener(this);
        addMouseMotionListener(this);

    }
    public void mousePressed(MouseEvent e) {
        firstPointer.setLocation(0, 0);
        secondPointer.setLocation(0, 0);
        firstPointer.setLocation(e.getX(), e.getY());
    }

    public void mouseReleased(MouseEvent e) {
        if (shapeString != "펜") {
            secondPointer.setLocation(e.getX(), e.getY());
            try {
                updatePaint();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        // 액션 이벤트 처리

        if (e.getSource().getClass().toString().contains("JButton")) {
            // 버튼 클릭 이벤트 처리
            shapeString = e.getActionCommand();
        }

        else if (e.getSource().equals(colorComboBox)) {
            // 색상 콤보박스 이벤트 처리
            colors = (Color) colorComboBox.getSelectedItem();
        }

        else if (e.getSource().equals(strokeComboBox)) {
            // 선 굵기 콤보박스 이벤트 처리
            stroke = (float) strokeComboBox.getSelectedItem();
        }

        if (e.getSource().getClass().toString().contains("JButton")) {
            // 버튼 클릭 이벤트에서 버튼 색상 초기화 및 선택한 버튼의 배경을 흰색으로 설정
            resetButtonColors();
            JButton clickedButton = (JButton) e.getSource();
            clickedButton.setBackground(Color.WHITE);
            shapeString = e.getActionCommand();
        }
    }

    public Dimension getPreferredSize() {
        // 컴포넌트의 적절한 기본 크기 반환
        return new Dimension(700, 600);
    }

    public void updatePaint() throws IOException {
        // 그림 업데이트 메서드
        width = Math.abs(secondPointer.x - firstPointer.x);
        height = Math.abs(secondPointer.y - firstPointer.y);

        minPointx = Math.min(firstPointer.x, secondPointer.x);
        minPointy = Math.min(firstPointer.y, secondPointer.y);

        Graphics2D g = bufferedImage.createGraphics();

        switch (shapeString) {

            case ("선"), ("펜"):
                // 선 또는 펜 그리기
                String message = colors + " " + stroke + " " + firstPointer.x + " " + firstPointer.y + " " + secondPointer.x + " " + secondPointer.y;
                sendMessage(socket, message);
                break;

            case ("네모"):
                // 사각형 그리기
                g.setColor(colors);
                g.setStroke(new BasicStroke(stroke));
                g.drawRect(minPointx, minPointy, width, height);
                break;

            case ("원"):
                // 원 그리기
                g.setColor(colors);
                g.setStroke(new BasicStroke(stroke));
                g.drawOval(minPointx, minPointy, width, height);
                break;

            case ("지우개"):
                // 지우개로 그리기
                g.setColor(new Color(63,61,62));
                g.setStroke(new BasicStroke(stroke));
                g.drawLine(firstPointer.x, firstPointer.y, secondPointer.x, secondPointer.y);
                break;

            case ("전체지우기"):
                // 전체 지우기
                setImageBackground(bufferedImage);
                shapeString ="";
                break;

            default:
                break;
        }
        g.dispose();
        repaint();
    }



    protected void paintComponent(Graphics g) {
        // 컴포넌트의 그리기 메서드
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // 이미지를 화면에 그림
        g.drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
    }



    private void resetButtonColors() {
        // 버튼의 배경 색상 초기화
        rectButton.setBackground(originalRectButtonColor);
        lineButton.setBackground(originalLineButtonColor);
        circleButton.setBackground(originalCircleButtonColor);
        penButton.setBackground(originalPenButtonColor);
        eraseButton.setBackground(originalEraseButtonColor);
        eraseAllButton.setBackground(originalEraseAllButtonColor);
        saveButton.setBackground(originalSaveButtonColor);
        openButton.setBackground(originalOpenButtonColor);
    }


    public void setImageBackground(BufferedImage bi) {
        // 이미지의 배경을 설정
        this.bufferedImage = bi;
        Graphics2D g = bufferedImage.createGraphics();
        Color backgroundColor = new Color(63, 61, 62);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 700, 600);
        g.dispose();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // 마우스 드래그 이벤트 처리
        width = Math.abs(secondPointer.x - firstPointer.x);
        height = Math.abs(secondPointer.y - firstPointer.y);

        minPointx = Math.min(firstPointer.x, secondPointer.x);
        minPointy = Math.min(firstPointer.y, secondPointer.y);

        if (shapeString == "펜" | shapeString == "지우개") {
            // 펜 또는 지우개로 그리기
            if (secondPointer.x != 0 && secondPointer.y != 0) {
                firstPointer.x = secondPointer.x;
                firstPointer.y = secondPointer.y;
            }
            secondPointer.setLocation(e.getX(), e.getY());
            try {
                updatePaint();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        } else if (shapeString == "선") {
            // 선 그리기
            Graphics g = getGraphics();

            g.drawLine(firstPointer.x, firstPointer.y, secondPointer.x, secondPointer.y);
            secondPointer.setLocation(e.getX(), e.getY());
            repaint();
            g.dispose();
        } else if (shapeString == "네모") {
            // 사각형 그리기
            Graphics g = getGraphics();
            g.setColor(Color.BLACK);
            g.setXORMode(getBackground());

            g.drawRect(minPointx, minPointy, width, height);
            secondPointer.setLocation(e.getX(), e.getY());
            repaint();
            g.dispose();
        } else if (shapeString == "원") {
            // 원 그리기
            Graphics g = getGraphics();
            g.setColor(Color.BLACK);
            g.setXORMode(getBackground());

            g.drawOval(minPointx, minPointy, width, height);
            secondPointer.setLocation(e.getX(), e.getY());

            g.dispose();
            repaint();
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) { }
    @Override
    public void mouseClicked(MouseEvent e) { }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }

    private void sendMessage(Socket socket, String message) throws IOException {
        // 메시지 전송 메서드
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.newLine();
        writer.flush();
    }
}

class OpenL implements ActionListener {
    RectPanel rectPanel;  // 해당 이벤트 리스너가 적용된 RectPanel 객체
    BufferedImage bufferedImage;  // 이미지 데이터를 저장하는 BufferedImage 객체
    JFileChooser jFileChooser = new JFileChooser();;  // 파일을 선택하는데 사용되는 파일 선택기

    // 생성자
    OpenL(RectPanel rectPanel, BufferedImage bufferedImage) {
        this.rectPanel = rectPanel;
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 파일 확장자 필터 추가
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG", "jpeg", "jpg", "png", "bmp", "gif");
        jFileChooser.addChoosableFileFilter(filter);

        // 파일 선택 대화상자 열기
        int rVal = jFileChooser.showOpenDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {  // "열기" 버튼이 클릭된 경우
            File selectedFile = jFileChooser.getSelectedFile();
            try {
                // 선택된 이미지 파일을 읽어와서 RectPanel의 BufferedImage에 설정
                rectPanel.bufferedImage = ImageIO.read(new File(selectedFile.getAbsolutePath()));
                rectPanel.repaint();  // 화면을 다시 그리도록 repaint 호출
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {  // "취소" 버튼이 클릭된 경우
            // 아무 동작도 수행하지 않음
        }
    }
}

class SaveL implements ActionListener {
    RectPanel rectPanel;  // 해당 이벤트 리스너가 적용된 RectPanel 객체
    BufferedImage bufferedImage;  // 이미지 데이터를 저장하는 BufferedImage 객체
    JFileChooser jFileChooser;  // 파일을 선택하는데 사용되는 파일 선택기

    // 생성자
    SaveL(RectPanel rectPanel, BufferedImage bufferedImage) {
        this.rectPanel = rectPanel;
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        jFileChooser = new JFileChooser();
        // 파일 필터 설정 (*.png 확장자의 파일만 허용)
        jFileChooser.setFileFilter(new FileNameExtensionFilter("*.png", "png"));

        // 파일 저장 대화상자 열기
        int rVal = jFileChooser.showSaveDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {  // "저장" 버튼이 클릭된 경우
            File file = jFileChooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(".png")) {
                // 파일 확장자가 .png가 아닌 경우 확장자를 .png로 변경
                file = new File(file.getAbsolutePath() + ".png");
            }
            try {
                // BufferedImage를 png 파일로 저장
                ImageIO.write(bufferedImage, "png", new File(file.getAbsolutePath()));
                System.out.println("saved Correctly " + file.getAbsolutePath());
            } catch (IOException e1) {
                System.out.println("Failed to save image");
            }
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {  // "취소" 버튼이 클릭된 경우
            System.out.println("No file choosen");
        }
    }
}