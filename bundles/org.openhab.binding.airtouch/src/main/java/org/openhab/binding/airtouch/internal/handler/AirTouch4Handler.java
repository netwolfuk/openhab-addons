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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.airtouch.internal.AirTouchBindingConstants;
import org.openhab.binding.airtouch.internal.AirTouchConfiguration;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airtouch.v4.Response;
import airtouch.v4.ResponseCallback;
import airtouch.v4.connector.AirtouchConnector;
import airtouch.v4.constant.AcStatusConstants.PowerState;
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
    private AtomicBoolean gotConfigFromAirtouch = new AtomicBoolean(false);

    private @Nullable ScheduledFuture<?> future;

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
                logger.warn("Exception occured requesting AirTouch update: {}. Attempting to restart service.",
                        e.getMessage());
                this.airtouchConnector.shutdown();
                this.airtouchConnector.start();
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

    protected void thingStructureChanged(AirtouchStatus airtouchStatus) {
        logger.debug("Updating thing structure");
        ThingBuilder thingBuilder = editThing();
        airtouchStatus.getAcAbilities().values().forEach(ac -> {
            logger.info("Attmpting to add found ac {}", ac);
            ChannelGroupUID channelGroupUID = new ChannelGroupUID(
                    this.getThing().getUID() + ":ac-unit-" + ac.getAcNumber());
            logger.info("ChannelGroupUID: '{}'", channelGroupUID.getAsString());

            Channel channelUnitPower = ChannelBuilder
                    .create(new ChannelUID(channelGroupUID, "airconditioner-unit-power"))
                    .withLabel(String.format("AC Unit Power - %s (%s)", ac.getAcName(), ac.getAcNumber()))
                    .withType(new ChannelTypeUID(AirTouchBindingConstants.BINDING_ID, "airconditioner-unit-power"))
                    .build();
            thingBuilder.withChannel(channelUnitPower);
            logger.trace("Added channel: '{}'", channelUnitPower.getLabel());

            Channel channelUnitSetPoint = ChannelBuilder
                    .create(new ChannelUID(channelGroupUID, "airconditioner-unit-setpoint"))
                    .withLabel(String.format("AC Unit Setpoint - %s (%s)", ac.getAcName(), ac.getAcNumber()))
                    .withType(new ChannelTypeUID(AirTouchBindingConstants.BINDING_ID, "airconditioner-unit-setpoint"))
                    .build();
            thingBuilder.withChannel(channelUnitSetPoint);
            logger.trace("Added channel: '{}'", channelUnitSetPoint.getLabel());

            Channel channelUnitTemperature = ChannelBuilder
                    .create(new ChannelUID(channelGroupUID, "airconditioner-unit-temperature"))
                    .withLabel(String.format("AC Unit Temperature - %s (%s)", ac.getAcName(), ac.getAcNumber()))
                    .withType(
                            new ChannelTypeUID(AirTouchBindingConstants.BINDING_ID, "airconditioner-unit-temperature"))
                    .build();
            thingBuilder.withChannel(channelUnitTemperature);
            logger.trace("Added channel: '{}'", channelUnitTemperature.getLabel());

        });

        airtouchStatus.getGroupNames().forEach((zoneId, zoneName) -> {
            logger.info("Attmpting to add found zone {}", zoneName);
            ChannelGroupUID channelGroupUID = new ChannelGroupUID(this.getThing().getUID() + ":ac-zone-" + zoneId);
            logger.info("ChannelGroupUID: '{}'", channelGroupUID.getAsString());

            Channel channelZoneSetpoint = ChannelBuilder
                    .create(new ChannelUID(channelGroupUID, "airconditioner-zone-setpoint"))
                    .withLabel(String.format("Zone Setpoint - %s", zoneName))
                    .withType(new ChannelTypeUID(AirTouchBindingConstants.BINDING_ID, "airconditioner-zone-setpoint"))
                    .build();
            thingBuilder.withChannel(channelZoneSetpoint);
            logger.trace("Added channel: '{}'", channelZoneSetpoint.getLabel());

            if (airtouchStatus.getGroupStatuses().get(zoneId).hasSensor()) {
                Channel channelZoneTemperature = ChannelBuilder
                        .create(new ChannelUID(channelGroupUID, "airconditioner-zone-temperature"))
                        .withLabel(String.format("Zone Temperature - %s", zoneName))
                        .withType(new ChannelTypeUID(AirTouchBindingConstants.BINDING_ID,
                                "airconditioner-zone-temperature"))
                        .build();
                thingBuilder.withChannel(channelZoneTemperature);
                logger.trace("Added channel: '{}'", channelZoneTemperature.getLabel());

                Channel channelZoneBatteryLow = ChannelBuilder
                        .create(new ChannelUID(channelGroupUID, "airconditioner-zone-battery-low"))
                        .withLabel(String.format("Zone Battery Low Indicator  - %s", zoneName))
                        .withType(new ChannelTypeUID(AirTouchBindingConstants.BINDING_ID,
                                "airconditioner-zone-battery-low"))
                        .build();
                thingBuilder.withChannel(channelZoneBatteryLow);
                logger.trace("Added channel: '{}'", channelZoneBatteryLow.getLabel());
            }

            Channel channelZoneFlowPercentage = ChannelBuilder
                    .create(new ChannelUID(channelGroupUID, "airconditioner-zone-flow"))
                    .withLabel(String.format("Zone AirFlow - %s", zoneName))
                    .withType(new ChannelTypeUID(AirTouchBindingConstants.BINDING_ID, "airconditioner-zone-flow"))
                    .build();
            thingBuilder.withChannel(channelZoneFlowPercentage);
            logger.trace("Added channel: '{}'", channelZoneFlowPercentage.getLabel());

        });
        updateThing(thingBuilder.build());
        this.gotConfigFromAirtouch.set(true);
        logger.trace("Scheduling AirTouch polling update every {} seconds", this.config.refreshInterval);
        this.future = scheduler.scheduleWithFixedDelay(() -> {
            try {
                requestUpdate();
            } catch (IOException e) {
                logger.warn("Exception occured requesting AirTouch update: {}. Attempting to restart service.",
                        e.getMessage());
                this.airtouchConnector.shutdown();
                this.airtouchConnector.start();
            }
        }, this.config.refreshInterval, this.config.refreshInterval, TimeUnit.SECONDS);
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

        if (!this.responseReceived.containsValue(Boolean.FALSE) && !this.gotConfigFromAirtouch.get()) {
            logger.debug("Expected events received: {}. Updating thing structure.", this.responseReceived);
            // this.eventListener.eventReceived(getStatus());
            thingStructureChanged(getStatus());
            updatesStatuses(getStatus()); // TODO: Make this only update the items relating to the MessageType received.
        } else if (this.gotConfigFromAirtouch.get()) {
            updatesStatuses(getStatus());
        } else {
            logger.debug("Not all events received yet: {}", this.responseReceived);
        }
    }

    private void updatesStatuses(AirtouchStatus airtouchStatus) {
        logger.trace("Updating thing statuses: {}", airtouchStatus);
        airtouchStatus.getAcStatuses().forEach(ac -> {
            ChannelGroupUID channelGroupUID = new ChannelGroupUID(
                    this.getThing().getUID() + ":ac-unit-" + ac.getAcNumber());
            updateState(new ChannelUID(channelGroupUID, "airconditioner-unit-power"),
                    PowerState.ON.equals(ac.getPowerstate()) ? OnOffType.ON : OnOffType.OFF);
            logger.trace("Updating channel: '{}:{}'", channelGroupUID.getAsString(), "airconditioner-unit-power");

            updateState(new ChannelUID(channelGroupUID, "airconditioner-unit-setpoint"),
                    new DecimalType(ac.getTargetSetpoint()));
            logger.trace("Updating channel: '{}:{}'", channelGroupUID.getAsString(), "airconditioner-unit-setpoint");
            updateState(new ChannelUID(channelGroupUID, "airconditioner-unit-temperature"),
                    new DecimalType(ac.getCurrentTemperature()));
            logger.trace("Updating channel: '{}:{}'", channelGroupUID.getAsString(), "airconditioner-unit-temperature");
        });
        airtouchStatus.getGroupStatuses().forEach(zone -> {
            ChannelGroupUID channelGroupUID = new ChannelGroupUID(
                    this.getThing().getUID() + ":ac-zone-" + zone.getGroupNumber());
            updateState(new ChannelUID(channelGroupUID, "airconditioner-zone-setpoint"),
                    new DecimalType(zone.getTargetSetpoint()));
            logger.trace("Updating channel: '{}:{}'", channelGroupUID.getAsString(), "airconditioner-zone-setpoint");
            updateState(new ChannelUID(channelGroupUID, "airconditioner-zone-flow"),
                    new PercentType(zone.getOpenPercentage()));
            logger.trace("Updating channel: '{}:{}'", channelGroupUID.getAsString(), "airconditioner-zone-flow");

            if (getStatus().getGroupStatuses().get(zone.getGroupNumber()).hasSensor()) {
                updateState(new ChannelUID(channelGroupUID, "airconditioner-zone-temperature"),
                        new DecimalType(zone.getCurrentTemperature()));
                logger.trace("Updating channel: '{}:{}'", channelGroupUID.getAsString(),
                        "airconditioner-zone-temperature");
                updateState(new ChannelUID(channelGroupUID, "airconditioner-zone-battery-low"),
                        Boolean.TRUE.equals(zone.isBatteryLow()) ? OnOffType.ON : OnOffType.OFF);
                logger.trace("Updating channel: '{}:{}'", channelGroupUID.getAsString(),
                        "airconditioner-zone-battery-low");
            }
        });
    }

    @Override
    public void dispose() {
        logger.info("Dispose called");
        if (this.airtouchConnector.isRunning()) {
            this.airtouchConnector.shutdown();
        }
        if (this.future != null && !this.future.isCancelled()) {
            this.future.cancel(true);
        }
        super.dispose();
    }
}
