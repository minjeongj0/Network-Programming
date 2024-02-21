import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.SystemColor;

public class JavaGameClientRoom extends JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtRoomName;
	JLabel MSG = new JLabel("");
	JList<String> list;
	private Vector<String> v = new Vector<String>();  //name
	ImageIcon back=new ImageIcon("src/room1.jpg");

	public JavaGameClientRoom(String username, String ip_addr, String port_no){
		//setResizable(false);
		setVisible(true);
		setTitle("게임방 선택");	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 476, 416);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnRoomCreate = new JButton("Create");
		btnRoomCreate.setBounds(241, 208, 205, 38);
		contentPane.add(btnRoomCreate);
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String roomname=txtRoomName.getText().trim();
				if(roomname.isEmpty() ) {
					MSG.setText("!방이름을 쓰세요!");
				}
				//else if(){  // 인원수가 다 찼을 때
					//MSG.setText("인원수가 다 찼습니다. 다른 방을 선택하세요");
					//txtRoomName.setText("");
				//}
				else {  // 게임방으로 들어가기
					new JavaGameClientView(username, ip_addr, port_no, roomname);
					setVisible(false);
				}
				
			}
		});
		
		btnEnter.setBounds(241, 270, 205, 38);
		contentPane.add(btnEnter);
		
		JLabel lblRoomName = new JLabel("Room Name");
		lblRoomName.setBounds(306, 67, 82, 33);
		contentPane.add(lblRoomName);
		
		txtRoomName = new JTextField();
		txtRoomName.setHorizontalAlignment(SwingConstants.CENTER);
		txtRoomName.setColumns(10);
		txtRoomName.setBounds(241, 110, 205, 33);
		contentPane.add(txtRoomName);
		MSG.setForeground(SystemColor.menuText);
		MSG.setFont(new Font("굴림", Font.BOLD | Font.ITALIC, 15));
		
		MSG.setBounds(12, 336, 432, 38);
		contentPane.add(MSG);
		
		JScrollPane scroll=new JScrollPane();
		scroll.setBounds(12, 83, 171, 250);
		contentPane.add(scroll);
		
		v.addElement("room1");
		v.addElement("room2");
		v.addElement("room3");
		v.addElement("room4");
		v.addElement("room5");
		v.addElement("room6");
		
		
		list = new JList<String>(v);
		list.setFont(new Font("굴림", Font.PLAIN, 25));
		scroll.setViewportView(list);
		
		JLabel lblPassword_1 = new JLabel(username+"님 \uBC29\uC744 \uB9CC\uB4E4\uAC70\uB098 \uC120\uD0DD\uD558\uC138\uC694!");
		lblPassword_1.setBounds(12, 26, 461, 30);
		contentPane.add(lblPassword_1);
		lblPassword_1.setBackground(Color.WHITE);
		lblPassword_1.setForeground(SystemColor.infoText);
		lblPassword_1.setFont(new Font("굴림", Font.BOLD, 25));
		
		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g) {
                // Approach 1: Dispaly image at at full size
            	g.drawImage(back.getImage(), 0, 0, null);
                setOpaque(false); //그림을 표시하게 설정,투명하게 조절
                super.paintComponent(g);
            }
		};
		panel.setBounds(0, 0, 473, 389);
		contentPane.add(panel);
		
		
		list.addListSelectionListener(new JListSelect());  //ListSelectListener
		
		RoomCreate action=new RoomCreate();
		btnRoomCreate.addActionListener(action);
	}


	//서버관리
	class RoomCreate implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String roomname=txtRoomName.getText().trim();
			if(roomname.isEmpty() ) {
				MSG.setText("!방이름을 쓰세요!");
			}
			else {
				v.addElement(roomname);
				list.setListData(v);
				txtRoomName.setText("");
				MSG.setText(roomname+"방이 생성되었습니다~");
			}
				
		}
	}
		
	public class JListSelect implements ListSelectionListener {  //list 선택시, txtRoomName에 출력
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			txtRoomName.setText(list.getSelectedValue());
			MSG.setText(list.getSelectedValue()+"방을 선택하셨습니다.");
			
		}
	}
}
