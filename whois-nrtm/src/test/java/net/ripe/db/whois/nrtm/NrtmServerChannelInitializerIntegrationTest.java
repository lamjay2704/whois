package net.ripe.db.whois.nrtm;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import net.ripe.db.whois.nrtm.integration.AbstractNrtmIntegrationBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@org.junit.jupiter.api.Tag("IntegrationTest")
public class NrtmServerChannelInitializerIntegrationTest extends AbstractNrtmIntegrationBase {
    @Autowired private NrtmServerChannelInitializer nrtmServerPipelineFactory;
    @Autowired private List<ChannelHandler> channelHandlers;

    @Test
    public void testChannelHandlersAddedToPipeline() throws Exception {
        channelHandlers.removeIf(channelHandler -> (channelHandler instanceof NrtmServerChannelInitializer));

        EmbeddedChannel embeddedChannel1 = new EmbeddedChannel();
        EmbeddedChannel embeddedChannel2 = new EmbeddedChannel();
        nrtmServerPipelineFactory.initChannel(embeddedChannel1);
        nrtmServerPipelineFactory.initChannel(embeddedChannel2);


        final ChannelPipeline pipeline1 = embeddedChannel1.pipeline();
        final ChannelPipeline pipeline2 = embeddedChannel2.pipeline();

        final Set<ChannelHandler> toCheck = new HashSet<>(channelHandlers);
        final List<String> names = pipeline1.names();
        for (final String name : names) {
            if (name.equalsIgnoreCase("DefaultChannelPipeline$TailContext#0")) {
                continue;
            }

            final ChannelHandler channelHandler = pipeline1.get(name);
            final ChannelHandler.Sharable annotation = AnnotationUtils.findAnnotation(channelHandler.getClass(), ChannelHandler.Sharable.class);
            final boolean handlerIsShared = pipeline2.get(channelHandler.getClass()) == channelHandler;
            if (annotation == null) {
                assertFalse(handlerIsShared, "Handler is not sharable, but reused: " + channelHandler);
            } else {
                assertTrue(handlerIsShared, "Handler is sharable, but not reused: " + channelHandler);
            }

            if (channelHandler.getClass().getName().contains("ripe")) {
                ReflectionUtils.doWithLocalFields(channelHandler.getClass(), field -> {
                    final int modifiers = field.getModifiers();

                    final String fieldName = field.getName();
                    final String className = channelHandler.getClass().getName();

                        if (fieldName.startsWith("$SWITCH_TABLE$")) {   // hidden enum switch helper field, generated by javac
                            return;
                        } else if (fieldName.startsWith("$jacoco")) { // added by sonar
                            return;
                        } else if (fieldName.startsWith("ajc$")) {  // aspectj
                            return;
                        }

                    if (handlerIsShared) {
                        /* Shared handlers can not have state */
                        if (!Modifier.isFinal(modifiers)) {
                            fail("Non final field '" + fieldName + "' in reused channel handler " + className);
                        }
                    } else {
                        /* non-shared handlers can still be executed in parallel. this is a primitive (and definitely not full) check
                         * for thread safety. It is meant as a basic safety net only. */
                        if (!Modifier.isVolatile(modifiers) && !Modifier.isFinal(modifiers) && (field.getType() != Annotation.class)) {
                            fail("Field '" + fieldName + "' in channel handler " + className + " must be volatile or final");
                        }
                    }
                });
            }

            toCheck.remove(channelHandler);
        }

        if (!toCheck.isEmpty()) {
            fail("Unused channel handlers: " + toCheck);
        }
    }
}

