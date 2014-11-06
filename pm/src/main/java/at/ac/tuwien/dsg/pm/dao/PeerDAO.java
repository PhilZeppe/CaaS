package at.ac.tuwien.dsg.pm.dao;

import at.ac.tuwien.dsg.pm.exceptions.PeerAlreadyExistsException;
import at.ac.tuwien.dsg.pm.model.Peer;

import java.util.List;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public interface PeerDAO {

    public Peer addPeer(Peer peer) throws PeerAlreadyExistsException;

    public Peer getPeer(String id);

    public List<Peer> getAll();

    public Peer updatePeer(Peer peer);

    public Peer deletePeer(String id);

    public void clearData();
}
