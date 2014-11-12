package at.ac.tuwien.dsg.peer;

import at.ac.tuwien.dsg.peer.dao.MongoDBPeerMailboxDAO;
import com.mongodb.MongoClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class PeerMailboxServiceLauncher {

    public static void main(String[] args) throws IOException {
        int port;
        String uriPrefix = "mailbox";
        String mongoDBHost = "localhost";
        int mongoDBPort = 12345;

        if (args.length == 0) {
            port = getFreePort();
        } else if (args.length == 1) {
            port = Integer.valueOf(args[1]);
        } else  {
            uriPrefix = args[0];
            port = Integer.valueOf(args[1]);

            if (args.length == 4) {
                mongoDBHost = args[2];
                mongoDBPort = Integer.valueOf(args[3]);
            }
        }

        PeerMailboxService service = startPeerMailboxService(port, uriPrefix, mongoDBHost, mongoDBPort);

        System.out.println("Press enter to shutdown the peer mailbox service");
        System.in.read();

        service.cleanUp();
    }

    public static PeerMailboxService startPeerMailboxService(int port, String uriPrefix, String mongoDBHost, int mongoDBPort) throws UnknownHostException {
        return startPeerManager(port, uriPrefix, new MongoClient(mongoDBHost, mongoDBPort));
    }

    public static PeerMailboxService startPeerManager(int port, String uriPrefix, MongoClient mongo) {
        System.out.println("Running the peer mailbox service on port ["+port+"] and path '"+uriPrefix+"'");

        MongoDBPeerMailboxDAO mailboxDao = new MongoDBPeerMailboxDAO(mongo, "MAILBOX", "PEER");

        PeerMailboxService mailbox = new PeerMailboxService(port, uriPrefix, mailboxDao);
        mailbox.init();
        return mailbox;
    }

    public static int getFreePort() {
        try {
            try (ServerSocket socket = new ServerSocket(0)) {
                socket.setReuseAddress(true);
                return socket.getLocalPort();
            }
        } catch (IOException e) {
            return -1;
        }
    }
}

