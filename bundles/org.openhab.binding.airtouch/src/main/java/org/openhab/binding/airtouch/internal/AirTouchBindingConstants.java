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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link AirTouchBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nathaniel Wolfe - Initial contribution
 */
@NonNullByDefault
public class AirTouchBindingConstants {

    private static final String BINDING_ID = "airtouch4";

    // List of all Thing Type UIDs
    public static final ThingTypeUID BRIDGEV4_THING_TYPE = new ThingTypeUID(BINDING_ID, "bridgev4");
    public static final ThingTypeUID BRIDGEV5_THING_TYPE = new ThingTypeUID(BINDING_ID, "bridgev5");

    public static final ThingTypeUID AIR_CONDITIONER_THINGV4_TYPE = new ThingTypeUID(BINDING_ID, "air-conditioner-v4");
    public static final ThingTypeUID AIR_CONDITIONER_THINGV5_TYPE = new ThingTypeUID(BINDING_ID, "air-conditioner-v5");

    // List of all Channel ids
    public static final String AC_POWER_CHANNEL = "ac-power";
    public static final String ZONE_POWER_CHANNEL = "zone-power";
}
