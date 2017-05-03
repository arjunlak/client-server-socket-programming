import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Calendar;
import java.io.FileInputStream;
import java.io.IOException;

public final class mserver {

	public static ServerSocket serverSocket;
	public static int port = 2000;

	public static void main(String argv[]) throws Exception {
		try {
			serverSocket = new ServerSocket(port);  // socket is initiated
			System.out.println("Port Number:  " + serverSocket.getLocalPort());
		    } 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		while (true) 
		{
			
			Socket requestSocket = serverSocket.accept();
			HttpRequest request = new HttpRequest(requestSocket);
			Thread thread = new Thread(request);
			thread.start();
		}
	}
}

final class HttpRequest implements Runnable {

	public static class HTTP_METHOD 
	{
		public static String GET = "GET";
		public static String POST = "POST";
		public static String HEAD = "HEAD";
		
	}

	public static String HTTPVERSION = "HTTP/1.0 200 OK";
	public static String CRLF = "\r\n";
	public static String SERVERNAME = "Server: localServer";
	public static int buffsize = 1024;
	public static Socket socket;
	public static String uri;
	public static String root = System.getProperty("user.dir") + File.separator + "localfile\\";

	
	@SuppressWarnings("static-access")
	
	public HttpRequest(Socket socket) throws Exception 
	{
		this.socket = socket;
	}

	public void run() {
		
		try {
			Request();
			Response();
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	public static void Request() throws Exception {
		try {
			System.out.println("              Client request");
			System.out.println(Calendar.getInstance().getTime() + "Running Request");
			InputStream inputStream = socket.getInputStream();
			StringBuffer requestBuffer = new StringBuffer(buffsize);
			int i;
			byte[] buffer = new byte[buffsize];
			try {
				i = inputStream.read(buffer);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				i = -1;
			}
			for (int j = 0; j < i; j++) 
			{
				requestBuffer.append((char) buffer[j]);
			}
			System.out.print(requestBuffer.toString());
			uri = parseUri(requestBuffer.toString());
		} 
		catch (SocketException sockExp) 
		{
			sockExp.printStackTrace();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static String parseUri(String requestString) 
	{
		int i1, i2;
		i1 = requestString.indexOf(' ');
		if (i1 != -1) {
			i2 = requestString.indexOf(' ', i1 + 1);
			if (i2 > i1)
				return requestString.substring(i1 + 1, i2);
		}
		return null;
	}

	public static String getUri() 
	{
		return uri;
	}

	public static void Response() throws Exception 
	{
		System.out.println("                          Response message");
		System.out.println(Calendar.getInstance().getTime() + "Response sent........");
		OutputStream outputStream = socket.getOutputStream();
		String headerString = "HTTP/1.1 200 OK \r\n" + "Content-Type: text/html\r\n" + "Content-Length: 23\r\n"
				+ "\r\n";
		byte[] bytes = new byte[buffsize];
		FileInputStream fis = null;
		try {
			System.out.println("file name " + root);
			File file = new File(root, getUri());
			System.out.println("file name path " + file.getAbsolutePath());
			if (file.exists())
			{
				fis = new FileInputStream(file);
				int ch = fis.read(bytes, 0, buffsize);
				while (ch != -1) 
				{
					outputStream.write(bytes, 0, ch);
					ch = fis.read(bytes, 0, buffsize);
				}
			} 
			else 
			{
				
				String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type: text/html\r\n"
						+ "Content-Length: 23\r\n" + "\r\n" + "<h1>File Not Found</h1>";
				outputStream.write(errorMessage.getBytes());
			}
			System.out.println(Calendar.getInstance().getTime() + "sending response");
		} 
		catch (NullPointerException nullExp) 
		{
			nullExp.printStackTrace();
		} catch (Exception e) 
		{
			
			e.printStackTrace();
		} 
		finally {
			if (fis != null)
				fis.close();
			outputStream.flush();
			outputStream.close();
			
		}
	}
}