package book;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class BookClientFrame extends JFrame{
	private JTextField nameTf = new JTextField(14);
	private JButton searchBtn = new JButton("����˻�");
	private JButton showBtn = new JButton("��ü����");
	private JTextArea showText = new JTextArea(20, 35);
	
	private Socket socket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	
	public BookClientFrame() {
		super("����üũ Ŭ���̾�Ʈ");
		setSize(300, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //������ ���� ��ư(X)�� Ŭ���ϸ� ���α׷� ����
		//â
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		//â�� ���� �׸�
		c.add(new JLabel("å���� "));
		c.add(nameTf);
		c.add(searchBtn);
		c.add(showBtn);
	//	showText.setText("id  å����    ����  ����\n========================");
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
				showText.append("id  å����    ����  ����\n========================");
				String score = in.readLine();
				showText.append("\n"+score+"\n\n");
			} catch (IOException e1) {
				System.out.println("Ŭ���̾�Ʈ : �����κ��� ���� ����");
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
				showText.append("     ==[��ü����]==\nid  å����    ����  ����\n========================");
				str = in.readLine();
				while(!str.equals("end")) {
					showText.append("\n"+str);			
					str = in.readLine();
				}
			} catch (IOException e1) {
				System.out.println("Ŭ���̾�Ʈ : �����κ��� ���� ����");
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
