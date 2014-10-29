package at.ac.tuwien.dsg.pm.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class CollectiveAlreadyExistsException extends WebApplicationException {

    public CollectiveAlreadyExistsException() {
    }

    public CollectiveAlreadyExistsException(String message) {
        super(message);
    }

    public CollectiveAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectiveAlreadyExistsException(Throwable cause) {
        super(cause, Response.status(Response.Status.CONFLICT).build());
    }

}
