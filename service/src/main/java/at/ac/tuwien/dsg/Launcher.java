package at.ac.tuwien.dsg;

import at.ac.tuwien.dsg.pm.PeerManager;
import at.ac.tuwien.dsg.pm.PeerManagerLauncher;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchCollectiveException;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchPeerException;
import at.ac.tuwien.dsg.smartcom.callback.exception.PeerAuthenticationException;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.CollectiveInfo;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class Launcher {

    public static void main(String[] args) throws IOException, NoSuchCollectiveException, NoSuchPeerException, PeerAuthenticationException, CommunicationException {

        MongoDBLauncher.MongoDBInstance mongodb = MongoDBLauncher.startMongoDB(12345, "storage/mongoDB");
        PeerManager peerManager = PeerManagerLauncher.startPeerManager(8080, "PeerManager", "localhost", 12345);

        PeerManagerConnector peerManagerConnector = new PeerManagerConnector("http://localhost:8080/PeerManager");
        SmartCom smartCom = new SmartCom(peerManagerConnector, peerManagerConnector, peerManagerConnector);
        smartCom.setRestApiPort(8080);
        smartCom.initializeSmartCom();

        System.out.println("Press enter to shutdown the application");
        new BufferedReader(new InputStreamReader(System.in)).readLine();

        smartCom.tearDownSmartCom();
        peerManager.cleanUp();
        mongodb.tearDown();
    }
}
