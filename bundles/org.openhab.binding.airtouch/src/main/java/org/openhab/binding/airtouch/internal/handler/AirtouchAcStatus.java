package org.openhab.binding.airtouch.internal.handler;

import airtouch.v4.constant.AcStatusConstants.FanSpeed;
import airtouch.v4.constant.AcStatusConstants.Mode;
import airtouch.v4.constant.AcStatusConstants.PowerState;

public class AirtouchAcStatus {
    private PowerState acPowerState;
    private Integer acNumber;
    private Mode acMode;
    private FanSpeed acFanSpeed;
    private Boolean acSpillActive;
    private Boolean acTimerActive;
    private Integer acSetpointTemperature;
    private Integer acTemperture;
    private Integer acErrorCode;
    private Boolean acErrored;

    public PowerState getAcPowerState() {
        return acPowerState;
    }

    public void setAcPowerState(PowerState acPowerState) {
        this.acPowerState = acPowerState;
    }

    public Integer getAcNumber() {
        return acNumber;
    }

    public void setAcNumber(Integer acNumber) {
        this.acNumber = acNumber;
    }

    public Mode getAcMode() {
        return acMode;
    }

    public void setAcMode(Mode acMode) {
        this.acMode = acMode;
    }

    public FanSpeed getAcFanSpeed() {
        return acFanSpeed;
    }

    public void setAcFanSpeed(FanSpeed acFanSpeed) {
        this.acFanSpeed = acFanSpeed;
    }

    public Boolean getAcSpillActive() {
        return acSpillActive;
    }

    public void setAcSpillActive(Boolean acSpillActive) {
        this.acSpillActive = acSpillActive;
    }

    public Boolean getAcTimerActive() {
        return acTimerActive;
    }

    public void setAcTimerActive(Boolean acTimerActive) {
        this.acTimerActive = acTimerActive;
    }

    public Integer getAcSetpointTemperature() {
        return acSetpointTemperature;
    }

    public void setAcSetpointTemperature(Integer acSetpointTemperature) {
        this.acSetpointTemperature = acSetpointTemperature;
    }

    public Integer getAcTemperture() {
        return acTemperture;
    }

    public void setAcTemperture(Integer acTemperture) {
        this.acTemperture = acTemperture;
    }

    public Integer getAcErrorCode() {
        return acErrorCode;
    }

    public void setAcErrorCode(Integer acErrorCode) {
        this.acErrorCode = acErrorCode;
    }

    public Boolean getAcErrored() {
        return acErrored;
    }

    public void setAcErrored(Boolean acErrored) {
        this.acErrored = acErrored;
    }
}
