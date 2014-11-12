package at.ac.tuwien.dsg;

import at.ac.tuwien.dsg.util.FreePortProviderUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

public class PeerManagerConnectorTest {

    @Before
    public void setUp() throws Exception {
        int freePort = FreePortProviderUtil.getFreePort();

        URI serverURI = URI.create("http://localhost:" + freePort + "/test");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetCollectiveInfo() throws Exception {

    }

    @Test
    public void testAuthenticate() throws Exception {

    }

    @Test
    public void testGetPeerInfo() throws Exception {

    }
}