package at.ac.tuwien.dsg.pm.resources;

import at.ac.tuwien.dsg.pm.PeerManager;
import at.ac.tuwien.dsg.pm.exceptions.CollectiveAlreadyExistsException;
import at.ac.tuwien.dsg.pm.model.Collective;
import at.ac.tuwien.dsg.pm.model.Peer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
@Path("collective")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CollectiveResource {

    ObjectMapper mapper = new ObjectMapper();

    @Inject
    private PeerManager manager;

    @POST
    public Collective addCollective(Collective collective) throws CollectiveAlreadyExistsException {
        return manager.addCollective(collective);
    }

    @GET
    @Path("/{id}")
    public Collective getCollective(@PathParam("id") String id) {
        return manager.getCollective(id);
    }

    @GET
    @Path("/all")
    public List<Collective> getAll() {
        return manager.getAllCollectives();
    }

    @PUT
    public Collective updateCollective(Collective collective) {
        return manager.updateCollective(collective);
    }

    @PUT
    @Path("/{collectiveId}/{peerId}")
    public Collective addPeerToCollective(@PathParam("collectiveId") String collectiveId, @PathParam("peerId") String peerId) {
        return manager.addPeerToCollective(collectiveId, peerId);
    }

    @DELETE
    @Path("/{collectiveId}/{peerId}")
    public Collective removePeerToCollective(@PathParam("collectiveId") String collectiveId, @PathParam("peerId") String peerId) {
        return manager.removePeerToCollective(collectiveId, peerId);
    }

    @GET
    @Path("/test")
    public String test() {
        return "hello world";
    }

    @DELETE
    @Path("/{id}")
    public void deleteCollective(@PathParam("id") String id) {
        manager.deleteCollective(id);
    }

    @DELETE
    @Path("/all")
    public void deleteAll() {
        manager.clearCollectiveData();
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile() throws IOException {
        File file = File.createTempFile("collective", UUID.randomUUID().toString());
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        List<Collective> all = manager.getAllCollectives();

        for (Collective collective : all) {
            String s = mapper.writeValueAsString(collective);
            writer.println(s.replaceAll("\\s", ""));
        }

        writer.close();

        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=collective.dump");
        return response.build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.WILDCARD)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        manager.clearCollectiveData();

        // save it
        try {
            handleFile(uploadedInputStream, fileDetail);
        } catch (Exception e) {
            return Response.status(501).build();
        }

        return Response.status(200).build();
    }

    private void handleFile(InputStream uploadedInputStream, FormDataContentDisposition fileDetail) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedInputStream));
        String line = "";
        while ((line = reader.readLine()) != null) {
            Collective collective = mapper.readValue(line, Collective.class);
            manager.addCollective(collective);
        }
    }
}
