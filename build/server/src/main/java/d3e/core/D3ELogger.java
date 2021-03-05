package d3e.core;

public class D3ELogger {

	private static boolean debug;

	public static void setDebug(boolean debug) {
		D3ELogger.debug = debug;
	}
	
	public static boolean isDebug() {
		return debug;
	}

	public static void info(String msg) {
		System.err.println(msg);
	}

	public static void debug(String msg) {
		if (debug) {
			System.err.println(msg);
		}
	}

	public static void printStackTrace(Throwable t) {
		t.printStackTrace(System.err);
	}
}
