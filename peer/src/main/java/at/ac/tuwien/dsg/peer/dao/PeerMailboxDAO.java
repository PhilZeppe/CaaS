package at.ac.tuwien.dsg.peer.dao;

import at.ac.tuwien.dsg.smartcom.adapters.rest.JsonMessageDTO;

import java.util.List;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public interface PeerMailboxDAO {

    public JsonMessageDTO persistMessage(JsonMessageDTO message, String receiver);

    public List<JsonMessageDTO> getMessagesForReceiver(String receiver);

    public JsonMessageDTO pullMessageForReceiver(String receiver);
}
