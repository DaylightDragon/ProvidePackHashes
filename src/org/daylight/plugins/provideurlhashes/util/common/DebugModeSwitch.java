package org.daylight.plugins.provideurlhashes.util.common;

import java.io.File;

import org.daylight.plugins.provideurlhashes.data.References;

public class DebugModeSwitch {
	public static void init() {
		File f = new File(References.pluginConfigDirectory, "enableDebug.dev");
		if(!f.exists()) {
			SimpleLogger.debugOutputEnabled = false;
		}
	}
}
