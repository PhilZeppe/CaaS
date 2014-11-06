package at.ac.tuwien.dsg.pm.resources;

import at.ac.tuwien.dsg.pm.PeerManager;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
@Path("peerAuth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerAuthenticationResource {

    @Inject
    private PeerManager manager;

    @GET
    @Path("/{id}")
    public boolean authenticatePeer(@PathParam("id") String id, @HeaderParam("password") String password) {
        return id.equals(password);
    }

}
