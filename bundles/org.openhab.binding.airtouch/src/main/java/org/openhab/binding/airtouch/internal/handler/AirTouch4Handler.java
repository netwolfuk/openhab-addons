/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.airtouch.internal.handler;

import static org.openhab.binding.airtouch.internal.AirTouchBindingConstants.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.airtouch.internal.AirTouchConfiguration;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airtouch.v4.Response;
import airtouch.v4.ResponseCallback;
import airtouch.v4.connector.AirtouchConnector;
import airtouch.v4.handler.AirConditionerAbilityHandler;
import airtouch.v4.handler.AirConditionerStatusHandler;
import airtouch.v4.handler.ConsoleVersionHandler;
import airtouch.v4.handler.GroupNameHandler;
import airtouch.v4.handler.GroupStatusHandler;
import airtouch.v4.model.AirConditionerAbilityResponse;
import airtouch.v4.model.ConsoleVersionResponse;
import airtouch.v4.model.GroupNameResponse;

/**
 * The {@link AirTouch4Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nathaniel Wolfe - Initial contribution
 */
@NonNullByDefault
public class AirTouch4Handler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(AirTouch4Handler.class);

    private @Nullable AirTouchConfiguration config;

    private @Nullable AirtouchConnector airtouchConnector;
    private AirtouchStatus status = new AirtouchStatus();
    private Map<Integer, Boolean> responseReceived = new HashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    public AirTouch4Handler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (AC_POWER_CHANNEL.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(AirTouchConfiguration.class);
        logger.debug("Using configuration: {}", config);

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly, i.e. any network access must be done in
        // the background initialization below.
        // Also, before leaving this method a thing status from one of ONLINE, OFFLINE or UNKNOWN must be set. This
        // might already be the real thing status in case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        this.airtouchConnector = new AirtouchConnector(config.host, config.port, new ResponseCallback() {
            @SuppressWarnings("rawtypes")
            public void handleResponse(@Nullable Response response) {
                updateStatus(ThingStatus.ONLINE);
                handleEvent(response);
            }
        });

        // Example for background initialization:
        scheduler.execute(() -> {
            this.airtouchConnector.start();
            try {
                requestUpdate();
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }

        });

        // These logging types should be primarily used by bindings
        // logger.trace("Example trace message");
        // logger.debug("Example debug message");
        // logger.warn("Example warn message");
        //
        // Logging to INFO should be avoided normally.
        // See https://www.openhab.org/docs/developer/guidelines.html#f-logging

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    private void requestUpdate() throws IOException {
        this.responseReceived.clear();
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        this.airtouchConnector.sendRequest(GroupStatusHandler.generateRequest(counter.get(), null));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        this.airtouchConnector.sendRequest(GroupNameHandler.generateRequest(counter.get(), null));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        this.airtouchConnector.sendRequest(AirConditionerStatusHandler.generateRequest(counter.get(), null));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        this.airtouchConnector.sendRequest(ConsoleVersionHandler.generateRequest(counter.get()));
        this.responseReceived.put(counter.incrementAndGet(), Boolean.FALSE);
        this.airtouchConnector.sendRequest(AirConditionerAbilityHandler.generateRequest(counter.get(), null));
    }

    public AirtouchStatus getStatus() {
        return status;
    }

    private void handleEvent(@Nullable Response response) {
        if (response == null) {
            return;
        }
        switch (response.getMessageType()) {
            case AC_STATUS:
                status.setAcStatuses(response.getData());
                break;
            case GROUP_STATUS:
                status.setGroupStatuses(response.getData());
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

        if (!this.responseReceived.containsValue(Boolean.FALSE)) {
            logger.debug("Expected events received: {}. Sending update to listeners.", this.responseReceived);
            // this.eventListener.eventReceived(getStatus());
        } else {
            logger.debug("Not all events received yet: {}", this.responseReceived);
        }
    }
}
