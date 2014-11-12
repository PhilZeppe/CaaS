package at.ac.tuwien.dsg.peer.dao;

import at.ac.tuwien.dsg.smartcom.adapters.rest.JsonMessageDTO;
import at.ac.tuwien.dsg.smartcom.utils.MongoDBInstance;
import com.mongodb.MongoClient;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MongoDBPeerMailboxDAOTest {

    private MongoDBInstance mongoDB;

    private MongoDBPeerMailboxDAO dao;

    @Before
    public void setUp() throws Exception {
        mongoDB = new MongoDBInstance();
        mongoDB.setUp();

        MongoClient mongo = new MongoClient("localhost", 12345);
        dao = new MongoDBPeerMailboxDAO(mongo, "TEST", "PEER");
    }

    @After
    public void tearDown() throws Exception {
        mongoDB.tearDown();
    }

    @Test
    public void testPersistMessageAndGetMessagesForReceiver() throws Exception {
        String receiver = "testReceiver";
        List<JsonMessageDTO> messages = dao.getMessagesForReceiver(receiver);
        assertThat(messages, Matchers.hasSize(0));

        dao.persistMessage(createMessageWrapper("1"), "sender1");
        dao.persistMessage(createMessageWrapper("2"), "sender1");
        dao.persistMessage(createMessageWrapper("3"), "sender1");
        dao.persistMessage(createMessageWrapper("1"), "sender2");
        dao.persistMessage(createMessageWrapper("1"), "sender3");

        messages = dao.getMessagesForReceiver("sender1");
        assertThat(messages, Matchers.hasSize(3));

        messages = dao.getMessagesForReceiver("sender2");
        assertThat(messages, Matchers.hasSize(1));

        messages = dao.getMessagesForReceiver("sender3");
        assertThat(messages, Matchers.hasSize(1));
    }

    @Test
    public void testPullMessageForReceiver() throws Exception {
        dao.persistMessage(createMessageWrapper("1"), "sender1");
        dao.persistMessage(createMessageWrapper("2"), "sender1");
        dao.persistMessage(createMessageWrapper("3"), "sender1");
        dao.persistMessage(createMessageWrapper("1"), "sender2");
        dao.persistMessage(createMessageWrapper("1"), "sender3");

        JsonMessageDTO dto = dao.pullMessageForReceiver("sender2");
        assertNotNull(dto);
        assertEquals("1", dto.getId());

        dto = dao.pullMessageForReceiver("sender1");
        assertNotNull(dto);
        assertEquals("1", dto.getId());

        dto = dao.pullMessageForReceiver("sender3");
        assertNotNull(dto);
        assertEquals("1", dto.getId());

        dto = dao.pullMessageForReceiver("sender1");
        assertNotNull(dto);
        assertEquals("2", dto.getId());

        dto = dao.pullMessageForReceiver("sender1");
        assertNotNull(dto);
        assertEquals("3", dto.getId());

        dto = dao.pullMessageForReceiver("sender1");
        assertNull(dto);
    }

    private JsonMessageDTO createMessageWrapper(String id) {
        return createMessage("content"+id, "type"+id, "subtype"+id, "sender"+id, id, "language"+id, "securityToken"+id, "conversation"+id);
    }

    private JsonMessageDTO createMessage(String content, String type, String subtype,
                                         String sender, String id, String language,
                                         String securityToken, String conversation) {
        JsonMessageDTO dto = new JsonMessageDTO();
        dto.setContent(content);
        dto.setType(type);
        dto.setSubtype(subtype);
        dto.setSender(sender);
        dto.setId(id);
        dto.setConversation(conversation);
        dto.setLanguage(language);
        dto.setSecurityToken(securityToken);

        return dto;
    }
}