package at.ac.tuwien.dsg.rest.adapter.resources;

import at.ac.tuwien.dsg.rest.adapter.AdapterRestService;
import at.ac.tuwien.dsg.smartcom.adapters.EmailInputAdapter;
import at.ac.tuwien.dsg.smartcom.utils.PropertiesLoader;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
@Path("email")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmailAdapterResource {

    @Inject
    private AdapterRestService adapterRestService;

    @POST
    public String createEmailAdapter(
            EmailAdapterConfig config,
            @DefaultValue("1000") @QueryParam("interval") long interval,
            @DefaultValue("true") @QueryParam("delete") boolean deleteIfSuccessful) {
        EmailInputAdapter adapter = new EmailInputAdapter(config.subject, config.host,
                config.username, config.password, config.port, config.authentication,
                config.type, config.subtype, config.deleteMessage);

        return adapterRestService.addPullAdapter(adapter, interval, deleteIfSuccessful).getId();
    }

    @POST
    @Path("default")
    public String createDefaultEmailAdapter(@QueryParam("subject") String subject, @QueryParam("type") String type,
                                            @QueryParam("subtype") String subtype,
                                            @DefaultValue("1000") @QueryParam("interval") long interval,
                                            @DefaultValue("true") @QueryParam("delete") boolean deleteIfSuccessful) {
        EmailInputAdapter adapter = new EmailInputAdapter(subject,
                PropertiesLoader.getProperty("EmailAdapter.properties", "hostIncoming"),
                PropertiesLoader.getProperty("EmailAdapter.properties", "username"),
                PropertiesLoader.getProperty("EmailAdapter.properties", "password"),
                Integer.valueOf(PropertiesLoader.getProperty("EmailAdapter.properties", "portIncoming")),
                true, type, subtype, true);

        return adapterRestService.addPullAdapter(adapter, interval, deleteIfSuccessful).getId();
    }

    private static class EmailAdapterConfig {
        String subject;
        String host;
        String username;
        String password;
        int port;
        boolean authentication;
        String type;
        String subtype;
        boolean deleteMessage;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isAuthentication() {
            return authentication;
        }

        public void setAuthentication(boolean authentication) {
            this.authentication = authentication;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSubtype() {
            return subtype;
        }

        public void setSubtype(String subtype) {
            this.subtype = subtype;
        }

        public boolean isDeleteMessage() {
            return deleteMessage;
        }

        public void setDeleteMessage(boolean deleteMessage) {
            this.deleteMessage = deleteMessage;
        }
    }
}
