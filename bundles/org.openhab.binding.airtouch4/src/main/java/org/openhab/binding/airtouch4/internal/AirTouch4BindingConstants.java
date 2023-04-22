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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link AirTouch4BindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nathaniel Wolfe - Initial contribution
 */
@NonNullByDefault
public class AirTouch4BindingConstants {

    private static final String BINDING_ID = "airtouch4";

    // List of all Thing Type UIDs
    public static final ThingTypeUID BRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, "bridge");

    public static final ThingTypeUID AIR_CONDITIONER_THING_TYPE = new ThingTypeUID(BINDING_ID, "air-conditioner");
    public static final ThingTypeUID ZONE_THING_TYPE = new ThingTypeUID(BINDING_ID, "zone");

    // List of all Channel ids
    public static final String AC_POWER_CHANNEL = "ac-power";
    public static final String ZONE_POWER_CHANNEL = "zone-power";
}
