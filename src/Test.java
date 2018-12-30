import java.net.ServerSocket;

public class Test {
	
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(54600);
			int i = 0;
			while(true) {
				ss.accept();
				System.out.println("accepted " + ++i + " times");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
