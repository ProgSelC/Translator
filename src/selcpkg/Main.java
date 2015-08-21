package selcpkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		String dictName = "English-Ukrainian";
		String src = "English.in";
		String dst = "Ukrainian.out";

		Dictionary engukr = restoreDict(dictName);
		if (engukr == null) {
			System.out.println("Создан новый словарь!");
			engukr = new Dictionary(dictName);
		}

		// engukr.printDict();

		translateAndSave(engukr, src, dst);

		serializeDict(engukr);
	}

	static void translateAndSave(Dictionary dict, String src, String dst) {
		String text = "";
		try (BufferedReader br = new BufferedReader(new FileReader(src))) {
			String line = "";
			for (; (line = br.readLine()) != null;) {
				text += line.toLowerCase() + "\n";
			}
		} catch (IOException e) {
			System.out.println("Error reading input file!");
		}

		String[] words = text.toLowerCase().split("[^a-z']+");

		ArrayList<String> translated = new ArrayList<>();
		ArrayList<String> nottranslated = new ArrayList<>();
		for (String word : words) {
			if (!(translated.contains(word) || nottranslated.contains(word))) {
				if (dict.getTranslate(word) != null) {
					text = text.replaceAll("\\b(" + word + ")\\b", dict.getTranslate(word));
					translated.add(word);
				} else {
					nottranslated.add(word);
				}
			}
		}

		if (!nottranslated.isEmpty()) {
			String answer = Interactive.askUser("Часть слов не найдена в словаре. Хотите предложить их первод?[y/n]",
					"[yn]");
			if (answer.equals("y")) {
				for (String word : nottranslated) {
					if (dict.addTranslate(word)) {
						text = text.replaceAll("\\b(" + word + ")\\b", dict.getTranslate(word));
					} else {
						text = text.replaceAll("\\b(" + word + ")\\b", "<" + word + ">");
					}
				}
			} else {
				for (String word : nottranslated) {
					text = text.replaceAll("\\b(" + word + ")\\b", "<" + word + ">");
				}
			}
		}
		
		String answer = Interactive.askUser("Перевод готов! Сохранить в файл или вывести на экран [f/o]", "[fo]");
		if (answer.equals("f")){
			saveToFile(text, dst);
		} else  {
			System.out.println(text);
		}
	}
	
	static void saveToFile(String text, String filename){
		try (PrintWriter fw = new PrintWriter(filename)) {
			fw.print(text);
		} catch (IOException e) {
			System.out.println("Error creating " + filename);
		}
		System.out.println("Перевод успешно сохнанен в "+filename);
	}

	static void serializeDict(Dictionary d) {
		try (ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(d.getName() + ".dict"))) {
			objStream.writeObject(d);
		} catch (IOException e) {
			System.out.println("ERROR saving dictionary!!!");
		}
	}

	static Dictionary restoreDict(String dictName) {
		Dictionary dict = null;
		File fl = new File(dictName + ".dict");
		if (fl.exists() && fl.isFile()) {
			try (ObjectInputStream OIS = new ObjectInputStream(new FileInputStream(dictName + ".dict"))) {
				dict = (Dictionary) OIS.readObject();
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("ERROR loading dictionary!!!");
			}
		} else {
			System.out.println("Файл со словарем не существует!");
		}
		return dict;
	}

}
