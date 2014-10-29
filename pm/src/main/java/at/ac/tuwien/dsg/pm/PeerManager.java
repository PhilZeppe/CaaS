package at.ac.tuwien.dsg.pm;

import at.ac.tuwien.dsg.pm.dao.CollectiveDAO;
import at.ac.tuwien.dsg.pm.exceptions.CollectiveAlreadyExistsException;
import at.ac.tuwien.dsg.pm.exceptions.PeerAlreadyExistsException;
import at.ac.tuwien.dsg.pm.dao.PeerDAO;
import at.ac.tuwien.dsg.pm.exceptions.PeerDoesNotExistsException;
import at.ac.tuwien.dsg.pm.model.Collective;
import at.ac.tuwien.dsg.pm.model.Peer;
import at.ac.tuwien.dsg.pm.model.PeerAddress;
import at.ac.tuwien.dsg.pm.resources.*;
import at.ac.tuwien.dsg.smartcom.callback.CollectiveInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerAuthenticationCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchCollectiveException;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchPeerException;
import at.ac.tuwien.dsg.smartcom.callback.exception.PeerAuthenticationException;
import at.ac.tuwien.dsg.smartcom.model.CollectiveInfo;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;
import at.ac.tuwien.dsg.smartcom.rest.ObjectMapperProvider;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
 * @version 1.0
 */
public class PeerManager {
    private static final Logger log = LoggerFactory.getLogger(PeerManager.class);
    private static long CACHE_SIZE = 1000;

    private HttpServer server;
    private final URI serverURI;

    @Inject
    private PeerDAO peerDAO;

    @Inject
    private CollectiveDAO collectiveDAO;

    private final LoadingCache<String, Peer> cache;

    public PeerManager(int port, String serverURIPostfix) {
        this.serverURI = URI.create("http://localhost:" + port + "/" + serverURIPostfix);

        this.cache = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(
                        //load entries from the database in case of a cache miss
                        new CacheLoader<String, Peer>() {
                            @Override
                            public Peer load(String id) throws Exception {
                                log.debug("loading peer with id {} from database", id);

                                Peer peer = peerDAO.getPeer(id);
                                if (peer == null) {
                                    throw new PeerDoesNotExistsException();
                                }

                                return peer;
                            }
                        });
    }

    public PeerManager(int port, String serverURIPostfix, PeerDAO peerDAO, CollectiveDAO collectiveDAO) {
        this(port, serverURIPostfix);
        this.peerDAO = peerDAO;
        this.collectiveDAO = collectiveDAO;
    }

    public void init() {
        server = GrizzlyHttpServerFactory.createHttpServer(serverURI, new RESTApplication());
        try {
            server.start();
        } catch (IOException e) {
            log.error("Could not initialize CommunicationRESTImpl", e);
        }
    }

    public void cleanUp() {
        server.shutdown();
    }

    public Peer addPeer(Peer peer) throws PeerAlreadyExistsException {
        peer = peerDAO.addPeer(peer);
        cache.put(peer.getId(), peer);
        return peer;
    }

    public Peer getPeer(String id) {
        try {
            return cache.get(id);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof PeerDoesNotExistsException) {
                log.debug("Could not load peer with id {}", id, e);
                return null;
            }
            log.error("Unknown exception while loading peer with id {}", id, e);
            return null;
        }
    }

    public Peer updatePeer(Peer peer) {
        peerDAO.updatePeer(peer);
        cache.put(peer.getId(), peer);
        return peer;
    }

    public void deletePeer(String id) {
        peerDAO.deletePeer(id);
        cache.invalidate(id);
    }

    public List<Peer> getAllPeers() {
        return peerDAO.getAll();
    }

    public void clearPeerData() {
        peerDAO.clearData();
        cache.invalidateAll();
    }

    public Collective addCollective(Collective collective) throws CollectiveAlreadyExistsException {
        return collectiveDAO.addCollective(collective);
    }

    public Collective getCollective(String id) {
        return collectiveDAO.getCollective(id);
    }

    public List<Collective> getAllCollectives() {
        return collectiveDAO.getAll();
    }

    /**
     * Note that this method does not update the peers of the collective!
     * @param collective
     * @return
     */
    public Collective updateCollective(Collective collective) {
        return collectiveDAO.updateCollective(collective);
    }

    public Collective addPeerToCollective(String collectiveId, String peerId) {
        return collectiveDAO.addPeerToCollective(collectiveId, peerId);
    }

    public Collective removePeerToCollective(String collectiveId, String peerId) {
        return collectiveDAO.removePeerToCollective(collectiveId, peerId);
    }

    public void deleteCollective(String id) {
        collectiveDAO.deleteCollective(id);
    }

    public void clearCollectiveData() {
        collectiveDAO.clearData();
    }

    private class RESTApplication extends ResourceConfig {
        private RESTApplication() {
            register(PeerResource.class);
            register(CollectiveResource.class);
            register(PeerInfoResource.class);
            register(CollectiveInfoResource.class);
            register(PeerAuthenticationResource.class);

            register(MultiPartFeature.class);
            register(ObjectMapperProvider.class);
            register(JacksonFeature.class);
//            register(new LoggingFilter(java.util.logging.Logger.getLogger("Jersey"), true));
            register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(PeerManager.this).to(PeerManager.class);
                }
            });
        }
    }
}
