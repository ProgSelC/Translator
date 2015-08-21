package selcpkg;

import java.io.Serializable;
import java.util.TreeMap;

public class Dictionary implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private TreeMap<String, String> dict;

	public Dictionary(String name) {
		super();
		this.name = name;
		dict = new TreeMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public String getTranslate(String word) {
		return dict.get(word);
	}

	public boolean addTranslate(String word) {
		String answer = Interactive.askUser("Please, input translation for '" + word + "' or 'q' to cancel",
				"^[А-яа-яІЇЄіїє ']+$|^q$");
		if (answer.equals("q")) {
			return false;
		} else {
			dict.put(word.toLowerCase(), answer.toLowerCase());
			return true;
		}
	}

	public boolean delTranslate(String word) {
		word = word.toLowerCase();
		if (dict.containsKey(word)) {
			dict.remove(word);
			return true;
		} else {
			return false;
		}
	}

	public boolean clearDictionary() {
		dict.clear();
		return true;
	}

	public void printDict() {
		for (String key : dict.keySet()) {
			System.out.println(key + ": " + dict.get(key));
		}
	}
}
