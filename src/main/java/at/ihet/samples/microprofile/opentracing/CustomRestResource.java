package at.ihet.samples.microprofile.opentracing;

import io.opentracing.contrib.jaxrs2.client.ClientTracingFeature;
import io.opentracing.util.GlobalTracer;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RequestScoped
@Path("/custom")
@Traced
public class CustomRestResource {

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get() {
        return createRestClient().get();
    }

    @Path("/delete")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String delete() {
        return createRestClient().delete();
    }

    @Path("/patch")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public String patch() {
        return createRestClient().patch();
    }

    @Path("/post")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String post() {
        return createRestClient().post();
    }

    /**
     * @return the rest client we have built ourself
     */
    private ExternalRestResource createRestClient() {
        try {
            final ClientTracingFeature feature = createClientTracingFeature();
            return RestClientBuilder.newBuilder()
                    .baseUrl(new URL("http://httpbin.org"))
                    .readTimeout(2000, TimeUnit.SECONDS)
                    .connectTimeout(2000, TimeUnit.SECONDS)
                    .register(feature)
                    .build(ExternalRestResource.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URI is not valid");
        }
    }

    /**
     * @return the configured ClientTracingFeature instance with a decorator
     */
    private ClientTracingFeature createClientTracingFeature() {
        return new ClientTracingFeature.Builder(GlobalTracer.get())
                .withTraceSerialization(false)
                .withDecorators(Collections.singletonList(new ClientTracingDecorator()))
                .build();
    }
}
