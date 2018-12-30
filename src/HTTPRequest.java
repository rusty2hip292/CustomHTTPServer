import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//GET / HTTP/1.1
//Host: localhost:54600
//Connection: keep-alive
//Cache-Control: max-age=0
//Upgrade-Insecure-Requests: 1
//User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
//Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
//Accept-Encoding: gzip, deflate, br
//Accept-Language: en-US,en;q=0.9

public class HTTPRequest {

	public static enum HTTPRequestType {
		GET,
		PUT,
		POST,
		DELETE,
		HEAD,
		OPTIONS,
		NOT_SUPPORTED,
		BAD,
		BAD__LENGTH_REQUIRED
	}
	
	public HTTPRequestType type;
	private HashMap<String, String[]> headers = new HashMap<String, String[]>();
	private String resource = "", body = "", version = "";
	private String head = "";
	
	public String head() {
		return head;
	}
	
	public String url() {
		return resource;
	}
	
	public HTTPRequest(ArrayList<String> lines, Scanner s) {
		if(lines == null || lines.size() < 1) {
			throw new IllegalArgumentException();
		}
		handleHead(lines.get(0));
		//System.out.println("head");
		for(int i = 1; i < lines.size(); i++) {
			//System.out.println("here");
			handleHeader(lines.get(i));
		}
		body = grabBody(s);
	}
	public HTTPRequest(Scanner s) {
		handleHead(s.nextLine());
		while(true) {
			String line = s.nextLine();
			if(line.equals("")) {
				break;
			}else {
				handleHeader(line);
			}
		}
		body = grabBody(s);
	}
	public HTTPRequest(InputStream stream) {
		this(streamToScanner(stream));
	}
	private static Scanner streamToScanner(InputStream is) {
		StringBuffer sb = new StringBuffer();
		int failed = 0;
		System.out.println("converting");
		while(true) {
			try {
				if(is.available() == 0) {
					throw new Exception();
				}
				sb.append((char) is.read());
				if(failed != 0) {
					System.out.println(sb.toString());
				}
				failed = 0;
			}catch(Exception e) {
				failed++;
			}
			//System.out.println(failed);
			if(failed >= 100) {
				break;
			}
		}
		System.err.println(sb.toString());
		return new Scanner(sb.toString());
	}
	
	private void handleHead(String head) {
		this.head = head;
		System.out.println(head);
		Scanner s = new Scanner(head);
		try {
			type = HTTPRequestType.valueOf(s.next());
		}catch(Exception e) {
			type = HTTPRequestType.NOT_SUPPORTED;
		}finally {
			resource = s.next();
			version = s.next();
		}
	}
	private void handleHeader(String headerline) {
		//System.out.println(headerline);
		Scanner s = new Scanner(headerline);
		String attribute = s.next();
		attribute = attribute.substring(0, attribute.length() - 1); // cut off ':'
		String value = s.nextLine();
		String[] vals = value.split(",");
		for(int i = 0; i < vals.length; i++) {
			vals[i] = strip(vals[i]).toLowerCase();
		}
		headers.put(attribute.toLowerCase(), vals);
	}
	private String grabBody(Scanner s) {
		if(headers.get("content-length") == null) {
			if(headers.get("content-type") != null) {
				this.type = HTTPRequest.HTTPRequestType.BAD__LENGTH_REQUIRED;
			}
			return "";
		}else {
			try {
				int i = Integer.parseInt(headers.get("content-length")[0]);
				String bod = "";
				System.err.println("entering while");
				while(bod.length() < i) {
					bod += s.nextLine() + "\n";
				}
				System.err.println("end while");
				return bod.substring(0, bod.length() - 1);
			}catch(Exception e) {
				e.printStackTrace();
				this.type = HTTPRequest.HTTPRequestType.BAD;
				return "";
			}
		}
	}
	
	private String strip(String s) {
		if(s.indexOf(" ") == 0) {
			return strip(s.substring(1));
		}else if(s.charAt(s.length() - 1) == ' ') {
			return strip(s.substring(0, s.length() - 1));
		}else {
			return s;
		}
	}
}
