/*
 * Copyright (c) 2010-2019, sikuli.org, sikulix.com - MIT license
 */

package se.vidstige.jadb;

import java.io.IOException;

public class Subprocess {
    public Process execute(String[] command) throws IOException {
        return Runtime.getRuntime().exec(command);
    }
}
