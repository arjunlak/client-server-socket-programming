import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


public class client {
public static void main (String argv[]) throws Exception {
	try
	{
		String serverportno = argv[0].trim();
		int port = Integer.parseInt(argv[1].trim());
		String f= argv[2].trim();
		Socket socket = new Socket(serverportno,port);
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		pw.println("GET /"+f+" HTTP/1.1");
		pw.flush();
		long i = System.currentTimeMillis();  // initiating time for RTT calculation using system time
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String t;
		while((t = br.readLine()) != null)
			System.out.println(t);
		
		long j = System.currentTimeMillis();  // finishing time for RTT calculation using system time
		long x = j - i;                       // Calculating the difference in time
		System.out.print("RTT for the client server interaction in Milli Seconds: "  +x);
		System.out.println("Client ip address (client parameter):  "+InetAddress.getLocalHost().getHostAddress());
		br.close();
		socket.close();                      //closure of socket after usage of socket 
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}
}
