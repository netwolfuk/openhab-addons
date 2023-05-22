package org.openhab.binding.airtouch.internal.discovery;

import static org.openhab.binding.airtouch.internal.AirTouchBindingConstants.AIRTOUCH4_CONTROLLER;
import static org.openhab.binding.airtouch.internal.AirTouchBindingConstants.AIRTOUCH4_CONTROLLER_THING_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.airtouch.internal.AirTouchBindingConstants;
import org.openhab.binding.airtouch.internal.handler.AirTouch4Handler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import airtouch.AirtouchVersion;
import airtouch.discovery.AirtouchBroadcaster;
import airtouch.discovery.BroadcastResponseCallback;

@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.airtouch")
@NonNullByDefault
public class AirTouchDiscoveryService extends AbstractDiscoveryService implements BroadcastResponseCallback {

    private final Logger logger = LoggerFactory.getLogger(AirTouchDiscoveryService.class);
    private AirtouchBroadcaster airtouch4Broadcaster;
    private @Nullable AirTouch4Handler airTouchAirConditionerHandler;

    public AirTouchDiscoveryService() {
        super(Set.of(AIRTOUCH4_CONTROLLER_THING_TYPE), AirTouchBindingConstants.DISCOVERY_SCAN_TIMEOUT_SECONDS, false);
        airtouch4Broadcaster = new AirtouchBroadcaster(AirtouchVersion.AIRTOUCH4, this);
    }

    @Override
    protected void startScan() {
        airtouch4Broadcaster.start();
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        airtouch4Broadcaster.shutdown();
    }

    @Override
    public void handleResponse(@Nullable BroadcastResponse response) {
        onAirtouchFoundInternal(response);
    }

    private void onAirtouchFoundInternal(@Nullable BroadcastResponse airtouch) {
        if (airtouch != null) {
            logger.info("Found '{}' at '{}' with id '{}'", airtouch.getAirtouchVersion(), airtouch.getHostAddress(),
                    airtouch.getAirtouchId());
            ThingUID thingUID = getThingUID(airtouch);
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(AirTouchBindingConstants.AIRTOUCH_ID, airtouch.getAirtouchId());
            properties.put("host", airtouch.getHostAddress());
            properties.put("port", airtouch.getPortNumber());
            properties.put("airTouchVersion", airtouch.getAirtouchVersion().getVersionIdentifier());
            properties.put("airTouchMacAddress", airtouch.getMacAddress());
            properties.put("airTouchIdentifier", airtouch.getAirtouchId());
            properties.put("refreshInterval", 60);
            properties.put("airTouchUid", airtouch.getMacAddress().replace(":", "").toLowerCase());
            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                    .withThingType(AIRTOUCH4_CONTROLLER_THING_TYPE)
                    .withLabel(airtouch.getAirtouchVersion().getVersionIdentifier()).withRepresentationProperty("host")
                    .build();
            thingDiscovered(discoveryResult);
        } else {
            // logger.debug("discovered unsupported light of type '{}' with id {}",
            // airtouch.getAirtouchVersion().getVersionIdentifier(), airtouch.getAirtouchId());
        }
    }

    private ThingUID getThingUID(BroadcastResponse airtouch) {
        return new ThingUID(AIRTOUCH4_CONTROLLER + ":" + airtouch.getMacAddress().replace(":", "").toLowerCase());
    }
}
