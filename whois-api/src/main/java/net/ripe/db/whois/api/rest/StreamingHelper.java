package net.ripe.db.whois.api.rest;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.OutputStream;

public class StreamingHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingHelper.class);

    private static final Splitter COMMA_SPLITTER = Splitter.on(',');

    public static StreamingMarshal getStreamingMarshal(final HttpServletRequest request, final OutputStream outputStream) {
        final String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        if (acceptHeader != null) {
            for (final String accept : COMMA_SPLITTER.split(acceptHeader)) {
                try {
                    final MediaType mediaType = MediaType.valueOf(accept);

                    if(MediaType.TEXT_PLAIN_TYPE.equals(mediaType)) {
                        return new StreamingMarshalTextPlain(outputStream); //TODO
                    }

                    final String subtype = mediaType.getSubtype().toLowerCase();
                    if (subtype.equals("json") || subtype.endsWith("+json")) {
                        return new StreamingMarshalJson(outputStream);
                    } else if (subtype.equals("xml") || subtype.endsWith("+xml")) {
                        return new StreamingMarshalXml(outputStream, "whois-resources");
                    }
                } catch (IllegalArgumentException ignored) {
                    LOGGER.debug("{}: {}", ignored.getClass().getName(), ignored.getMessage());
                }
            }
        }

        return new StreamingMarshalXml(outputStream, "whois-resources");
    }

    public static StreamingMarshal getStreamingMarshalJson(final OutputStream outputStream){
        return new StreamingMarshalJson(outputStream);
    }
}
