import java.io.*;
import java.net.*;


public class TwoWayMesgClient {
    
    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("usage: java TwoWayMesgClient <server name> <server port> <client name>");
            System.exit(1);
        }

        String serverName = args[0]; // localhost
        int serverPort = Integer.parseInt(args[1]);
        String clientName = args[2];

        try{

            Socket sock = new Socket(serverName, serverPort);

			System.out.println(
					"Connected to server at ('" + serverName + "', '" + serverPort + "')");
            
			PrintWriter toServerWriter = new PrintWriter(sock.getOutputStream(), true);

            BufferedReader serverReader = new BufferedReader(new BufferedReader(new InputStreamReader(sock.getInputStream())));

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            while(true) {
                String messageOut = keyboard.readLine();

                if (messageOut == null) {
					System.out.println("Closing connection");
					break;
				}
                toServerWriter.println(clientName + ": " + messageOut + "\n");

                String messageIn = serverReader.readLine();

                System.out.println(messageIn);

                keyboard.readLine();
            }

            toServerWriter.close();
            serverReader.close();
            keyboard.close();
            sock.close();

        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
