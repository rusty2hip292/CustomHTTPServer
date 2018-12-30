import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Scanner;

public class HTTPResponse {

	public static enum HTTPCode {
		
		// 100 status codes
		CONTINUE,
		
		// 200 status codes
		OK,
		CREATED,
		ACCEPTED,
		NO_CONTENT,
		REFRESH_PAGE,
		
		// 300 status codes
		NOT_MODIFIED,
		
		// 400 status codes
		BAD_REQUEST,
		NOT_FOUND,
		FORBIDDEN,
		LENGTH_REQUIRED,
		
		// 500 status codes
		INTERNAL_SERVER_ERROR,
		NOT_IMPLEMENTED
	}
	
	private String head(HTTPCode code) {
		switch(code) {
		case ACCEPTED:
			return "HTTP/1.1 202 Accepted\r\n";
		case BAD_REQUEST:
			return "HTTP/1.1 400 Bad Request\r\n";
		case CONTINUE:
			return "HTTP/1.1 100 Continue\r\n";
		case CREATED:
			return "HTTP/1.1 201 Created\r\n";
		case FORBIDDEN:
			return "HTTP/1.1 403 Forbidden\r\n";
		case NOT_FOUND:
			return "HTTP/1.1 404 Not Found\r\n";
		case NOT_MODIFIED:
			return "HTTP/1.1 304 Not Modified\r\n";
		case NO_CONTENT:
			return "HTTP/1.1 204 No Content\r\n";
		case OK:
			return "HTTP/1.1 200 OK\r\n";
		case REFRESH_PAGE:
			return "HTTP/1.1 205 Reset Content\r\n";
		case LENGTH_REQUIRED:
			return "HTTP/1.1 411 Length Required\r\n";
		case INTERNAL_SERVER_ERROR:
			return "HTTP/1.1 500 Internal Server Error\r\n";
		case NOT_IMPLEMENTED:
		default:
			return "HTTP/1.1 501 Not Implemented\r\n";
		}
	}
	private String header() {
		return "\r\n";
	}
	
	public String toString() {
		System.out.println(response);
		return response;
	}
	
	private final String response;
	private String body = "";
	private HTTPRequest req;
	
	public HTTPResponse(HTTPRequest req) {
		if(req == null) {
			throw new IllegalArgumentException();
		}
		this.req = req;
		response = handleRequest();
	}
	
	private String handleRequest() {
		switch(req.type) {
		case BAD:
			return head(HTTPCode.BAD_REQUEST) + header();
		case BAD__LENGTH_REQUIRED:
			return head(HTTPCode.LENGTH_REQUIRED) + header();
		case DELETE:
			return head(HTTPCode.NO_CONTENT) + header();
		case GET:
			HTTPCode c = get();
			if(c == HTTPCode.OK) {
				return head(c) + header() + body;
			}
			return head(c) + header();
		case HEAD:
			return head(get()) + header();
		case OPTIONS:
		case POST:
		case PUT:
		case NOT_SUPPORTED:
		default:
			return head(HTTPCode.NOT_IMPLEMENTED) + header();
		}
	}
	
	public HTTPCode get() {
		String path = req.url();
		path = path.substring(1); // strip preceeding /
		if(path.indexOf("?") >= 0) {
			path = path.substring(0, path.indexOf("?"));
		}
		File f = new File(path);
		if(!f.exists()) {
			System.out.println("path: " + path);
			return HTTPCode.NOT_FOUND;
		}
		try {
			Scanner s = new Scanner(f);
			while(s.hasNextLine()) {
				body += s.nextLine() + "\n";
			}
		}catch(FileNotFoundException e) {
			e.printStackTrace();
			return HTTPCode.INTERNAL_SERVER_ERROR;
		}
		return HTTPCode.OK;
	}
}
