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

    public static final String BINDING_ID = "airtouch";
    public static final String AIRTOUCH4_CONTROLLER = "airtouch4-controller";

    // List of all Thing Type UIDs
    public static final ThingTypeUID AIRTOUCH4_CONTROLLER_THING_TYPE = new ThingTypeUID(BINDING_ID,
            AIRTOUCH4_CONTROLLER);

    // List of all Channel ids
    public static final String AC_POWER_CHANNEL = "ac-power";
    public static final String ZONE_POWER_CHANNEL = "zone-power";

    public static final int DISCOVERY_SCAN_TIMEOUT_SECONDS = 30;

    public static final String PROPERTY_AIRTOUCH_HOST = "host";
    public static final String PROPERTY_AIRTOUCH_PORT = "port";
    public static final String PROPERTY_AIRTOUCH_REFRESH_INTERVAL = "refreshInterval";
    public static final String PROPERTY_AIRTOUCH_ID = "AirTouchConsoleId";
    public static final String PROPERTY_AIRTOUCH_UID = "AirTouchUid";
    public static final String PROPERTY_AIRTOUCH_MAC_ADDRESS = "AirTouchMacAddress";
    public static final String PROPERTY_AIRTOUCH_VERSION = "AirTouchVersion";
}
