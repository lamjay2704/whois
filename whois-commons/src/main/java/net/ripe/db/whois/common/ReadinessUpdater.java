package net.ripe.db.whois.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReadinessUpdater {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadinessUpdater.class);
    private boolean loadBalancerEnabled = true;

    public void up(){
        this.loadBalancerEnabled = true;
        LOGGER.info("Marked service as ready to receive traffic");
    }

    public void down() {
        this.loadBalancerEnabled = false;
        LOGGER.info("Marked service as not ready to receive traffic");
    }

    public boolean isLoadBalancerEnabled() {
        return loadBalancerEnabled;
    }

}
