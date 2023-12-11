import java.io.*;
import java.net.*;
import java.util.*;

public class GroupChatServer implements Runnable {
    private Socket clientSock;
    private static List<PrintWriter> clientList = new ArrayList<>();

    public GroupChatServer(Socket sock) {
        clientSock = sock;
    }

    public static synchronized boolean addClient(PrintWriter toClientWriter) {
        return clientList.add(toClientWriter);
    }

    public static synchronized boolean removeClient(PrintWriter toClientWriter) {
        return clientList.remove(toClientWriter);
    }

    public static synchronized void relayMessage(PrintWriter fromClientWriter, String mesg) {
        for (PrintWriter client : clientList) {
            if (client != fromClientWriter) {
                client.println(mesg);
            }
        }
    }

    public void run() {
        try {
            // Prepare to read from the client
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));

            // Prepare to write to the client with auto flush on
            PrintWriter toClient = new PrintWriter(clientSock.getOutputStream(), true);

            // Add this client to the active client list
            addClient(toClient);

            while (true) {
				// Read a line from the client
				String line = fromClient.readLine();

                // If we get null, it means the client quit, break out of the loop
                if (line == null) {
					System.out.println("*** Server closing a connection");
                    break;
                }
                // Else, relay the message to all active clients
                relayMessage(toClient, line);
            }

            // Done with the client, remove it from the client list
            removeClient(toClient);
			//relayMessage(toClient, "left chat");
            clientSock.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("usage: java GroupChatServer <server port>");
            System.exit(1);
        }

		// Get the port on which the server should listen
		int serverPort = Integer.parseInt(args[0]);

		Socket clientSock = null;
        try {
            // Create a server socket with the given port
            ServerSocket serverSocket = new ServerSocket(serverPort);

			// Keep accepting/serving new clients
            while (true) {
                // Wait to accept another client
				System.out.println("Waiting for a client ...");
                clientSock = serverSocket.accept();
				System.out.println("Connected to a client at ('" +
									((InetSocketAddress) clientSock.getRemoteSocketAddress()).getAddress().getHostAddress()
									+ "', '" +
									((InetSocketAddress) clientSock.getRemoteSocketAddress()).getPort()
									+ "')"
									);

                // Spawn a thread to read/relay messages from this client
                Thread clientThread = new Thread(new GroupChatServer(clientSock));
                clientThread.start();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
