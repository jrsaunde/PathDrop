package guiFX;

public class Validator {
	public static boolean validateWindow(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean validatePort(String str) {
		try {
			int num = Integer.parseInt(str);
			if (num>65535)
				return false;
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean validateIP(String str) {
		String[] srcIPSplit = str.split("\\."); // regular expression
		if (srcIPSplit.length != 4)
			return false;
		for (String s : srcIPSplit) {
			if (s.length()>3)
				return false;
			try {
				int num = Integer.parseInt(s);
				if (num>255)
					return false;
			} catch(NumberFormatException e) {
				return false;
			}
			
		}
		return true;
	}
}
