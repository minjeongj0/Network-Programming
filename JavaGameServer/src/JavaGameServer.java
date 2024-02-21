//JavaObjServer.java ObjectStream ��� ä�� Server

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class JavaGameServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	private Vector UserVec = new Vector(); // ����� ����ڸ� ������ ����
	
	//private Vector RoomVec=new Vector();  // ����� ���ӹ��� ������ ����
	private Vector Room1Vec = new Vector(); // room1�� ����� ����ڸ� ������ ����
	private Vector Room2Vec = new Vector(); // room2�� ����� ����ڸ� ������ ����
	private Vector Room3Vec = new Vector(); // room3�� ����� ����ڸ� ������ ����
	private Vector Room4Vec = new Vector(); // room4�� ����� ����ڸ� ������ ����
	private Vector Room5Vec = new Vector(); // room5�� ����� ����ڸ� ������ ����
	private Vector Room6Vec = new Vector(); // room6�� ����� ����ڸ� ������ ����
	
	//private String answer=new String("ȣ����");
	String answer;
	String answers[]=new String[4];
	String questions[]= {"ȣ����", "�䳢", "��", "�ڵ���", "�ſ�", "��ǥ", "����", "����", "å", "�ڵ���", "�ٶ�����", "����"
			, "�̾���", "���"};
	
	int Tcount=0;  //���� Ƚ��

	
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
	 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaGameServer frame = new JavaGameServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaGameServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
		
	}

	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					AppendText("���ο� ������ from " + client_socket);
					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					AppendText("���� ������ �� " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public synchronized void AppendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public synchronized void AppendObject(ChatMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("room = " + msg.room + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User �� �����Ǵ� Thread
	// Read One ���� ��� -> Write All
	class UserService extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String UserStatus;
				
		private Vector room_vc;
		public String RoomName = "";
		
		public int UserScore=0;
		public int count=0;
		

		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// �Ű������� �Ѿ�� �ڷ� ����
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			//this.room_vc=RoomVec;  // ���ӹ� �߰�
			
			try {
				dos = new DataOutputStream(os);

				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());

			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Login() {
			AppendText("���ο� ������ " + UserName + " ����.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "��! "+ RoomName+"�� ���Ű� ȯ���մϴ�.\n"); // ����� ����ڿ��� ���������� �˸�
			
			//ChatMsg info = new ChatMsg("SERVER", "110", UserName);
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(user.UserName.matches(UserName)==false) {// ����� ����ڿ��� ���ο� ���� �̸� ����
					ChatMsg info = new ChatMsg("SERVER", "110", user_vc.size()+UserName);
					WriteOthersObject(info);
				}
			}
			
			for (int i = 0; i < user_vc.size(); i++) {  // ���ο� ��������
				UserService user = (UserService) user_vc.elementAt(i);
				if(user.UserName.matches(UserName)==false) {
					ChatMsg info = new ChatMsg("SERVER", "110", (i+2)+user.UserName);
					WriteOneObject(info);
				}
			}
			
			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			WriteOthers(msg); // ���� user_vc�� ���� ������ user�� ���Ե��� �ʾҴ�.
		}

		public void Logout() {
			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			UserVec.removeElement(this); // Logout�� ���� ��ü�� ���Ϳ��� �����
			WriteAll(msg); // ���� ������ �ٸ� User�鿡�� ����
			AppendText("����� " + "[" + UserName + "] ����. ���� ������ �� " + UserVec.size());
		}

		// ��� User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}
		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O" || user.UserStatus == "T")
					user.WriteOneObject(ob);
			}
		}
		
		// ���� ������ User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void WriteOthersObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O" || user.UserStatus == "T")
					user.WriteOneObject(ob);
			}
		}

		// ���� ������ User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O" || user.UserStatus=="T")
					user.WriteOne(str);
			}
		}

		// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
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
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread�� ����ϴ� Client ���� 1:1 ����
		public synchronized void WriteOne(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}

		// �ӼӸ� ����
		public synchronized void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("�ӼӸ�", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		public synchronized void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		
		
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					//int Tcount=0;
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					} else
						continue;
					if (cm.code.matches("100")) {
						UserName = cm.UserName;
						RoomName=cm.room;
						UserStatus = "O"; // Online ����
						UserScore=0;
						Login();
					} 
					
					else if (cm.code.matches("200")) {
						msg = String.format("[%s - %s] %s", cm.room, cm.UserName, cm.data);
						AppendText(msg); // server ȭ�鿡 ���
						//String[] args = msg.split(" "); // �ܾ���� �и��Ѵ�. 
						// �Ϲ� ä�� �޽���
						//UserStatus = "O";
						if(UserStatus =="T" && cm.data.matches(answers[Tcount]))  // �����ڰ� ������ ���ϸ� 
							continue;
							
						else if(UserStatus!="T"&& cm.data.matches(answers[Tcount])) {  // ������ ���߸�
							WriteAllObject(cm); 
							ChatMsg ans = new ChatMsg("SERVER", "800", UserName+"���� "+answers[Tcount]+"�� ������ϴ�!");
							AppendText("[" + UserName + "] ����");
							//answer=answers[++Tcount];
							answer="";
							WriteAllObject(ans);
									
							for (int i = 0; i < user_vc.size(); i++) {  // �������� UserStatus ����
								UserService user = (UserService) user_vc.elementAt(i);
								if (user != this && user.UserStatus=="T")
									user.UserStatus="O";
							}
								
									
							ChatMsg score = new ChatMsg("SERVER", "900", (++UserScore)+" "+UserName);  // ���� update
							WriteAllObject(score);
								
							ChatMsg erase = new ChatMsg("SERVER", "850", "Erase");  // ��ü �����
							WriteAllObject(erase);
							if(Tcount<3) {	// 4 ���� ����, ���� ����� ���� ����
								UserStatus="T";
								WriteOthers(UserName+"���� �׸��� ���߼���");
								answer=answers[++Tcount];
								
								// ���� ����
								ChatMsg turn = new ChatMsg("SERVER", "700", answer+" �׸�����!");
								AppendText("[" + UserName + "] TURN");
								WriteOneObject(turn);	
							}
							else {  // ������ 4�� ���� -> ���� ����
								// ��������
								ChatMsg end = new ChatMsg("SERVER", "1100", "GameEnd");
								AppendText("GameEnd");
								WriteAllObject(end);
							}
							
							
							/*for (int i = 0; i < user_vc.size(); i++) {  // �������� UserStatus ����
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.count==0 && user.UserStatus=="O") {  // ������ �� �� ���
									//user.UserStatus="T";
									user.count++;
									WriteOthers(user.UserName+"���� �׸��� ���߼���");
										
									// ���� �������� �̱�
									Random random=new Random();
									int randNum=random.nextInt(questions.length);
									answer=questions[randNum];
									// ���� ����
									ChatMsg turn = new ChatMsg("SERVER", "700", answer+" �׸�����!");
									AppendText("[" + user.UserName + "] TURN");
									user.UserStatus="T";
									WriteOneObject(turn);	
									break;
								}							
							}//for��
						*/	
						}
						else WriteAllObject(cm);					
					}// 200
					
					else if (cm.code.matches("400")) { // logout message ó��
						Logout();
						break;
					}
					else if(cm.code.matches("600")) {  // game start
						if(user_vc.size()>=2) {  //2�� �̻� ���� ����
							//Tcount++;
							//count++;  // ���� Ƚ��
							
							String game = "[" + UserName + "]���� ���� ������ ��û�ϼ̽��ϴ�.\n";
							WriteOthers(game); // ���� ������ �ٸ� User�鿡�� ����
							WriteOne("������ �����մϴ�!");
							AppendText("[" + UserName + "] ���� ��û");
							WriteOthers(UserName+"���� �׸��� ���߼���");
							
							// ���� �������� �̱�, �ߺ�����
							int a[]=new int[questions.length];
							Random random=new Random();
							for(int i=0;i<5;i++) { 
								a[i]=random.nextInt(questions.length);
								//answers[i]=questions[a[i]];
								//AppendText("i="+i+"answers= "+answers[i]);
								
								for(int j=0;j<i;j++) { // �ߺ�����
									if(a[i]==a[j])
										i--;
									else {
										//AppendText("���� ����: "+ a[j]);
										answers[i-1]=questions[a[j]];
										//AppendText("i="+i+"answers= "+answers[i]);
									}						
								}
							}
							
							for(int i=0;i<4;i++) {
								AppendText("i="+i+"answers= "+answers[i]);
							}
							//int randNum=random.nextInt(questions.length);
							//answer=questions[randNum];
							
							answer=answers[Tcount];
							// ������ ��û�� ����� ���� ����
							ChatMsg turn = new ChatMsg("SERVER", "700", answer+" �׸�����!");
							AppendText("[" + UserName + "] TURN"+Tcount);
							UserStatus="T";
							WriteOneObject(turn);						
						}
						else {  //1���϶��� ���� ���� �Ұ�
							WriteOne("�ٸ� ������ ��ٸ�����!");
						}
					}
					else if(cm.code.matches("300")) {
						if(UserStatus=="T") {
							String s="src/image/"+answer+".png";
							ImageIcon img=new ImageIcon(s);
							ChatMsg hint = new ChatMsg("SERVER", "300", "IMG");
							hint.img=img;
							WriteOneObject(hint);
						}
					}
					
					else { // 300, 500, ...1000  ��Ÿ object�� ��� ����Ѵ�.
						WriteAllObject(cm);
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {;
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // �������� ���� ��ü�� ���Ϳ��� �����
						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����
			} // while
		} // run
	}

}
