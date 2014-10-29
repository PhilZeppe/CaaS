package at.ac.tuwien.dsg.pm.resources;

import at.ac.tuwien.dsg.pm.PeerManager;
import at.ac.tuwien.dsg.pm.model.Collective;
import at.ac.tuwien.dsg.pm.model.Peer;
import at.ac.tuwien.dsg.pm.model.PeerAddress;
import at.ac.tuwien.dsg.smartcom.model.CollectiveInfo;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
@Path("collectiveInfo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CollectiveInfoResource {

    @Inject
    private PeerManager manager;

    @GET
    @Path("/{id}")
    public CollectiveInfo getCollectiveInfo(@PathParam("id") String id) {
        Collective collective = manager.getCollective(id);

        if (collective == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());
        }

        CollectiveInfo info = new CollectiveInfo();
        info.setId(Identifier.collective(id));
        info.setDeliveryPolicy(collective.getDeliveryPolicy());

        List<Identifier> peers = new ArrayList<>(collective.getPeers().size());
        for (String s : collective.getPeers()) {
            peers.add(Identifier.peer(s));
        }
        info.setPeers(peers);

        return info;
    }
}
