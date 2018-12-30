import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HTTPHandler {

	public static void handle(Socket s) throws Exception {
		HTTPResponse httpr = new HTTPResponse(new HTTPRequest(s.getInputStream()));
		new PrintStream(s.getOutputStream()).println(httpr);
		s.close();
	}
	
	public static String test() {
		return
				"HTTP/1.1 200 OK\r\n"
				+ "\r\n"
				+ "<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "<head></head>\n"
				+ "<body>\n"
				+ "<p>testing testing 123</p>\n"
				+ "</html>";
	}
}
