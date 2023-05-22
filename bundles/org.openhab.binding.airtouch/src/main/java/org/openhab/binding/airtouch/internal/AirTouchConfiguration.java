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
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link AirTouchConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Nathaniel Wolfe - Initial contribution
 */
@NonNullByDefault
public class AirTouchConfiguration {

    /**
     * Sample configuration parameters. Replace with your own.
     */
    @Nullable
    public String host;
    public int port;
    public int refreshInterval = 60;
}
