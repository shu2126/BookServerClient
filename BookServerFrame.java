package book;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import javax.swing.*;

public class BookServerFrame extends JFrame {
	private BookManager bookManager = null;
	private ArrayList<BookList> list = new ArrayList<BookList>();
	private JTextArea log = new JTextArea();
	private HashMap<String, BookList> map =  new HashMap<String, BookList>();
	
	public BookServerFrame() {
		super("���� ��ȸ ����");
		setSize(250, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //������ ���� ��ư(X)�� Ŭ���ϸ� ���α׷� ����
		Container c = getContentPane();
		c.add(new JLabel("���� ��ȸ �����Դϴ�"));
		c.add(new JScrollPane(log), BorderLayout.CENTER);
		setVisible(true);
		
		bookManager = new BookManager("d:\\bookFile.txt");
		if(bookManager.isFileRead()) { // �ܾ� ������ �������� ��� ���� ����
			log.setText("bookFile.txt �б� �Ϸ�\n");
			new ServerThread().start(); // ���� ����
		}
	}
	
	class ServerThread extends Thread {
		@Override
		public void run() {
			ServerSocket listener = null;
			Socket socket = null;
			try {
				listener = new ServerSocket(9998);
				while(true) {
					socket = listener.accept();
					log.append("Ŭ���̾�Ʈ �����\n");
					new ServiceThread(socket).start();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if(listener != null)
					listener.close();
				if(socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class BookManager {
		BookList b = null;
		private boolean fileOn = false;	
		public BookManager(String fileName) {
			try {
				Scanner reader = new Scanner(new FileReader(fileName));
				while(reader.hasNext()) {
					 b = new BookList();
					b.setId(reader.next());
					b.setBookName(reader.next());
					b.setWriter(reader.next());
					b.setPrice(reader.next());
					list.add(b);
					map.put(b.getBookName(),b);
				}
				reader.close();
				fileOn = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fileOn = false;
			}
		}
		
		public boolean isFileRead() {
			return fileOn;
		}
		
		public BookList get(String name) { // map���� name�� score�� �˻��Ͽ� ����
			return map.get(name);
		}
	}
	
	class ServiceThread extends Thread {
		private Socket socket = null;
		private BufferedReader in = null;
		private BufferedWriter out = null;
		
		public ServiceThread(Socket socket) { // Ŭ���̾�Ʈ�� ����� ������ ���޹���
			this.socket = socket;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					String name = in.readLine(); // Ŭ���̾�Ʈ�κ��� �̸� ����
					BookList book = bookManager.get(name);
					String str="";
					if(book == null && !name.equals("showList")) {
						out.write("���� å\n");
						log.append(name + " ����\n");
					}
					else if(name.equals("showList")) {
						for(String key : map.keySet()) {
							book = bookManager.get(key);
							str += book+"\n";	
						}	

						out.write(str + "\n"+"end\n");
						log.append("��ü����\n"+str + "\n");
					}
					else { 
						out.write(book + "\n");
						log.append(name + "\n" + book + "\n");				
					}
					out.flush();
				} catch (IOException e) {
					log.append("���� ����\n");
					System.out.println("���� ����");
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return; // ������ ����
					//e.printStackTrace();
				}

			}
		}
	}
	public static void main(String[] args) {
		new BookServerFrame();
	}
}
