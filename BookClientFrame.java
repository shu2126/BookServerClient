package book;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class BookClientFrame extends JFrame{
	private JTextField nameTf = new JTextField(14);
	private JButton searchBtn = new JButton("제목검색");
	private JButton showBtn = new JButton("전체보기");
	private JTextArea showText = new JTextArea(20, 35);
	
	private Socket socket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	
	public BookClientFrame() {
		super("스펠체크 클라이언트");
		setSize(300, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //프레임 종료 버튼(X)을 클릭하면 프로그램 종료
		//창
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		//창에 들어가는 항목
		c.add(new JLabel("책제목 "));
		c.add(nameTf);
		c.add(searchBtn);
		c.add(showBtn);
	//	showText.setText("id  책제목    저자  가격\n========================");
		c.add(new JScrollPane(showText));
		setVisible(true);
		setupConnection();

		searchBtn.addActionListener(new MyActionListener());
		showBtn.addActionListener(new MyActionListener2());
		
	}
	
	class MyActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				out.write(nameTf.getText()+"\n");
				out.flush();
				showText.append("id  책제목    저자  가격\n========================");
				String score = in.readLine();
				showText.append("\n"+score+"\n\n");
			} catch (IOException e1) {
				System.out.println("클라이언트 : 서버로부터 연결 종료");
				return;
				// e.printStackTrace();
			}
			
		}
		
	}
	
	class MyActionListener2 implements ActionListener {		
		String str="";
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				out.write("showList"+"\n");
				out.flush();
				showText.append("     ==[전체보기]==\nid  책제목    저자  가격\n========================");
				str = in.readLine();
				while(!str.equals("end")) {
					showText.append("\n"+str);			
					str = in.readLine();
				}
			} catch (IOException e1) {
				System.out.println("클라이언트 : 서버로부터 연결 종료");
				return;
				// e.printStackTrace();
			}
			
		}
		
	}
	
	public void setupConnection() {
		try {
			socket = new Socket("localhost", 9998);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new BookClientFrame();
	}
}
