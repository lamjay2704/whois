package net.ripe.db.whois.update.handler.validator.inetnum;

import com.google.common.collect.ImmutableList;
import net.ripe.db.whois.common.dao.StatusDao;
import net.ripe.db.whois.common.domain.CIString;
import net.ripe.db.whois.common.domain.Maintainers;
import net.ripe.db.whois.common.iptree.Ipv6Tree;
import net.ripe.db.whois.common.rpsl.AttributeType;
import net.ripe.db.whois.common.rpsl.ObjectType;
import net.ripe.db.whois.common.rpsl.attrs.InetStatus;
import net.ripe.db.whois.update.authentication.Principal;
import net.ripe.db.whois.update.authentication.Subject;
import net.ripe.db.whois.update.domain.Action;
import net.ripe.db.whois.update.domain.PreparedUpdate;
import net.ripe.db.whois.update.domain.UpdateContext;
import net.ripe.db.whois.update.domain.UpdateMessages;
import net.ripe.db.whois.update.handler.validator.BusinessRuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

import static net.ripe.db.whois.common.rpsl.AttributeType.STATUS;
import static net.ripe.db.whois.update.handler.validator.inetnum.InetStatusHelper.getStatus;

@Component
public class Inet6numStatusValidator implements BusinessRuleValidator {

    private static final ImmutableList<Action> ACTIONS = ImmutableList.of(Action.MODIFY, Action.DELETE);
    private static final ImmutableList<ObjectType> TYPES = ImmutableList.of(ObjectType.INET6NUM);

    private final StatusDao statusDao;
    private final Ipv6Tree ipv6Tree;
    private final Maintainers maintainers;

    @Autowired
    public Inet6numStatusValidator(
            final StatusDao statusDao,
            final Ipv6Tree ipv6Tree,
            final Maintainers maintainers) {
        this.statusDao = statusDao;
        this.ipv6Tree = ipv6Tree;
        this.maintainers = maintainers;
    }

    @Override
    public void validate(final PreparedUpdate update, final UpdateContext updateContext) {
        switch (update.getAction()) {
            case MODIFY:
                validateModify(update, updateContext);
                break;
            case DELETE:
                validateDelete(update, updateContext);
                break;
            default:
                throw new IllegalStateException(update.getAction().toString());
        }
    }

    private void validateModify(final PreparedUpdate update, final UpdateContext updateContext) {
        if (update.getReferenceObject() == null || update.getUpdatedObject() == null) {
            return;
        }

        final CIString originalStatus = update.getReferenceObject().getValueForAttribute(STATUS);
        final CIString updateStatus = update.getUpdatedObject().getValueForAttribute(STATUS);

        if (!Objects.equals(originalStatus, updateStatus) && (!hasAuthOverride(updateContext.getSubject(update)))) {
            updateContext.addMessage(update, UpdateMessages.statusChange());
        }
    }

    private void validateDelete(final PreparedUpdate update, final UpdateContext updateContext) {
        if (update.getReferenceObject() == null) {
            return;
        }

        final InetStatus status;
        try {
            status = getStatus(update.getReferenceObject());
            if (status == null) {
                // invalid status attribute value
                return;
            }
        } catch (IllegalArgumentException e) {
            // status attribute not found
            return;
        }

        if (status.requiresRsMaintainer()) {
            final Set<CIString> mntBy = update.getReferenceObject().getValuesForAttribute(AttributeType.MNT_BY);
            if (!maintainers.isRsMaintainer(mntBy)) {
                if (!hasAuthOverride(updateContext.getSubject(update))) {
                    updateContext.addMessage(update, UpdateMessages.deleteWithStatusRequiresAuthorization(status.toString()));
                }
            }
        }
    }

    private boolean hasAuthOverride(final Subject subject) {
        return subject.hasPrincipal(Principal.OVERRIDE_MAINTAINER);
    }

    @Override
    public ImmutableList<Action> getActions() {
        return ACTIONS;
    }

    @Override
    public ImmutableList<ObjectType> getTypes() {
        return TYPES;
    }

}
