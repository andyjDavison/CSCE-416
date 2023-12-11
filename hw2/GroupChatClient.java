import java.net.*;
import java.io.*;

public class GroupChatClient implements Runnable {
    
    private BufferedReader fromUserReader;

    private PrintWriter toSockWriter;

    private String name;

    public GroupChatClient(BufferedReader br, PrintWriter pr, String name) {
        fromUserReader = br;
        toSockWriter = pr;
        this.name = name;
    }

    public void run() {

        try {
            while(true) {
                String line = fromUserReader.readLine();

                if(line == null) {
                    System.out.println("*** Client closing connection");
                    toSockWriter.println(name + " has left the chat");
                    break;
                }

                toSockWriter.println(name + ": " + line);
            }
        } catch(Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("usage: java GroupChatClient <host> <port>");
			System.exit(1);
        }
        Socket sock = null;

        try {
            sock = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println(
					"Connected to server at " + args[0] + ":" + args[1]);
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
        }

        // Set up a thread to read from user and write to socket
		try {
			// Prepare to write to socket with auto flush on
			PrintWriter toSockWriter =
					new PrintWriter(sock.getOutputStream(), true);

			// Prepare to read from keyboard
			BufferedReader fromUserReader = new BufferedReader(
					new InputStreamReader(System.in));

            System.out.print("Enter your username: ");
            String name = fromUserReader.readLine();

            // Send username to server
            toSockWriter.println(name + " has joined the chat");

			// Spawn a thread to read from user and write to socket
			Thread child = new Thread(
					new GroupChatClient(fromUserReader, toSockWriter, name));
			child.start();
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}

        // Now read from socket and display to user
		try {
			// Prepare to read from socket
			BufferedReader fromSockReader = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));

			// Keep doing till server is done
			while (true) {
				// Read a line from the socket
				String line = fromSockReader.readLine();

				// If we get null, it means EOF
				if (line == null) {
					// Tell user server quit
					System.out.println("*** Server closed connection");
					break;
				}

				// Write the line to the user
				System.out.println(line);
			}
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		// End the other thread too
		System.exit(0);
	}

}
