import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class Listener implements Runnable {

	private ServerSocket ss;
	
	public Listener(int port) throws Exception {
		ss = new ServerSocket(port);
	}
	
	public static void main(String[] args) {
		System.out.println(Paths.get("").toAbsolutePath());
		try {
			new Listener(80).listen().listen().listen();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Thread start() {
		Thread t = new Thread(this);
		t.start();
		return t;
	}
	
	public Listener listen() {
		this.start();
		return this;
	}
	
	public void run() {
		while(true) {
			try {
				Socket s = ss.accept();
				new Thread(() -> { try { HTTPHandler.handle(s); }catch(Exception e2) { e2.printStackTrace(); } }).start();
			}catch(Exception e) {
				e.printStackTrace();
				try {
					ss.close();
				}catch(Exception e2) { }
				try {
					ss = new ServerSocket(ss.getLocalPort());
				}catch(Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}
