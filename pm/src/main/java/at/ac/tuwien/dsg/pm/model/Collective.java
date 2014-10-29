package at.ac.tuwien.dsg.pm.model;

import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class Collective {
    private String id;
    private List<String> peers = new ArrayList<String>();
    private DeliveryPolicy.Collective deliveryPolicy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPeers() {
        return peers;
    }

    public void addPeer(String peer) {
        removePeer(peer);
        this.peers.add(peer);
    }

    public void removePeer(String peer) {
        this.peers.remove(peer);
    }

    public void setPeers(List<String> peers) {
        this.peers = peers;
    }

    public DeliveryPolicy.Collective getDeliveryPolicy() {
        return deliveryPolicy;
    }

    public void setDeliveryPolicy(DeliveryPolicy.Collective deliveryPolicy) {
        this.deliveryPolicy = deliveryPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collective that = (Collective) o;

        if (deliveryPolicy != that.deliveryPolicy) return false;
        if (!id.equals(that.id)) return false;
        if (peers != null ? !peers.equals(that.peers) : that.peers != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (peers != null ? peers.hashCode() : 0);
        result = 31 * result + (deliveryPolicy != null ? deliveryPolicy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Collective{" +
                "id='" + id + '\'' +
                ", peers=" + peers +
                ", deliveryPolicy=" + deliveryPolicy +
                '}';
    }
}
