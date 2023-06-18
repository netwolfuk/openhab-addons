package org.openhab.binding.airtouch.internal;

import org.openhab.binding.airtouch.internal.dto.AirtouchStatus;

import airtouch.v4.model.AirConditionerAbilityResponse;

public class AirTouchStatusUtil {
	private AirTouchStatusUtil() {} // Can't Instantiate utilities class.

    public static  AirConditionerAbilityResponse findAcForZone(AirtouchStatus airTouchStatus, Integer zoneNumber) {
        AirConditionerAbilityResponse response = null;
        for (AirConditionerAbilityResponse acAbilityResponse : airTouchStatus.getAcAbilities().values()) {
            if (acAbilityResponse.getStartGroupNumber() >= zoneNumber) {
                response = acAbilityResponse;
            }
        }
        if (response == null) {
            throw new IllegalArgumentException(String.format("Unable to determine AC Unit for Zone '%s' (%s).",
            		airTouchStatus.getGroupNames().get(zoneNumber), zoneNumber));
        }
        return response;
    }

}
