/*
 * Copyright (c) 2010-2019, sikuli.org, sikulix.com - MIT license
 */

package se.vidstige.jadb.server;

import java.util.List;

/**
 * Created by vidstige on 20/03/14.
 */
public interface AdbResponder {
    void onCommand(String command);

    int getVersion();

    List<AdbDeviceResponder> getDevices();
}
