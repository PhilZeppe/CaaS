package at.ac.tuwien.dsg.pm;

import at.ac.tuwien.dsg.pm.dao.MongoDBCollectiveDAO;
import at.ac.tuwien.dsg.pm.dao.MongoDBPeerDAO;
import at.ac.tuwien.dsg.pm.model.Collective;
import at.ac.tuwien.dsg.pm.model.Peer;
import at.ac.tuwien.dsg.pm.model.PeerAddress;
import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;
import at.ac.tuwien.dsg.smartcom.utils.MongoDBInstance;
import at.ac.tuwien.dsg.pm.util.RequestMappingFeature;
import com.mongodb.MongoClient;
import org.glassfish.jersey.client.ClientProperties;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class PeerManagerPeerInfoResourceTest {

    public static final String URL = "http://localhost:8080/SmartCom/peerInfo";
    private MongoDBInstance mongoDB;

    private MongoDBPeerDAO peerDAO;

    private Client client;
    private PeerManager manager;

    @Before
    public void setUp() throws Exception {
        mongoDB = new MongoDBInstance();
        mongoDB.setUp();

        MongoClient mongo = new MongoClient("localhost", 12345);
        peerDAO = new MongoDBPeerDAO(mongo, "TEST", "PEER");
        MongoDBCollectiveDAO collectiveDAO = new MongoDBCollectiveDAO(mongo, "TEST", "COLLECTIVE");

        this.client = ClientBuilder.newBuilder()
                .register(RequestMappingFeature.class)
                .property(ClientProperties.CONNECT_TIMEOUT, 5000)
                .property(ClientProperties.READ_TIMEOUT, 5000)
                .build();
//        client.register(new LoggingFilter(java.util.logging.Logger.getLogger("Jersey"), true)); //enables this to have additional logging information

        manager = new PeerManager(8080, "SmartCom", peerDAO, collectiveDAO);
        manager.init();
    }

    @After
    public void tearDown() throws Exception {
        mongoDB.tearDown();
        client.close();
        manager.cleanUp();
    }

    @Test
    public void testPeerInfo() throws Exception {
        Peer peer1 = createAndAddPeer("1", "Peer1");
        Peer peer2 = createAndAddPeer("2", "Peer2");
        Peer peer3 = createAndAddPeer("3", "Peer3");
        Peer peer4 = createAndAddPeer("4", "Peer4");
        Peer peer5 = createAndAddPeer("5", "Peer5");

        List<Peer> peers = Arrays.asList(peer1, peer2, peer3, peer4, peer5);


        for (Peer peer : peers) {
            WebTarget target = client.target(URL+"/"+peer.getId());
            PeerInfo peerInfo = target.request(MediaType.APPLICATION_JSON).get(PeerInfo.class);

            assertEquals(peer.getId(), peerInfo.getId().getId());
            assertEquals(peer.getDeliveryPolicy(), peerInfo.getDeliveryPolicy());
            assertEquals(peer.getPeerAddressList().size(), peerInfo.getAddresses().size());
        }

        WebTarget target = client.target(URL+"/6");
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    private Peer createAndAddPeer(String id, String name) {
        Peer peer = new Peer();
        peer.setId(id);
        peer.setName(name);
        peer.setDeliveryPolicy(DeliveryPolicy.Peer.AT_LEAST_ONE);

        PeerAddress address1 = new PeerAddress();
        address1.setType("email");
        address1.setValues(Arrays.asList("peer@peer.de"));

        PeerAddress address2 = new PeerAddress();
        address2.setType("skype");
        address2.setValues(Arrays.asList("peerSkype"));

        peer.setPeerAddressList(Arrays.asList(address1, address2));
        peerDAO.addPeer(peer);
        return peer;
    }
}