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
		super("도서 조회 서버");
		setSize(250, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //프레임 종료 버튼(X)을 클릭하면 프로그램 종료
		Container c = getContentPane();
		c.add(new JLabel("도서 조회 서버입니다"));
		c.add(new JScrollPane(log), BorderLayout.CENTER);
		setVisible(true);
		
		bookManager = new BookManager("d:\\bookFile.txt");
		if(bookManager.isFileRead()) { // 단어 파일이 읽혀졌을 경우 서비스 시작
			log.setText("bookFile.txt 읽기 완료\n");
			new ServerThread().start(); // 서비스 시작
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
					log.append("클라이언트 연결됨\n");
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
		
		public BookList get(String name) { // map에서 name의 score를 검색하여 리턴
			return map.get(name);
		}
	}
	
	class ServiceThread extends Thread {
		private Socket socket = null;
		private BufferedReader in = null;
		private BufferedWriter out = null;
		
		public ServiceThread(Socket socket) { // 클라이언트와 통신할 소켓을 전달받음
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
					String name = in.readLine(); // 클라이언트로부터 이름 받음
					BookList book = bookManager.get(name);
					String str="";
					if(book == null && !name.equals("showList")) {
						out.write("없는 책\n");
						log.append(name + " 없음\n");
					}
					else if(name.equals("showList")) {
						for(String key : map.keySet()) {
							book = bookManager.get(key);
							str += book+"\n";	
						}	

						out.write(str + "\n"+"end\n");
						log.append("전체보기\n"+str + "\n");
					}
					else { 
						out.write(book + "\n");
						log.append(name + "\n" + book + "\n");				
					}
					out.flush();
				} catch (IOException e) {
					log.append("연결 종료\n");
					System.out.println("연결 종료");
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return; // 스레드 종료
					//e.printStackTrace();
				}

			}
		}
	}
	public static void main(String[] args) {
		new BookServerFrame();
	}
}
