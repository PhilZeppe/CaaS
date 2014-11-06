package at.ac.tuwien.dsg.rest.adapter.resources;

import at.ac.tuwien.dsg.rest.adapter.AdapterRestService;
import at.ac.tuwien.dsg.smartcom.adapters.DropboxInputAdapter;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
@Path("dropbox")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DropboxAdapterResource {

    @Inject
    private AdapterRestService adapterRestService;

    @POST
    public String createEmailAdapter(DropboxAdapterConfig config,
                                     @DefaultValue("1000") @QueryParam("interval") long interval,
                                     @DefaultValue("true") @QueryParam("delete") boolean deleteIfSuccessful) {
        Message returnMessage = new Message.MessageBuilder()
                .setType(config.type)
                .setSubtype(config.subtype)
                .setSenderId(Identifier.component("dropbox"))
                .setConversationId(config.conversationId)
                .create();

        DropboxInputAdapter adapter = new DropboxInputAdapter(config.dropboxKey, config.dropboxFolder, config.fileName, returnMessage);

        return adapterRestService.addPullAdapter(adapter, interval, deleteIfSuccessful).getId();
    }

    private static class DropboxAdapterConfig {
        String dropboxKey;
        String dropboxFolder;
        String fileName;

        String type;
        String subtype;
        String conversationId;
    }
}
