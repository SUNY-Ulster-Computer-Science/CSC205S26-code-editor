package formatting;

public class Formatting {
	
	static int spaceToTab = 3;
	
	static public String clean(String str) {
		String[] tokens = str.split("\n");
		String[] array = arrayClean(tokens);
		String out = "";
		for (int i = 0; i < array.length; i++) {
			out += array[i] + '\n';
		}
		return out;
	}
	
	private static String[] arrayClean(String[] text) {
		String[] out = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			out[i] = cleanLine(text[i]);
		}
		return out;
	}
	
	private static String cleanLine(String str) {
		if (str.strip().equals(""))
			return "";
		
		String out = "";
		out = outside(str);
		out = middle(out);
		return out;
		
	}
	private static String outside(String str) {
		//fix indenting using only tabs
		
		String out = "";
		
		int tabsIn = 0;
		int tabsOut = 0;
		int spacesIn = 0;
		
		int i = 0;
		
		//measure whitespace
		while ((str.charAt(i) == ' ' || str.charAt(i) == '\t') && i < str.length()) {
			if (str.charAt(i) == ' ')
				spacesIn++;
			else
				tabsIn++;
			i++;
		}
		
		tabsOut = tabsIn + spacesIn / spaceToTab;
		
		for (int j = 1; j <= tabsOut; j++)
			out += '\t';
		
		out += str.trim();
		
		return out;
	}
	
	private static String middle(String str) {
		//detect excess whitespace inside line but excluding literal strings
		
		String out = "";
		int lastIndex = str.length() - 1;
		
		for (int i = 0; i < lastIndex; i++) {
			if (str.charAt(i) == '"') {
				while (i < lastIndex) {
					out += str.charAt(i);
					i++;
					if (str.charAt(i) == '"' && str.charAt(i - 1) != '\\') {
						out += str.charAt(i);
						break;
					}
				}
			}
			else if (str.charAt(i) != ' ' || str.charAt(i + 1) != ' ')
				out += str.charAt(i);
		}
		
		char c = str.charAt(lastIndex);
		if (c != ' ' && c != '"')
			out += c;
		
		return out;
	}
	
}
