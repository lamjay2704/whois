package net.ripe.db.whois.api.rest;

import net.ripe.db.whois.api.rest.client.StreamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class StreamingMarshalTextPlain implements StreamingMarshal {

    protected final Log logger = LogFactory.getLog(StreamingMarshalTextPlain.class);
    private final Writer writer;

    public StreamingMarshalTextPlain(OutputStream out) {
        this.writer  = new OutputStreamWriter(out);
    }

    @Override
    public void open() {
        logger.info("Called open");
    }

    @Override
    public void start(String name) {
        logger.info("Called start: " + name);
//        try {
//            writer.write(QueryMessages.outputFilterNotice().getFormattedText());
//        } catch (IOException e) {
//            throw new StreamingException(e);
//        }
    }

    @Override
    public void end(String name) {
        logger.info("Called end: " + name);
    }

    @Override
    public <T> void write(String name, T t) {
        logger.info("Called write: " + name);
        logger.info(t);
//        try {
//            writer.write(t.toString() + "\n");
//        } catch (IOException e) {
//            throw new StreamingException(e);
//        }
    }

    @Override
    public <T> void writeArray(T t) {
        logger.info("Called writeArray: " + t.toString());
        try {
            writer.write(t + "\n");
        } catch (IOException e) {
            throw new StreamingException(e);
        }
    }

    @Override
    public <T> void startArray(String name) {
        logger.info("Called startArray: " + name);
    }

    @Override
    public <T> void endArray() {
        logger.info("Called endArray");
    }

    @Override
    public void close() {
        logger.info("Called close");
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new StreamingException(e);
        }
    }

    @Override
    public <T> void singleton(T t) {
        logger.info("Called singleton");
    }
}
