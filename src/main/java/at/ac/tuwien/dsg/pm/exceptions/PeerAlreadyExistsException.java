package at.ac.tuwien.dsg.pm.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class PeerAlreadyExistsException extends WebApplicationException {

    public PeerAlreadyExistsException(Throwable cause) {
        super(cause, Response.status(Response.Status.CONFLICT).build());
    }
}
