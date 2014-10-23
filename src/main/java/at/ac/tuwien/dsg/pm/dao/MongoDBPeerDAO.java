package at.ac.tuwien.dsg.pm.dao;

import at.ac.tuwien.dsg.pm.exceptions.PeerAlreadyExistsException;
import at.ac.tuwien.dsg.pm.model.Peer;
import at.ac.tuwien.dsg.pm.model.PeerAddress;
import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class MongoDBPeerDAO implements PeerDAO {
    private static final Logger log = LoggerFactory.getLogger(MongoDBPeerDAO.class);
    private static final String COLLECTION = "PEERS";

    private final DBCollection coll;

    public MongoDBPeerDAO(String host, int port, String database) throws UnknownHostException {
        this(new MongoClient(host, port), database, COLLECTION);
    }

    public MongoDBPeerDAO(MongoClient client, String database, String collection) {
        coll = client.getDB(database).getCollection(collection);
    }

    @Override
    public Peer addPeer(Peer peer) throws PeerAlreadyExistsException {
        DBObject object = serializePeer(peer);
        if (peer.getId() == null) {
            object.removeField("_id");
        }
        try {
            //try to insert the new document
            WriteResult insert = coll.insert(object);
            if (peer.getId() == null) {
                peer.setId(String.valueOf(object.get("_id")));
            }

            log.trace("Created document for peer: {}", object);
        } catch (DuplicateKeyException e) {
            log.trace("Peer already exists: {}", object);
            throw new PeerAlreadyExistsException(e);
        }

        return peer;
    }

    @Override
    public Peer getPeer(String id) {
        return deserializePeer(coll.findOne(new BasicDBObject("_id", id)));
    }

    @Override
    public List<Peer> getAll() {
        DBCursor dbObjects = coll.find(new BasicDBObject());
        final List<Peer> peers = new ArrayList<>(dbObjects.size());

        for (DBObject dbObject : dbObjects) {
            peers.add(deserializePeer(dbObject));
        }

        return peers;
    }

    @Override
    public Peer updatePeer(Peer peer) {
        coll.update(new BasicDBObject("_id", peer.getId()), serializePeer(peer), false, false);
        return peer;
    }

    @Override
    public void deletePeer(String id) {
        coll.remove(new BasicDBObject("_id", id));
    }

    public void clearData() {
        coll.remove(new BasicDBObject());
    }

    private DBObject serializePeer(Peer peer) {
        BasicDBObject dbObject = new BasicDBObject()
                .append("_id", peer.getId())
                .append("name", peer.getName())
                .append("deliveryPolicy", peer.getDeliveryPolicy().name());

        List<DBObject> addresses = new ArrayList<DBObject>(peer.getPeerAddressList().size());
        for (PeerAddress peerAddress : peer.getPeerAddressList()) {
            BasicDBObject address = new BasicDBObject()
                    .append("type", peerAddress.getType())
                    .append("values", peerAddress.getValues());
            addresses.add(address);
        }
        return dbObject.append("addresses", addresses);
    }

    private Peer deserializePeer(DBObject object) {
        if (object == null) {
            return null;
        }

        Peer peer = new Peer();
        peer.setId(String.valueOf(object.get("_id")));
        peer.setName(String.valueOf(object.get("name")));
        peer.setDeliveryPolicy(DeliveryPolicy.Peer.valueOf(String.valueOf(object.get("deliveryPolicy"))));

        List<PeerAddress> list = new ArrayList<PeerAddress>();
        peer.setPeerAddressList(list);

        Collection<DBObject> collection = (Collection<DBObject>) object.get("addresses");
        for (DBObject dbObject : collection) {
            PeerAddress address = new PeerAddress();
            address.setType(String.valueOf(dbObject.get("type")));
            address.setValues(new ArrayList<String>((List<String>) dbObject.get("values")));

            list.add(address);
        }

        return peer;
    }
}
