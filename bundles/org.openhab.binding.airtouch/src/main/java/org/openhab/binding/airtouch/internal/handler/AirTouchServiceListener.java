package org.openhab.binding.airtouch.internal.handler;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.airtouch.internal.dto.AirtouchStatus;

import airtouch.v4.model.AirConditionerStatusResponse;
import airtouch.v4.model.GroupStatusResponse;

public interface AirTouchServiceListener {

    void initialisationCompleted(final @NonNull AirtouchStatus status);

    void airconditionerStatusUpdate(final @NonNull List<AirConditionerStatusResponse> acStatuses);

    void zoneStatusUpdate(final @NonNull List<GroupStatusResponse> groupStatuses);

    void allStatusUpdate(final @NonNull AirtouchStatus status);
}
