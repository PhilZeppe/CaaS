package at.ac.tuwien.dsg.pm.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.io.Serializable;

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
