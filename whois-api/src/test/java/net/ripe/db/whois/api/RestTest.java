package net.ripe.db.whois.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.ripe.db.whois.api.rest.client.RestClientUtils;
import net.ripe.db.whois.api.rest.domain.ErrorMessage;
import net.ripe.db.whois.api.rest.domain.WhoisResources;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class RestTest {
    private static final Client client;

    static {
        final JacksonJsonProvider jsonProvider = new JacksonJaxbJsonProvider()
                .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class)
                .register(jsonProvider)
                .register(new MyCustom())
                .build();
    }


    @Provider
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    private static class MyCustom implements MessageBodyReader<WhoisResources>, MessageBodyWriter<WhoisResources> {

        Logger logger = org.slf4j.LoggerFactory.getLogger(MyCustom.class);

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type.isAssignableFrom(WhoisResources.class);
        }

        @Override
        public WhoisResources readFrom(Class<WhoisResources> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            logger.info("JACOPO read: " + new String(entityStream.readAllBytes(), StandardCharsets.UTF_8));
            return new WhoisResources();
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type.isAssignableFrom(WhoisResources.class);
        }

        @Override
        public void writeTo(WhoisResources whoisResources, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            logger.info("JACOPO write");
            entityStream.write("Jacopo".getBytes(StandardCharsets.UTF_8));
            entityStream.flush();
        }
    }

    public static WebTarget target(final int port, final String path) {
        return client.target(String.format("http://localhost:%d/%s", port, path));
    }

    public static WebTarget target(final int port, final String path, String queryParam, final String apiKey) {
        return client.target(String.format("http://localhost:%d/%s?%sapiKey=%s", port, path,
                StringUtils.isBlank(queryParam) ? "" : queryParam + "&",
                apiKey));
    }

    public static WebTarget target(final int port, final String path, final String pathParam, String queryParam, final String apiKey) {
        return client.target(String.format("http://localhost:%d/%s/%s?%sapiKey=%s", port, path, RestClientUtils.encode(pathParam),
                StringUtils.isBlank(queryParam) ? "" : queryParam + "&",
                apiKey));
    }

    public static WhoisResources mapClientException(final ClientErrorException e) {
        return e.getResponse().readEntity(WhoisResources.class);
    }

    public static void assertOnlyErrorMessage(final ClientErrorException e, final String severity, final String text, final String... argument) {
        WhoisResources whoisResources = e.getResponse().readEntity(WhoisResources.class);
        assertErrorCount(whoisResources, 1);
        assertErrorMessage(whoisResources, 0, severity, text, argument);
    }

    public static void assertErrorMessage(final ClientErrorException e, final int number, final String severity, final String text, final String... argument) {
        WhoisResources whoisResources = e.getResponse().readEntity(WhoisResources.class);
        assertErrorMessage(whoisResources, number, severity, text, argument);
    }

    public static void assertErrorMessage(final WhoisResources whoisResources, final int number, final String severity, final String text, final String... argument) {

        ErrorMessage errorMsg = whoisResources.getErrorMessages().get(number);

        assertThat(errorMsg.getText(), is(text));
        assertThat(errorMsg.getSeverity(), is(severity));

        if (argument.length > 0) {
            assertThat(errorMsg.getArgs(), hasSize(argument.length));
            for (int i = 0; i < argument.length; i++) {
                assertThat(errorMsg.getArgs().get(i).getValue(), is(argument[i]));
            }
        }
    }

    public static void assertInfoCount(final WhoisResources whoisResources, final int expectedCount) {
        int errorCount = 0;
        for (ErrorMessage em : whoisResources.getErrorMessages()) {
            if ("Info".equalsIgnoreCase(em.getSeverity())) {
                errorCount++;
            }
        }
        assertThat(errorCount, is(expectedCount));
    }

    public static void assertWarningCount(final WhoisResources whoisResources, final int expectedCount) {
        int errorCount = 0;
        for (ErrorMessage em : whoisResources.getErrorMessages()) {
            if ("Warning".equalsIgnoreCase(em.getSeverity())) {
                errorCount++;
            }
        }
        assertThat(errorCount, is(expectedCount));
    }

    public static void assertErrorCount(final WhoisResources whoisResources, final int expectedCount) {
        int errorCount = 0;
        for (ErrorMessage em : whoisResources.getErrorMessages()) {
            if ("Error".equalsIgnoreCase(em.getSeverity())) {
                errorCount++;
            }
        }
        assertThat(errorCount, is(expectedCount));
    }
}
