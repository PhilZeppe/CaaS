package at.ac.tuwien.dsg.pm.resources;

import at.ac.tuwien.dsg.pm.PeerManager;
import at.ac.tuwien.dsg.pm.exceptions.PeerAlreadyExistsException;
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
@Path("peer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerResource {

    ObjectMapper mapper = new ObjectMapper();

    @Inject
    private PeerManager manager;

    @POST
    public Peer addPeer(Peer peer) throws PeerAlreadyExistsException {
        return manager.addPeer(peer);
    }

    @GET
    @Path("/{id}")
    public Peer getPeer(@PathParam("id") String id) {
        return manager.getPeer(id);
    }

    @GET
    @Path("/all")
    public List<Peer> getAll() {
        return manager.getAllPeers();
    }

    @PUT
    public Peer updatePeer(Peer peer) {
        return manager.updatePeer(peer);
    }

    @DELETE
    @Path("/{id}")
    public void deletePeer(@PathParam("id") String id) {
        manager.deletePeer(id);
    }

    @DELETE
    @Path("/all")
    public void deleteAll() {
        manager.clearPeerData();
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile() throws IOException {
        File file = File.createTempFile("peer", UUID.randomUUID().toString());
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        List<Peer> all = manager.getAllPeers();

        for (Peer peer : all) {
            String s = mapper.writeValueAsString(peer);
            writer.println(s.replaceAll("\\s", ""));
        }

        writer.close();

        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=peer.dump");
        return response.build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.WILDCARD)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        manager.clearPeerData();

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
            Peer peer = mapper.readValue(line, Peer.class);
            manager.addPeer(peer);
        }
    }
}
