package org.openhab.binding.airtouch.internal.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import airtouch.v4.model.AirConditionerAbilityResponse;
import airtouch.v4.model.AirConditionerStatusResponse;
import airtouch.v4.model.ConsoleVersionResponse;
import airtouch.v4.model.GroupStatusResponse;

public class AirtouchStatus {
    private List<AirConditionerStatusResponse> acStatuses = new ArrayList<>();
    private Map<Integer, AirConditionerAbilityResponse> acAbilities = new HashMap<>();
    private List<GroupStatusResponse> groupStatuses = new ArrayList<>();
    private Map<Integer, String> groupNames = new HashMap<>();
    private ConsoleVersionResponse consoleVersion = null;

    public List<AirConditionerStatusResponse> getAcStatuses() {
        return acStatuses;
    }

    public void setAcStatuses(List<AirConditionerStatusResponse> acStatuses) {
        this.acStatuses = acStatuses;
    }

    public Map<Integer, AirConditionerAbilityResponse> getAcAbilities() {
        return acAbilities;
    }

    public void setAcAbilities(Map<Integer, AirConditionerAbilityResponse> acAbilities) {
        this.acAbilities = acAbilities;
    }

    public List<GroupStatusResponse> getGroupStatuses() {
        return groupStatuses;
    }

    public void setGroupStatuses(List<GroupStatusResponse> groupStatuses) {
        this.groupStatuses = groupStatuses;
    }

    public Map<Integer, String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(Map<Integer, String> groupNames) {
        this.groupNames = groupNames;
    }

    public ConsoleVersionResponse getConsoleVersion() {
        return consoleVersion;
    }

    public void setConsoleVersion(ConsoleVersionResponse consoleVersion) {
        this.consoleVersion = consoleVersion;
    }
}
