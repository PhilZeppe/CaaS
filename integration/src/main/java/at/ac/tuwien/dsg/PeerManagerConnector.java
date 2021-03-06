package at.ac.tuwien.dsg;

import at.ac.tuwien.dsg.smartcom.callback.CollectiveInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerAuthenticationCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchCollectiveException;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchPeerException;
import at.ac.tuwien.dsg.smartcom.callback.exception.PeerAuthenticationException;
import at.ac.tuwien.dsg.smartcom.model.CollectiveInfo;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class PeerManagerConnector implements PeerInfoCallback, CollectiveInfoCallback, PeerAuthenticationCallback {

    private final Client client;
    private final String collectiveURL;
    private final String peerURL;
    private final String authenticationURL;

    public PeerManagerConnector(String urlPrefix) {
        this.client = ClientBuilder.newBuilder()
                .register(RequestMappingFeature.class)
                .property(ClientProperties.CONNECT_TIMEOUT, 1000)
                .property(ClientProperties.READ_TIMEOUT,    1000)
                .build();

        collectiveURL       = urlPrefix + "/collectiveInfo";
        peerURL             = urlPrefix + "/peerInfo";
        authenticationURL   = urlPrefix + "/peerAuth";
    }

    @Override
    public CollectiveInfo getCollectiveInfo(Identifier collective) throws NoSuchCollectiveException {
        WebTarget target = client.target(collectiveURL + "/" + collective.getId());
        Response response = target.request(MediaType.APPLICATION_JSON).get();

        if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
            throw new NoSuchCollectiveException();
        }

        return response.readEntity(CollectiveInfo.class);
    }

    @Override
    public boolean authenticate(Identifier peerId, String password) throws PeerAuthenticationException {
        WebTarget target = client.target(authenticationURL + "/" + peerId.getId());

        return target.request(MediaType.APPLICATION_JSON).header("password", password).get(Boolean.class);
    }

    @Override
    public PeerInfo getPeerInfo(Identifier id) throws NoSuchPeerException {
        WebTarget target = client.target(peerURL + "/" + id.getId());
        Response response = target.request(MediaType.APPLICATION_JSON).get();

        if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
            throw new NoSuchPeerException(id);
        }

        return response.readEntity(PeerInfo.class);
    }

}
