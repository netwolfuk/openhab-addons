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
package org.openhab.binding.airtouch.internal;

import static org.openhab.binding.airtouch.internal.AirTouchBindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.airtouch.internal.handler.AirTouch4Handler;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AirTouch4HandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Nathaniel Wolfe - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.airtouch", service = ThingHandlerFactory.class)
public class AirTouchHandlerFactory extends BaseThingHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(AirTouchHandlerFactory.class);

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(AIRTOUCH4_CONTROLLER_THING_TYPE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        logger.debug("I am being asked if I support '{}'", thingTypeUID);
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        logger.debug("ThingHandler called with thing '{}'", thing.getThingTypeUID());

        if (AIRTOUCH4_CONTROLLER_THING_TYPE.equals(thingTypeUID)) {
            logger.debug("Creating AirTouchAirConditionerHandler for '{}'", thing.getThingTypeUID());
            return new AirTouch4Handler(thing);
        }

        logger.debug("Returning null for '{}'", thing.getThingTypeUID());
        return null;
    }
}
