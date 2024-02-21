//JavaObjServer.java ObjectStream 기반 채팅 Server

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

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	
	//private Vector RoomVec=new Vector();  // 연결된 게임방을 저장할 벡터
	private Vector Room1Vec = new Vector(); // room1에 연결된 사용자를 저장할 벡터
	private Vector Room2Vec = new Vector(); // room2에 연결된 사용자를 저장할 벡터
	private Vector Room3Vec = new Vector(); // room3에 연결된 사용자를 저장할 벡터
	private Vector Room4Vec = new Vector(); // room4에 연결된 사용자를 저장할 벡터
	private Vector Room5Vec = new Vector(); // room5에 연결된 사용자를 저장할 벡터
	private Vector Room6Vec = new Vector(); // room6에 연결된 사용자를 저장할 벡터
	
	//private String answer=new String("호랑이");
	String answer;
	String answers[]=new String[4];
	String questions[]= {"호랑이", "토끼", "새", "핸드폰", "거울", "음표", "연필", "물병", "책", "자동차", "바람개비", "웃음"
			, "이어폰", "사과"};
	
	int Tcount=0;  //출제 횟수

	
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	 

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
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
		
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public synchronized void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public synchronized void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("room = " + msg.room + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
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
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			//this.room_vc=RoomVec;  // 게임방 추가
			
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
			AppendText("새로운 참가자 " + UserName + " 입장.");
			WriteOne("Welcome to Java chat server\n");
			WriteOne(UserName + "님! "+ RoomName+"에 오신걸 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
			
			//ChatMsg info = new ChatMsg("SERVER", "110", UserName);
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(user.UserName.matches(UserName)==false) {// 연결된 사용자에게 새로운 유저 이름 전송
					ChatMsg info = new ChatMsg("SERVER", "110", user_vc.size()+UserName);
					WriteOthersObject(info);
				}
			}
			
			for (int i = 0; i < user_vc.size(); i++) {  // 새로운 유저에게
				UserService user = (UserService) user_vc.elementAt(i);
				if(user.UserName.matches(UserName)==false) {
					ChatMsg info = new ChatMsg("SERVER", "110", (i+2)+user.UserName);
					WriteOneObject(info);
				}
			}
			
			String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
			WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
		}

		public void Logout() {
			String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O" || user.UserStatus == "T")
					user.WriteOneObject(ob);
			}
		}
		
		// 나를 제외한 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteOthersObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O" || user.UserStatus == "T")
					user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O" || user.UserStatus=="T")
					user.WriteOne(str);
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
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 귓속말 전송
		public synchronized void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("귓속말", "200", msg);
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
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
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
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
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
						UserStatus = "O"; // Online 상태
						UserScore=0;
						Login();
					} 
					
					else if (cm.code.matches("200")) {
						msg = String.format("[%s - %s] %s", cm.room, cm.UserName, cm.data);
						AppendText(msg); // server 화면에 출력
						//String[] args = msg.split(" "); // 단어들을 분리한다. 
						// 일반 채팅 메시지
						//UserStatus = "O";
						if(UserStatus =="T" && cm.data.matches(answers[Tcount]))  // 출제자가 정답을 말하면 
							continue;
							
						else if(UserStatus!="T"&& cm.data.matches(answers[Tcount])) {  // 정답을 맞추면
							WriteAllObject(cm); 
							ChatMsg ans = new ChatMsg("SERVER", "800", UserName+"님이 "+answers[Tcount]+"을 맞췄습니다!");
							AppendText("[" + UserName + "] 정답");
							//answer=answers[++Tcount];
							answer="";
							WriteAllObject(ans);
									
							for (int i = 0; i < user_vc.size(); i++) {  // 출제자의 UserStatus 리셋
								UserService user = (UserService) user_vc.elementAt(i);
								if (user != this && user.UserStatus=="T")
									user.UserStatus="O";
							}
								
									
							ChatMsg score = new ChatMsg("SERVER", "900", (++UserScore)+" "+UserName);  // 점수 update
							WriteAllObject(score);
								
							ChatMsg erase = new ChatMsg("SERVER", "850", "Erase");  // 전체 지우기
							WriteAllObject(erase);
							if(Tcount<3) {	// 4 문제 출제, 맞춘 사람이 문제 출제
								UserStatus="T";
								WriteOthers(UserName+"님의 그림을 맞추세요");
								answer=answers[++Tcount];
								
								// 문제 출제
								ChatMsg turn = new ChatMsg("SERVER", "700", answer+" 그리세요!");
								AppendText("[" + UserName + "] TURN");
								WriteOneObject(turn);	
							}
							else {  // 문제를 4번 출제 -> 게임 종료
								// 게임종료
								ChatMsg end = new ChatMsg("SERVER", "1100", "GameEnd");
								AppendText("GameEnd");
								WriteAllObject(end);
							}
							
							
							/*for (int i = 0; i < user_vc.size(); i++) {  // 출제자의 UserStatus 리셋
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.count==0 && user.UserStatus=="O") {  // 출제를 안 한 사람
									//user.UserStatus="T";
									user.count++;
									WriteOthers(user.UserName+"님의 그림을 맞추세요");
										
									// 문제 랜덤으로 뽑기
									Random random=new Random();
									int randNum=random.nextInt(questions.length);
									answer=questions[randNum];
									// 문제 출제
									ChatMsg turn = new ChatMsg("SERVER", "700", answer+" 그리세요!");
									AppendText("[" + user.UserName + "] TURN");
									user.UserStatus="T";
									WriteOneObject(turn);	
									break;
								}							
							}//for문
						*/	
						}
						else WriteAllObject(cm);					
					}// 200
					
					else if (cm.code.matches("400")) { // logout message 처리
						Logout();
						break;
					}
					else if(cm.code.matches("600")) {  // game start
						if(user_vc.size()>=2) {  //2인 이상 게임 시작
							//Tcount++;
							//count++;  // 출제 횟수
							
							String game = "[" + UserName + "]님이 게임 시작을 요청하셨습니다.\n";
							WriteOthers(game); // 나를 제외한 다른 User들에게 전송
							WriteOne("게임을 시작합니다!");
							AppendText("[" + UserName + "] 게임 요청");
							WriteOthers(UserName+"님의 그림을 맞추세요");
							
							// 문제 랜덤으로 뽑기, 중복제거
							int a[]=new int[questions.length];
							Random random=new Random();
							for(int i=0;i<5;i++) { 
								a[i]=random.nextInt(questions.length);
								//answers[i]=questions[a[i]];
								//AppendText("i="+i+"answers= "+answers[i]);
								
								for(int j=0;j<i;j++) { // 중복제거
									if(a[i]==a[j])
										i--;
									else {
										//AppendText("랜덤 숫자: "+ a[j]);
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
							// 게임을 요청한 사람이 문제 출제
							ChatMsg turn = new ChatMsg("SERVER", "700", answer+" 그리세요!");
							AppendText("[" + UserName + "] TURN"+Tcount);
							UserStatus="T";
							WriteOneObject(turn);						
						}
						else {  //1인일때는 게임 시작 불가
							WriteOne("다른 유저를 기다리세요!");
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
					
					else { // 300, 500, ...1000  기타 object는 모두 방송한다.
						WriteAllObject(cm);
					} 
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {;
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}
