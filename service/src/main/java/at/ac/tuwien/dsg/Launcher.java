package at.ac.tuwien.dsg;

import at.ac.tuwien.dsg.pm.PeerManager;
import at.ac.tuwien.dsg.pm.PeerManagerLauncher;
import at.ac.tuwien.dsg.rest.adapter.AdapterRestService;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.SmartComBuilder;
import at.ac.tuwien.dsg.smartcom.callback.exception.PeerAuthenticationException;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;

import java.io.IOException;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class Launcher {

    public static void main(String[] args) throws IOException, PeerAuthenticationException, CommunicationException {

        MongoDBLauncher.MongoDBInstance mongodb = MongoDBLauncher.startMongoDB(12345, "storage/mongoDB");

        PeerManager peerManager = PeerManagerLauncher.startPeerManager(8080, "PeerManager", mongodb.getClient());

        PeerManagerConnector peerManagerConnector = new PeerManagerConnector("http://localhost:8080/PeerManager");

        SmartCom smartCom = new SmartComBuilder(peerManagerConnector, peerManagerConnector, peerManagerConnector)
                .initAdapters(true)
                .initializeActiveMQ(true)
                .setMongoClient(mongodb.getClient())
                .setRestApiPort(8081)
                .useLocalMessageQueue(true)
                .create();
        System.out.println("Running the the SmartCom rest service on port ["+8081+"] and path 'SmartCom'");

        AdapterRestService adapterRestService = new AdapterRestService(8082, "SmartCom/adapter", smartCom.getCommunication());
        adapterRestService.init();
        System.out.println("Running the the adapter rest service on port ["+8082+"] and path 'SmartCom/adapter'");

        System.out.println("Press enter to shutdown the application");
        System.in.read();

        adapterRestService.cleanUp();
        smartCom.tearDownSmartCom();
        peerManager.cleanUp();
        mongodb.tearDown();
    }
}
