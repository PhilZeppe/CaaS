package at.ac.tuwien.dsg.pm.dao;

import at.ac.tuwien.dsg.pm.exceptions.CollectiveAlreadyExistsException;
import at.ac.tuwien.dsg.pm.model.Collective;

import java.util.List;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public interface CollectiveDAO {

    public Collective addCollective(Collective collective) throws CollectiveAlreadyExistsException;

    public Collective getCollective(String id);

    public List<Collective> getAll();

    /**
     * Note that this method does not update the peers of the collective!
     * @param collective
     * @return
     */
    public Collective updateCollective(Collective collective);

    public Collective addPeerToCollective(String collectiveId, String peerId);

    public Collective removePeerToCollective(String collectiveId, String peerId);

    public Collective deleteCollective(String id);

    public void clearData();
}
