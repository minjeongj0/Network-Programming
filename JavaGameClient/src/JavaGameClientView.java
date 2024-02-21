
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.JToggleButton;
import javax.swing.JList;
import java.awt.Canvas;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.Vector;
import java.awt.SystemColor;

public class JavaGameClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private String RoomName;
	
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private int pen_size = 2; // minimum 2
	
	JPanel info1, info2, info3, info4;
	private Graphics gc_info1;
	private Graphics gc_info2;
	private Graphics gc_info3;
	private Graphics gc_info4;
	
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImageInfo1 = null;
	private Graphics gc2_info1 = null;
	private Image panelImageInfo2 = null;
	private Graphics gc2_info2 = null;
	private Image panelImageInfo3 = null;
	private Graphics gc2_info3 = null;
	private Image panelImageInfo4 = null;
	private Graphics gc2_info4 = null;
	
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null; 
	private Graphics gc2 = null;
	
	JLabel lblUserName_4;
	JLabel lblScore_4 ;
	JLabel lblScore ;
	
	JButton BLACK, WHITE, GRAY, LIGHTGRAY, RED, PINK, ORANGE, YELLOW, GREEN, YELLOWGREEN,
			BLUE, SKYBLUE, PURPLE, LIGHTPURPLE, BROWN, LIGHTBROWN;
	private Color pen_color=Color.blue;  //default
	

	ImageIcon back=new ImageIcon("src/back.jpg");
	ImageIcon imgInfo=new ImageIcon("src/Imginfo.jpg");
	Image img = imgInfo.getImage();
	
	
	private JPanel radioPanel;
	private String pen_shape="Dot"; //default 
	private JRadioButton[] radio= new JRadioButton[2];
	private String[] text= {"Dot", "Line"};
	private ButtonGroup g=new ButtonGroup();
	
	
	Vector<Point> vStart = new Vector<Point>();
	int ox,oy, nx, ny;
	
	String ip_addr;
	String port_no;
	
	/**
	 * Create the frame.
	 * @throws BadLocationException 
	 */
	public JavaGameClientView(String username, String ip, String port, String room)  {
		ip_addr=ip;
		port_no=port;
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 992, 522);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 222, 285, 184);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(12, 408, 210, 61);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(222, 408, 75, 61);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setForeground(Color.WHITE);
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 25));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(134, 99, 163, 59);
		contentPane.add(lblUserName);
		setVisible(true);
		
		JLabel lblRoomName = new JLabel("<dynamic>");
		lblRoomName.setHorizontalAlignment(SwingConstants.CENTER);
		lblRoomName.setFont(new Font("굴림", Font.BOLD, 14));
		lblRoomName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblRoomName.setBackground(Color.WHITE);
		lblRoomName.setBounds(12, 9, 95, 40);
		contentPane.add(lblRoomName);

		AppendText("Room: "+room+" User: " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);
		
		RoomName=room;
		lblRoomName.setText(room);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(12, 59, 50, 40);
		contentPane.add(imgBtn);

		JButton btnNewButton = new JButton("종 료");
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye", RoomName);
				//ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendObject(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(840, 9, 101, 47);
		contentPane.add(btnNewButton);
		

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(Color.WHITE);
		panel.setBounds(301, 66, 346, 403);
		contentPane.add(panel);
		gc = panel.getGraphics();
		
		// Image 영역 보관용. paint() 에서 이용한다.
		panelImage = createImage(panel.getWidth(), panel.getHeight());
		gc2 = panelImage.getGraphics();
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
		gc2.setColor(Color.BLACK);
		gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
		
		
		BLACK = new JButton("");
		BLACK.setBackground(Color.BLACK);
		BLACK.setForeground(Color.BLACK);
		BLACK.setBounds(408, 10, 18, 18);
		contentPane.add(BLACK);
		BLACK.addActionListener(new ColorActionListener());
		
		WHITE = new JButton("");
		WHITE.setBackground(Color.WHITE);
		WHITE.setForeground(Color.WHITE);
		WHITE.setBounds(408, 38, 18, 18);
		contentPane.add(WHITE);
		WHITE.addActionListener(new ColorActionListener());
		
		GRAY = new JButton("");
		GRAY.setBackground(Color.GRAY);
		GRAY.setBounds(438, 10, 18, 18);
		contentPane.add(GRAY);
		GRAY.addActionListener(new ColorActionListener());
		
		LIGHTGRAY = new JButton("");
		LIGHTGRAY.setBackground(Color.LIGHT_GRAY);
		LIGHTGRAY.setBounds(438, 38, 18, 18);
		contentPane.add(LIGHTGRAY);
		LIGHTGRAY.addActionListener(new ColorActionListener());
		
		RED = new JButton("");
		RED.setBackground(new Color(255, 0, 0));
		RED.setBounds(498, 10, 18, 18);
		contentPane.add(RED);
		RED.addActionListener(new ColorActionListener());
		
		PINK = new JButton("");
		PINK.setBackground(new Color(255, 153, 255));
		PINK.setBounds(498, 38, 18, 18);
		contentPane.add(PINK);
		PINK.addActionListener(new ColorActionListener());
		
		ORANGE = new JButton("");
		ORANGE.setBackground(new Color(255, 153, 51));
		ORANGE.setBounds(528, 10, 18, 18);
		contentPane.add(ORANGE);
		ORANGE.addActionListener(new ColorActionListener());
		
		YELLOW = new JButton("");
		YELLOW.setForeground(new Color(255, 204, 0));
		YELLOW.setBackground(new Color(255, 204, 0));
		YELLOW.setBounds(528, 38, 18, 18);
		contentPane.add(YELLOW);
		YELLOW.addActionListener(new ColorActionListener());
		
		GREEN = new JButton("");
		GREEN.setBackground(new Color(0, 102, 0));
		GREEN.setBounds(558, 10, 18, 18);
		contentPane.add(GREEN);
		GREEN.addActionListener(new ColorActionListener());
		
		YELLOWGREEN = new JButton("");
		YELLOWGREEN.setBackground(new Color(51, 204, 0));
		YELLOWGREEN.setBounds(558, 38, 18, 18);
		contentPane.add(YELLOWGREEN);
		YELLOWGREEN.addActionListener(new ColorActionListener());
		
		BLUE = new JButton("");
		BLUE.setBackground(new Color(0, 0, 204));
		BLUE.setBounds(588, 10, 18, 18);
		contentPane.add(BLUE);
		BLUE.addActionListener(new ColorActionListener());
		
		SKYBLUE = new JButton("");
		SKYBLUE.setBackground(new Color(0, 153, 255));
		SKYBLUE.setBounds(588, 38, 18, 18);
		contentPane.add(SKYBLUE);
		SKYBLUE.addActionListener(new ColorActionListener());
		
		PURPLE = new JButton("");
		PURPLE.setBackground(new Color(102, 0, 255));
		PURPLE.setBounds(618, 10, 18, 18);
		contentPane.add(PURPLE);
		PURPLE.addActionListener(new ColorActionListener());
		
		LIGHTPURPLE = new JButton("");
		LIGHTPURPLE.setBackground(new Color(204, 204, 255));
		LIGHTPURPLE.setBounds(618, 38, 18, 18);
		contentPane.add(LIGHTPURPLE);
		LIGHTPURPLE.addActionListener(new ColorActionListener());
		
		JButton colorButton = new JButton("Color");
		colorButton.setBounds(301, 10, 95, 46);
		contentPane.add(colorButton);
		colorButton.addActionListener(new ColorActionListener());
		
		BROWN = new JButton("");
		BROWN.setBackground(new Color(102, 51, 0));
		BROWN.setBounds(468, 10, 18, 18);
		contentPane.add(BROWN);
		BROWN.addActionListener(new ColorActionListener());
		
		LIGHTBROWN = new JButton("");
		LIGHTBROWN.setBackground(new Color(204, 153, 51));
		LIGHTBROWN.setBounds(468, 38, 18, 18);
		contentPane.add(LIGHTBROWN);
		LIGHTBROWN.addActionListener(new ColorActionListener());
		
		JButton Start = new JButton("\uC2DC\uC791");
		Start.setFont(new Font("굴림", Font.PLAIN, 14));
		Start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//ChatMsg msg = new ChatMsg(UserName, "600", "GameStart");
				ChatMsg msg = new ChatMsg(UserName, "600", "GameStart", RoomName);
				SendObject(msg);
			}	
		});
		Start.setBounds(222, 59, 75, 40);
		contentPane.add(Start);
		
		Erase = new JButton("\uC9C0\uC6B0\uAE30");
		Erase.setFont(new Font("굴림", Font.PLAIN, 14));
		Erase.setBounds(665, 9, 80, 47);
		contentPane.add(Erase);
		
		info1 = new JPanel();
		info1.setBorder(new LineBorder(new Color(0, 0, 0)));
		info1.setBackground(Color.WHITE);
		info1.setBounds(12, 99, 121, 121);
		contentPane.add(info1);
		gc_info1 = info1.getGraphics();
		
		
		
		lblScore = new JLabel("0");
		lblScore.setForeground(Color.WHITE);
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblScore.setFont(new Font("굴림", Font.BOLD, 25));
		lblScore.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblScore.setBackground(Color.BLACK);
		lblScore.setBounds(134, 156, 163, 64);
		contentPane.add(lblScore);
		
		info4 = new JPanel();
		info4.setBorder(new LineBorder(new Color(0, 0, 0)));
		info4.setBackground(Color.WHITE);
		info4.setBounds(665, 348, 121, 121);
		contentPane.add(info4);
		gc_info4 = info4.getGraphics();
		
		
		lblUserName_4 = new JLabel("<dynamic>");
		lblUserName_4.setForeground(Color.WHITE);
		lblUserName_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName_4.setFont(new Font("굴림", Font.BOLD, 25));
		lblUserName_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName_4.setBackground(Color.WHITE);
		lblUserName_4.setBounds(786, 348, 155, 61);
		contentPane.add(lblUserName_4);
		
		lblScore_4 = new JLabel("0");
		lblScore_4.setForeground(Color.WHITE);
		lblScore_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblScore_4.setFont(new Font("굴림", Font.BOLD, 25));
		lblScore_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblScore_4.setBackground(Color.WHITE);
		lblScore_4.setBounds(786, 408, 155, 61);
		contentPane.add(lblScore_4);
		
		info2 = new JPanel();
		info2.setBorder(new LineBorder(new Color(0, 0, 0)));
		info2.setBackground(Color.WHITE);
		info2.setBounds(665, 66, 121, 121);
		contentPane.add(info2);
		gc_info2 = info2.getGraphics();
		
		
		lblUserName_2 = new JLabel("<dynamic>");
		lblUserName_2.setForeground(Color.WHITE);
		lblUserName_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName_2.setFont(new Font("굴림", Font.BOLD, 25));
		lblUserName_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName_2.setBackground(Color.WHITE);
		lblUserName_2.setBounds(786, 66, 155, 59);
		contentPane.add(lblUserName_2);
		
		lblScore_2 = new JLabel("0");
		lblScore_2.setForeground(Color.WHITE);
		lblScore_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblScore_2.setFont(new Font("굴림", Font.BOLD, 25));
		lblScore_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblScore_2.setBackground(Color.WHITE);
		lblScore_2.setBounds(786, 126, 155, 59);
		contentPane.add(lblScore_2);
		
		info3 = new JPanel();
		info3.setBorder(new LineBorder(new Color(0, 0, 0)));
		info3.setBackground(Color.WHITE);
		info3.setBounds(665, 206, 121, 121);
		contentPane.add(info3);
		gc_info3 = info3.getGraphics();
		
		
		lblUserName_3 = new JLabel("<dynamic>");
		lblUserName_3.setForeground(Color.WHITE);
		lblUserName_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName_3.setFont(new Font("굴림", Font.BOLD, 25));
		lblUserName_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName_3.setBackground(Color.WHITE);
		lblUserName_3.setBounds(786, 206, 155, 64);
		contentPane.add(lblUserName_3);
		
		lblScore_3 = new JLabel("0");
		lblScore_3.setForeground(Color.WHITE);
		lblScore_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblScore_3.setFont(new Font("굴림", Font.BOLD, 25));
		lblScore_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblScore_3.setBackground(Color.WHITE);
		lblScore_3.setBounds(786, 269, 155, 59);
		contentPane.add(lblScore_3);
		
		Erase.addActionListener(new EraseActionListener());
		
		
		// Image 영역 보관용. paint() 에서 이용한다.
		panelImageInfo1 = createImage(info1.getWidth(), info1.getHeight());
		gc2_info1 = panelImageInfo1.getGraphics();
		gc2_info1.setColor(info1.getBackground());
		gc2_info1.fillRect(0,0, info1.getWidth(),  info1.getHeight());
		gc2_info1.setColor(Color.BLACK);
		gc2_info1.drawRect(0,0, info1.getWidth()-1,  info1.getHeight()-1);
				
		panelImageInfo2 = createImage(info2.getWidth(), info2.getHeight());
		gc2_info2 = panelImageInfo2.getGraphics();
		gc2_info2.setColor(info2.getBackground());
		gc2_info2.fillRect(0,0, info2.getWidth(),  info2.getHeight());
		gc2_info2.setColor(Color.BLACK);
		gc2_info2.drawRect(0,0, info2.getWidth()-1,  info2.getHeight()-1);
				
		panelImageInfo3 = createImage(info3.getWidth(), info3.getHeight());
		gc2_info3 = panelImageInfo3.getGraphics();
		gc2_info3.setColor(info3.getBackground());
		gc2_info3.fillRect(0,0, info3.getWidth(),  info3.getHeight());
		gc2_info3.setColor(Color.BLACK);
		gc2_info3.drawRect(0,0, info3.getWidth()-1,  info3.getHeight()-1);
				
		panelImageInfo4 = createImage(info4.getWidth(), info4.getHeight());
		gc2_info4 = panelImageInfo4.getGraphics();
		gc2_info4.setColor(info4.getBackground());
		gc2_info4.fillRect(0,0, info4.getWidth(),  info4.getHeight());
		gc2_info4.setColor(Color.BLACK);
		gc2_info4.drawRect(0,0, info4.getWidth()-1,  info4.getHeight()-1);
		
		
		//초기 이미지 세팅
		//gc2_info1.drawImage(img, 0, 0, info1.getWidth(), info1.getHeight(), info1);
		//gc2_info2.drawImage(img, 0, 0, info2.getWidth(), info2.getHeight(), info2);
		//gc2_info3.drawImage(img, 0, 0, info3.getWidth(), info3.getHeight(), info3);
		//gc2_info4.drawImage(img, 0, 0, info4.getWidth(), info4.getHeight(), info4);
		
		JPanel radioPanel = new JPanel();
		radioPanel.setBackground(new Color(176, 196, 222));
		radioPanel.setBounds(163, 9, 134, 40);
		contentPane.add(radioPanel);
		
		JButton btnNewButton_1 = new JButton("\uD78C\uD2B8");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "300", "HINT", RoomName);
				SendObject(msg);
			}
		});
		
		btnNewButton_1.setBounds(146, 60, 76, 40);
		contentPane.add(btnNewButton_1);
		
		JPanel panel_1 = new JPanel() {
			public void paintComponent(Graphics g) {
				// Approach 1: Dispaly image at at full size
            	g.drawImage(back.getImage(), 0, 0, null);
                setOpaque(false); //그림을 표시하게 설정,투명하게 조절
                super.paintComponent(g);
			}
		};
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(1, 0, 987, 494);
		contentPane.add(panel_1);
		
		for(int i=0;i<radio.length;i++) { //라디오 버튼 생성
			radio[i]=new JRadioButton(text[i]);
			g.add(radio[i]);
			radioPanel.add(radio[i]);
			radio[i].addItemListener(new MyItemListener()); // Item리스너 등록
		}
		radio[0].setSelected(true);  //Dot를 선택 상태로
		

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			//ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello", RoomName);
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);
			MyMouseEvent mouse = new MyMouseEvent();
			panel.addMouseMotionListener(mouse);
			panel.addMouseListener(mouse);
			MyMouseWheelEvent wheel = new MyMouseWheelEvent();
			panel.addMouseWheelListener(wheel);


		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

	}
	
	// Item Listener - pen shape 선택
	public class MyItemListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if(e.getStateChange()==ItemEvent.DESELECTED)  // 아무것도 선택하지 않음
				pen_shape="";
			if(radio[0].isSelected())  //Dot
				pen_shape="Dot";
			else if(radio[1].isSelected())  // Line
				pen_shape="Line";
			/*else if(radio[1].isSelected())  //Rectangle
				pen_shape="Rectangle";
			else if(radio[2].isSelected())  // Circle
				pen_shape="Circle";*/

		}

	}


	public void paint(Graphics g) {
		super.paint(g);
		// Image 영역이 가려졌다 다시 나타날 때 그려준다.
		gc.drawImage(panelImage, 0, 0, this);
	}
	
	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
					case "110":
						if(cm.data.charAt(0)=='2')
							lblUserName_2.setText(cm.data.substring(1));
						else if(cm.data.charAt(0)=='3')
							lblUserName_3.setText(cm.data.substring(1));
						else if(cm.data.charAt(0)=='4')
							lblUserName_4.setText(cm.data.substring(1));
						break;
							
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg); // 내 메세지는 우측에
						else
							AppendText(msg);
						break;
						
					case "300": // Image 첨부
						AppendImage(cm.img);
						break;
						
					case "500": // Mouse Event 수신
						DoMouseEvent(cm);
						break;
						
					case "600":  //game start 수신
						AppendText(msg);
						break;
						
					case "700":  //game turn 수신
						StyledDocument doc=textArea.getStyledDocument();
						SimpleAttributeSet center=new SimpleAttributeSet();
						StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
						StyleConstants.setForeground(center, Color.red);
						doc.setParagraphAttributes(doc.getLength(), 1, center, false);
						try {
							doc.insertString(doc.getLength(),msg+"\n", center );
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
						
					case "800":  // answer 수신
						AppendText(msg);
						break;
						
					case "900":
						//msg = String.format("[%s - %s] %s", cm.room, cm.UserName, cm.data);
						String[] args = cm.data.split(" "); // 단어들을 분리한다. (점수 username) 
						//AppendText("arg0="+args[0]+" arg1= "+args[1]);
						if(args[1].matches(lblUserName.getText())) { 
							lblScore.setText(args[0]);
						}
						else if(args[1].matches(lblUserName_2.getText())) {
							lblScore_2.setText(args[0]);
						}
						else if(args[1].matches(lblUserName_3.getText())) {
							lblScore_3.setText(args[0]);
						}
						else if(args[1].matches(lblUserName_4.getText())) {
							lblScore_4.setText(args[0]);
						}
						break;
						
					case "1000": // ImageInfo 첨부
						AppendImageInfo(cm.img, cm.UserName);
						break;
						
					case "550":  //line
						DoLine(cm);
						break;
					case "850" : //Erase
						ImageIcon imgInfo=new ImageIcon("src/white.PNG");
						Image img = imgInfo.getImage();
						gc2.drawImage(img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
						gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
						break;
						
					case "1100":
						//AppendText(msg);
						JavaGameClientRoom room=new JavaGameClientRoom(UserName, ip_addr, port_no);
						setVisible(false);
						break;
						
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
		
	//Color Listener
	public class ColorActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String cmd=e.getActionCommand();  //button name
			if(cmd.equals("Color")) {
				Color selectedColor=JColorChooser.showDialog(null, "Color", Color.yellow);
				if(selectedColor!=null)
					pen_color=selectedColor;		
			}
			else if(e.getSource()==BLACK)	pen_color=Color.black;
			else if(e.getSource()==WHITE)	pen_color=Color.white;
			else if(e.getSource()==GRAY)	pen_color=Color.gray;
			else if(e.getSource()==LIGHTGRAY)	pen_color=Color.LIGHT_GRAY;
			else if(e.getSource()==BROWN)	pen_color=new Color(153, 56, 0);
			else if(e.getSource()==LIGHTBROWN)	pen_color=new Color(225, 128, 72);
			else if(e.getSource()==RED)	pen_color=Color.red;
			else if(e.getSource()==PINK)	pen_color=Color.pink;
			else if(e.getSource()==ORANGE)	pen_color=new Color(255, 94, 0);
			else if(e.getSource()==YELLOW)	pen_color=Color.yellow;
			else if(e.getSource()==GREEN)	pen_color=Color.green;
			else if(e.getSource()==YELLOWGREEN)	pen_color=new Color(134, 229, 127);
			else if(e.getSource()==BLUE)	pen_color=Color.blue;
			else if(e.getSource()==SKYBLUE)	pen_color=new Color(92, 209, 229);
			else if(e.getSource()==PURPLE)	pen_color=new Color(128, 65, 217);
			else if(e.getSource()==LIGHTPURPLE)	pen_color=new Color(218, 155, 255);
			
			
		}

	}

	
	//Erase Listener
	public class EraseActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			pen_color=Color.white;
		}
	}

	// Mouse Event 수신 처리
	public void DoMouseEvent(ChatMsg cm) {
		Color c;
		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
			return;
		//c = new Color(255, 0, 0); // 다른 사람 것은 Red
		c=cm.pen_color;
		gc2.setColor(c);
		//gc2.fillOval(cm.mouse_e.getX() - pen_size/2, cm.mouse_e.getY() - cm.pen_size/2, cm.pen_size, cm.pen_size);
		if(cm.pen_shape.matches("Dot"))
			gc2.fillOval(cm.mouse_e.getX() - pen_size/2, cm.mouse_e.getY() - cm.pen_size/2, cm.pen_size, cm.pen_size);
		gc.drawImage(panelImage, 0, 0, panel);
	}
	
	// Line 수신 처리
	public void DoLine(ChatMsg cm) {
		Color c;
		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
			return;
		c=cm.pen_color;
		gc2.setColor(c);
		//AppendText("ox= "+cm.ox+", oy= "+cm.oy);
		//AppendText("nx= "+cm.nx+", ny= "+cm.ny);
		gc2.drawLine(cm.ox, cm.oy, cm.nx, cm.ny);
		gc.drawImage(panelImage, 0, 0, panel);
			
	}

	public void SendMouseEvent(MouseEvent e) {
		//ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE", RoomName);
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		cm.pen_shape=pen_shape;
		cm.pen_color=pen_color;
		SendObject(cm);
	}
	
	class MyMouseWheelEvent implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if (e.getWheelRotation() < 0) { // 위로 올리는 경우 pen_size 증가
				if (pen_size < 20)
					pen_size++;
			} else {
				if (pen_size > 2)
					pen_size--;
			}

		}
		
	}
	
	
	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
			//Color c = new Color(0,0,255);
			Color c;
			c=pen_color;
			gc2.setColor(c);
			//gc2.fillOval(e.getX()-pen_size/2, e.getY()-pen_size/2, pen_size, pen_size);
			
			if(pen_shape=="Dot")
				gc2.fillOval(e.getX() - pen_size/2, e.getY() - pen_size/2, pen_size, pen_size);
			
			else if(pen_shape=="Line") {
				vStart.add(e.getPoint());
				for (int i = 1; i < vStart.size(); i++) {
					if (vStart.get(i - 1) == null)
						continue;
				else if (vStart.get(i) == null)
					continue;
				else {	
					ox=(int)vStart.get(i-1).getX()-pen_size;
					oy=(int)vStart.get(i-1).getY()-pen_size;
					nx=(int) vStart.get(i).getX()-pen_size;
					ny=(int) vStart.get(i).getY()-pen_size;
					
					ChatMsg cm = new ChatMsg(UserName, "550", "MOUSE", RoomName);
					cm.ox=ox;
					cm.oy=oy;
					cm.nx=nx;
					cm.ny=ny;
					cm.pen_size = pen_size;
					cm.pen_shape=pen_shape;
					cm.pen_color=pen_color;
					SendObject(cm);	
					
					gc2.drawLine((int) vStart.get(i - 1).getX()-pen_size, (int) vStart.get(i - 1).getY()-pen_size,
													(int) vStart.get(i).getX()-pen_size, (int) vStart.get(i).getY()-pen_size);
					gc.drawImage(panelImage, 0, 0, panel);
			
					}
				}
			}
			
			// panelImnage는 paint()에서 이용한다.
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			//Color c = new Color(0,0,255);
			Color c=pen_color;
			gc2.setColor(c);
			//gc2.fillOval(e.getX()-pen_size/2, e.getY()-pen_size/2, pen_size, pen_size);
			
			if(pen_shape=="Dot") {
				gc2.fillOval(e.getX() - pen_size/2, e.getY() - pen_size/2, pen_size, pen_size);
			}
				
			else if(pen_shape=="Line") {
				vStart.add(null);
				vStart.add(e.getPoint());
			}
			
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseEntered " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.YELLOW);

		}

		@Override
		public void mouseExited(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseExited " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.CYAN);

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(pen_shape=="Line") {		
				vStart.add(null);
				vStart.add(e.getPoint());
			}
			//lblMouseEvent.setText(e.getButton() + " mousePressed " + e.getX() + "," + e.getY());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseReleased " + e.getX() + "," + e.getY());
			// 드래그중 멈출시 보임
			if(pen_shape=="Line") {
				vStart.removeAllElements();
			}
		}
		
	}
	

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	// 이미지 전송
	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 
			// 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				fd.setVisible(true);
				
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					//ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
					ChatMsg obcm = new ChatMsg(UserName, "1000", "IMGINFO", RoomName);
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					SendObject(obcm);
				}
			}
		}
	}

	ImageIcon icon1 = new ImageIcon("src/icon1.jpg");
	private JButton Erase;
	private JLabel lblUserName_2;
	private JLabel lblScore_2;
	private JLabel lblUserName_3;
	private JLabel lblScore_3;

	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public synchronized void AppendText(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		//textArea.setCaretPosition(len);
		//textArea.replaceSelection(msg + "\n");
		
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg+"\n", left );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	// 화면 우측에 출력
	public synchronized void AppendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.	
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLUE);	
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),msg+"\n", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public synchronized void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
			//textArea.insertIcon(new_icon);
		} else {
			//textArea.insertIcon(ori_icon);
			new_img = ori_img;
		}
		//len = textArea.getDocument().getLength();
		//textArea.setCaretPosition(len);
		//textArea.replaceSelection("\n");

		gc2.drawImage(ori_img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
		gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
	}

	//이미지 정보 세팅
	public synchronized void AppendImageInfo(ImageIcon ori_icon, String userName) {
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 121;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 121;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
		} else {
			new_img = ori_img;
		}
		
		//gc2.drawImage(ori_img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
		//gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
		
		if(userName.matches(lblUserName.getText())) {
			gc2_info1.drawImage(ori_img,  0,  0, info1.getWidth(), info1.getHeight(), info1);
			gc_info1.drawImage(panelImageInfo1,  0,  0, info1.getWidth(), info1.getHeight(), info1);
			
		}
		else if(userName.matches(lblUserName_2.getText())) {
			gc2_info2.drawImage(ori_img,  0,  0, info2.getWidth(), info2.getHeight(), info2);
			gc_info2.drawImage(panelImageInfo2,  0,  0, info2.getWidth(), info2.getHeight(), info2);
			
		}
		else if(userName.matches(lblUserName_3.getText())) {
			gc2_info3.drawImage(ori_img,  0,  0, info3.getWidth(), info3.getHeight(), info3);
			gc_info3.drawImage(panelImageInfo3,  0,  0, info3.getWidth(), info3.getHeight(), info3);
			
		}
		else if(userName.matches(lblUserName_4.getText())) {
			gc2_info4.drawImage(ori_img,  0,  0, info4.getWidth(), info4.getHeight(), info4);
			gc_info4.drawImage(panelImageInfo4,  0,  0, info4.getWidth(), info4.getHeight(), info4);
			
		}
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server에게 network으로 전송
	public synchronized void SendMessage(String msg) {
		try {
			//ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			ChatMsg obcm = new ChatMsg(UserName, "200", msg, RoomName);
			oos.writeObject(obcm);
		} catch (IOException e) {
			AppendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public synchronized void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			AppendText("SendObject Error");
		}
	}
}
