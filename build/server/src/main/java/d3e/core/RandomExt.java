package d3e.core;

import java.security.SecureRandom;
import java.util.Random;

public class RandomExt {
	public static Random secure() {
		return new SecureRandom();
	}
	
	public static boolean nextBool(Random ran) {
		return ran.nextBoolean();
	}
}
