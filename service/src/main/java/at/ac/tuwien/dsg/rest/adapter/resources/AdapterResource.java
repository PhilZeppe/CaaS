package at.ac.tuwien.dsg.rest.adapter.resources;

import at.ac.tuwien.dsg.rest.adapter.AdapterRestService;
import at.ac.tuwien.dsg.smartcom.model.Identifier;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdapterResource {

    @Inject
    private AdapterRestService adapterRestService;

    @DELETE
    @Path("/{id}")
    public Response deleteAdapter(@PathParam("id") String adapter) {
        if (adapterRestService.removeInputAdapter(Identifier.adapter(adapter)) != null) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
