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
package org.openhab.binding.airtouch4.internal;

import static org.openhab.binding.airtouch4.internal.AirTouch4BindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.airtouch4.internal.handler.AirTouch4AirConditionerHandler;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link AirTouch4HandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Nathaniel Wolfe - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.airtouch4", service = ThingHandlerFactory.class)
public class AirTouch4HandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(BRIDGE_THING_TYPE,
            AIR_CONDITIONER_THING_TYPE, ZONE_THING_TYPE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (AIR_CONDITIONER_THING_TYPE.equals(thingTypeUID)) {
            return new AirTouch4AirConditionerHandler(thing);
        }

        return null;
    }
}
