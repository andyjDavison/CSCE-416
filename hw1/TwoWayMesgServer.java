import java.io.*;
import java.net.*;

public class TwoWayMesgServer {

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("usage: java TwoWayMesgServer <port> <server name>");
            System.exit(1);
        }

        int serverPort = Integer.parseInt(args[0]);
        String serverName = args[1];

        try{
            ServerSocket serverSock = new ServerSocket(serverPort);
            System.out.println("Waiting for a client...");

            Socket clientSock = serverSock.accept();
            System.out.println("Connected to a client at ('" +
												((InetSocketAddress) clientSock.getRemoteSocketAddress()).getAddress().getHostAddress()
												+ "', '" +
												((InetSocketAddress) clientSock.getRemoteSocketAddress()).getPort()
												+ "')"
												);
            serverSock.close();

            // to send data to client
            PrintStream serverWriter = new PrintStream(clientSock.getOutputStream());

            // to read data from client
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));

            // to read data from keyboard
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            while(true) {

                String messageIn = serverReader.readLine();

                if (messageIn == null) {
					System.out.println("Client closed connection");
					clientSock.close();
                    serverWriter.close();
                    serverReader.close();
                    keyboard.close();
					break;
				}

                System.out.print(messageIn);

                String messageOut = keyboard.readLine();

                serverWriter.println(serverName + ": " + messageOut + "\n");
                
                keyboard.readLine();

            }
        } catch(Exception e) {
            System.out.println(e);
        }
    }

}