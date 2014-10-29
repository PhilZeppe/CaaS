package at.ac.tuwien.dsg;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
* @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
* @version 1.0
*/
class SerializableDeserializer extends StdDeserializer<Serializable> {

    SerializableDeserializer() {
        super(Serializable.class);
    }

    @Override
    public Serializable deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return _parseString(jp, ctxt);
    }
}
