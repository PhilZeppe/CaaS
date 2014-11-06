package at.ac.tuwien.dsg.rest.adapter.resources;

import at.ac.tuwien.dsg.rest.adapter.AdapterRestService;
import at.ac.tuwien.dsg.smartcom.adapters.RESTInputAdapter;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
@Path("rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RESTAdapterResource {

    @Inject
    private AdapterRestService adapterRestService;

    @POST
    public String createRESTAdapter(@QueryParam("port") int port, @QueryParam("uri") String serverURIPostfix) {
        RESTInputAdapter adapter = new RESTInputAdapter(port, serverURIPostfix);

        return adapterRestService.addPushAdapter(adapter).getId();
    }
}
