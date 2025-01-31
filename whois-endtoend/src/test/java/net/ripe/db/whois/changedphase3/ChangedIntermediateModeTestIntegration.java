package net.ripe.db.whois.changedphase3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static net.ripe.db.whois.changedphase3.util.Scenario.Builder.given;
import static net.ripe.db.whois.changedphase3.util.Scenario.Method.CREATE;
import static net.ripe.db.whois.changedphase3.util.Scenario.Method.DELETE;
import static net.ripe.db.whois.changedphase3.util.Scenario.Method.GET___;
import static net.ripe.db.whois.changedphase3.util.Scenario.Method.META__;
import static net.ripe.db.whois.changedphase3.util.Scenario.Method.MODIFY;
import static net.ripe.db.whois.changedphase3.util.Scenario.Method.SEARCH;
import static net.ripe.db.whois.changedphase3.util.Scenario.Mode.OLD_MODE;
import static net.ripe.db.whois.changedphase3.util.Scenario.ObjectStatus.OBJ_DOES_NOT_EXIST_____;
import static net.ripe.db.whois.changedphase3.util.Scenario.ObjectStatus.OBJ_EXISTS_NO_CHANGED__;
import static net.ripe.db.whois.changedphase3.util.Scenario.ObjectStatus.OBJ_EXISTS_WITH_CHANGED;
import static net.ripe.db.whois.changedphase3.util.Scenario.Protocol.EXPORT_;
import static net.ripe.db.whois.changedphase3.util.Scenario.Protocol.MAILUPD;
import static net.ripe.db.whois.changedphase3.util.Scenario.Protocol.NRTM___;
import static net.ripe.db.whois.changedphase3.util.Scenario.Protocol.REST___;
import static net.ripe.db.whois.changedphase3.util.Scenario.Protocol.SYNCUPD;
import static net.ripe.db.whois.changedphase3.util.Scenario.Protocol.TELNET_;
import static net.ripe.db.whois.changedphase3.util.Scenario.Req.NOT_APPLIC__;
import static net.ripe.db.whois.changedphase3.util.Scenario.Req.NO_CHANGED__;
import static net.ripe.db.whois.changedphase3.util.Scenario.Req.WITH_CHANGED;
import static net.ripe.db.whois.changedphase3.util.Scenario.Result.SUCCESS;

@org.junit.jupiter.api.Tag("IntegrationTest")
public class ChangedIntermediateModeTestIntegration extends AbstractChangedPhase3IntegrationTest {

    @BeforeAll
    public static void beforeClass() {
        System.setProperty("feature.toggle.changed.attr.available", "true");
    }

    @AfterAll
    public static void afterClass() {
        System.clearProperty("feature.toggle.changed.attr.available");
    }

    @Test
    public void intermediate_mode_rest() {
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(REST___, SEARCH, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(REST___, SEARCH, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(REST___, GET___, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(REST___, GET___, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(REST___, META__, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
    }

    @Test
    public void intermediate_mode_rest_create() {
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(REST___, CREATE, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(REST___, CREATE, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
    }

    @Test
    public void intermediate_mode_rest_modify() {
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(REST___, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(REST___, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(REST___, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(REST___, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
    }

    @Test
    public void intermediate_mode_rest_delete() {
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(REST___, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(REST___, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(REST___, DELETE, NOT_APPLIC__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(REST___, DELETE, NOT_APPLIC__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
    }

    @Test
    public void intermediate_mode_telnet() {
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(TELNET_, SEARCH, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(TELNET_, SEARCH, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(TELNET_, META__, NOT_APPLIC__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
    }

    @Test
    public void intermediate_mode_syncupdates() {
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(SYNCUPD, CREATE, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(SYNCUPD, CREATE, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(SYNCUPD, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(SYNCUPD, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(SYNCUPD, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(SYNCUPD, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(SYNCUPD, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(SYNCUPD, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(SYNCUPD, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(SYNCUPD, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
    }

    @Test
    public void intermediate_mode_mailupdates() {
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(MAILUPD, CREATE, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(MAILUPD, CREATE, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(MAILUPD, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(MAILUPD, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(MAILUPD, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(MAILUPD, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(MAILUPD, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(MAILUPD, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(MAILUPD, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(MAILUPD, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
    }

    @Test
    public void intermediate_mode_nrtm() {
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(NRTM___, CREATE, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(NRTM___, CREATE, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(NRTM___, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(NRTM___, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(NRTM___, MODIFY, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(NRTM___, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(NRTM___, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(NRTM___, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(NRTM___, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(NRTM___, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
    }

    @Test
    public void intermediate_mode_export() {
        given(OLD_MODE, OBJ_DOES_NOT_EXIST_____).when(EXPORT_, CREATE, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(EXPORT_, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(EXPORT_, MODIFY, NO_CHANGED__).then(SUCCESS, OBJ_EXISTS_NO_CHANGED__).run(context);

        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(EXPORT_, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_WITH_CHANGED).when(EXPORT_, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(EXPORT_, DELETE, WITH_CHANGED).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
        given(OLD_MODE, OBJ_EXISTS_NO_CHANGED__).when(EXPORT_, DELETE, NO_CHANGED__).then(SUCCESS, OBJ_DOES_NOT_EXIST_____).run(context);
    }

}
