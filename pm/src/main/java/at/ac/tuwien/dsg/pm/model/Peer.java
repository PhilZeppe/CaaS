package at.ac.tuwien.dsg.pm.model;

import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;

import java.util.List;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class Peer {
    private String id = null;
    private String name;
    private DeliveryPolicy.Peer deliveryPolicy;
    private List<PeerAddress> peerAddressList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeliveryPolicy.Peer getDeliveryPolicy() {
        return deliveryPolicy;
    }

    public void setDeliveryPolicy(DeliveryPolicy.Peer deliveryPolicy) {
        this.deliveryPolicy = deliveryPolicy;
    }

    public List<PeerAddress> getPeerAddressList() {
        return peerAddressList;
    }

    public void setPeerAddressList(List<PeerAddress> peerAddressList) {
        this.peerAddressList = peerAddressList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        if (deliveryPolicy != peer.deliveryPolicy) return false;
        if (!id.equals(peer.id)) return false;
        if (name != null ? !name.equals(peer.name) : peer.name != null) return false;
        if (peerAddressList != null ? !peerAddressList.equals(peer.peerAddressList) : peer.peerAddressList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (deliveryPolicy != null ? deliveryPolicy.hashCode() : 0);
        result = 31 * result + (peerAddressList != null ? peerAddressList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deliveryPolicy=" + deliveryPolicy +
                ", peerAddressList=" + peerAddressList +
                '}';
    }
}
