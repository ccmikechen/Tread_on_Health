package edu.kuas.mis.wmc.app;

import android.util.Log;

public class Util {
	public static void log(Object msg) {
		if(MainActivity.debug)
			if(msg != null)
				Log.e("[DEBUG]", msg.toString());
			else
				Log.e("[DEBUG]", "Error Msg is null.");
	}
	
	public static void log(Object className, Object msg) {
		if(MainActivity.debug)
			if(className != null)
				if(msg != null)
					Log.e("[DEBUG]", className.toString() + " error: " + msg.toString());
				else
					Log.e("[DEBUG]", "Error Msg is null.");
			else
				Log.e("[DEBUG]", "ClassName is null.");
	}
}
