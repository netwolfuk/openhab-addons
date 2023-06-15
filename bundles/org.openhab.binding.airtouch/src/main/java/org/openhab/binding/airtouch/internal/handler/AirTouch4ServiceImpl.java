package org.openhab.binding.airtouch.internal.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.airtouch.internal.dto.AirtouchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airtouch.v4.Request;
import airtouch.v4.Response;
import airtouch.v4.ResponseCallback;
import airtouch.v4.connector.AirtouchConnector;
import airtouch.v4.handler.AirConditionerAbilityHandler;
import airtouch.v4.handler.AirConditionerStatusHandler;
import airtouch.v4.handler.ConsoleVersionHandler;
import airtouch.v4.handler.GroupNameHandler;
import airtouch.v4.handler.GroupStatusHandler;
import airtouch.v4.model.AirConditionerAbilityResponse;
import airtouch.v4.model.AirConditionerStatusResponse;
import airtouch.v4.model.ConsoleVersionResponse;
import airtouch.v4.model.GroupNameResponse;

@NonNullByDefault
public class AirTouch4ServiceImpl implements AirTouch4Service {

    private final Logger logger = LoggerFactory.getLogger(AirTouch4ServiceImpl.class);

    private @Nullable AirtouchConnector airtouchConnector;

    private AirtouchStatus status = new AirtouchStatus();
    private Map<Integer, Boolean> responseReceived = new HashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);
    private AtomicBoolean gotConfigFromAirtouch = new AtomicBoolean(false);
    private @Nullable ScheduledFuture<?> future;

    private @Nullable AirTouchServiceListener airTouchServiceListener;

    public void setAirTouchServiceListener(AirTouchServiceListener airTouchServiceListener) {
        this.airTouchServiceListener = airTouchServiceListener;
    }

    @Override
    public void requestFullUpdate() throws IOException {
        if (this.airtouchConnector == null) {
            logger.trace("AirtouchConnector is not yet initialised. Skipping requestFullUpdate");
            return;
        }
        final AirtouchConnector myairtouchConnector = this.airtouchConnector;
        this.responseReceived.clear();
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        myairtouchConnector.sendRequest(GroupStatusHandler.generateRequest(counter.get(), null));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        myairtouchConnector.sendRequest(GroupNameHandler.generateRequest(counter.get(), null));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        myairtouchConnector.sendRequest(AirConditionerStatusHandler.generateRequest(counter.get(), null));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        myairtouchConnector.sendRequest(ConsoleVersionHandler.generateRequest(counter.get()));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        myairtouchConnector.sendRequest(AirConditionerAbilityHandler.generateRequest(counter.get(), null));
    }

    @Override
    public void requestStatusUpdate() throws IOException {
        if (this.airtouchConnector == null) {
            logger.trace("AirtouchConnector is not yet initialised. Skipping requestStatusUpdate");
            return;
        }
        final AirtouchConnector myairtouchConnector = this.airtouchConnector;
        myairtouchConnector.sendRequest(GroupStatusHandler.generateRequest(counter.incrementAndGet(), null));
        myairtouchConnector.sendRequest(AirConditionerStatusHandler.generateRequest(counter.incrementAndGet(), null));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void handleEvent(final @Nullable Response response) {
        if (response == null) {
            logger.trace("handlingEvent called with null response");
            return;
        }
        logger.trace("handlingEvent with response: {}", response);
        switch (response.getMessageType()) {
            case AC_STATUS:
                final List<AirConditionerStatusResponse> acStatuses = response.getData();
                status.setAcStatuses(acStatuses);
                if (this.gotConfigFromAirtouch.get()) {
                    this.airTouchServiceListener.airconditionerStatusUpdate(acStatuses);
                }
                break;
            case GROUP_STATUS:
                status.setGroupStatuses(response.getData());
                if (this.gotConfigFromAirtouch.get()) {
                    this.airTouchServiceListener.zoneStatusUpdate(response.getData());
                }
                break;
            case GROUP_NAME:
                status.setGroupNames(((List<GroupNameResponse>) response.getData()).stream()
                        .collect(Collectors.toMap(GroupNameResponse::getGroupNumber, GroupNameResponse::getName)));
                break;
            case AC_ABILITY:
                status.setAcAbilities(((List<AirConditionerAbilityResponse>) response.getData()).stream()
                        .collect(Collectors.toMap(AirConditionerAbilityResponse::getAcNumber, r -> r)));
                break;
            case CONSOLE_VERSION:
                status.setConsoleVersion((ConsoleVersionResponse) response.getData().stream().findFirst().orElse(null));
                break;
            case EXTENDED:
                break;
            case GROUP_CONTROL:
                break;
            default:
                break;
        }

        if (this.responseReceived.containsKey(response.getMessageId())) {
            this.responseReceived.put(response.getMessageId(), Boolean.TRUE);
        }

        if (!this.responseReceived.containsValue(Boolean.FALSE) && !this.gotConfigFromAirtouch.get()) {
            this.gotConfigFromAirtouch.set(true);
            logger.debug("Expected events received: {}. Updating thing structure.", this.responseReceived);
            // this.eventListener.eventReceived(getStatus());
            this.airTouchServiceListener.initialisationCompleted(getStatus());

            // updatesStatuses(getStatus()); // TODO: Make this only update the items relating to the MessageType
            // received.
            // } else if (this.gotConfigFromAirtouch.get()) {
            // updatesStatuses(getStatus());
        } else if (!this.gotConfigFromAirtouch.get()) {
            logger.debug("Not all events received yet: {}", this.responseReceived);
        }
    }

    public AirtouchStatus getStatus() {
        return status;
    }

    @Override
    public void start(@Nullable String host, int port) {
        this.airtouchConnector = new AirtouchConnector(host, port, new ResponseCallback() {
            @SuppressWarnings("rawtypes")
            public void handleResponse(@Nullable Response response) {
                handleEvent(response);
            }
        });
        this.airtouchConnector.start();
    }

    @Override
    public void shutdown() {
        if (this.airtouchConnector.isRunning()) {
            this.airtouchConnector.shutdown();
        }
    }

    @Override
    public void restart(@Nullable String host, int port) {
        shutdown();
        start(host, port);
    }

    @Override
    public void sendRequest(@NonNull Request airTouchRequest) throws IOException {
        this.airtouchConnector.sendRequest(airTouchRequest);
    }

    @Override
    public void registerListener(@NonNull AirTouchServiceListener airTouchServiceListener) {
        this.airTouchServiceListener = airTouchServiceListener;
    }

    @Override
    public int getNextRequestId() {
        return this.counter.incrementAndGet();
    }
}
