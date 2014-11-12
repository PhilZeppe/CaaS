package at.ac.tuwien.dsg.pm;

import at.ac.tuwien.dsg.pm.dao.MongoDBCollectiveDAO;
import at.ac.tuwien.dsg.pm.dao.MongoDBPeerDAO;
import com.mongodb.MongoClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class PeerManagerLauncher {

    public static void main(String[] args) throws IOException {
        int port;
        String uriPrefix = "PeerManager";
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

        PeerManager manager = startPeerManager(port, uriPrefix, mongoDBHost, mongoDBPort);

        System.out.println("Press enter to shutdown the peer manager");
        System.in.read();

        manager.cleanUp();
    }

    public static PeerManager startPeerManager(int port, String uriPrefix, MongoClient mongo) throws UnknownHostException {
        System.out.println("Running the peer manager on port ["+port+"] and path '"+uriPrefix+"'");

        MongoDBPeerDAO peerDAO = new MongoDBPeerDAO(mongo, "PM", "PEER");
        MongoDBCollectiveDAO collectiveDAO = new MongoDBCollectiveDAO(mongo, "PM", "COLLECTIVE");

        PeerManager manager = new PeerManager(port, uriPrefix, peerDAO, collectiveDAO);
        manager.init();
        return manager;
    }

    public static PeerManager startPeerManager(int port, String uriPrefix, String mongoDBHost, int mongoDBPort) throws UnknownHostException {
        return startPeerManager(port, uriPrefix, new MongoClient(mongoDBHost, mongoDBPort));
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
