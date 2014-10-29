package at.ac.tuwien.dsg;


import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
* @author Philipp Zeppezauer (philipp.zeppezauer@gmail.com)
* @version 1.0
*/
class RequestMappingFeature implements Feature {

    @Override
    public boolean configure(final FeatureContext context) {

        context.register(ObjectMapperResolver.class);

        // If you comment out this line, it stops working.
        context.register(JacksonJaxbJsonProvider.class);

        return true;
    }
}
