package org.openhab.binding.airtouch.internal.handler;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import airtouch.v4.Request;

public interface AirTouch4Service {

    void start(@Nullable String host, int port);

    void shutdown();

    void restart(@Nullable String host, int port);

    void registerListener(@NonNull AirTouchServiceListener airTouchServiceListener);

    int getNextRequestId();

    void sendRequest(@NonNull Request airTouchRequest) throws IOException;

    void requestFullUpdate() throws IOException;

    void requestStatusUpdate() throws IOException;
}
