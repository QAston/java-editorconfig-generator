package qaston.editorconfig.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryConfigGenerator {
	private WhitespaceEstimatorFactory estimatorFactory;

	public void run(File directory, boolean root) {
		if (!directory.exists())
			return;
		if (!directory.canWrite())
			return;
		if (!directory.isDirectory())
			return;
		List<ConfigEntry> entries = generate(directory);
		saveConfig(directory, entries, root);
	}

	private List<ConfigEntry> generate(File directory) {
		List<ConfigEntry> fileEntries = new ArrayList<>();
		for (File file : directory.listFiles()) {
			WhitespaceStyleEstimator e = estimatorFactory.forFile(file);
			if (e == null)
				continue;
			WhitespaceStyle style;
			try {
				style = e.estimateStyle(new BufferedReader(new FileReader(directory)));
			} catch (FileNotFoundException e1) {
				throw new UncheckedIOException(e1);
			}
			fileEntries.add(new ConfigEntry(new File(file.getName()), style));
		}
		return fileEntries;
	}

	private void saveConfig(File directory, List<ConfigEntry> entries, boolean root) {
		try (FileWriter configWriter = new FileWriter(new File(directory, ".editorconfig"), false)) {
			if (root)
				configWriter.append("root=true\n");
			for (ConfigEntry e : entries) {
				if (e.isEmpty())
					continue;
				configWriter.append(String.format("[%1$s]\n", e.path));
				if (e.style.indentStyle != null) {
					String wsRepr = "space";
					if (e.style.indentStyle == '\t') {
						wsRepr = "tab";
					}
					configWriter.append(String.format("indent_style = %1$s\n", wsRepr));
					configWriter.append(String.format("indent_size = %1$s\n", e.style.indentSize));
				}
				if (e.style.newlineStyle != null) {
					String wsRepr = "lf";
					if (e.style.newlineStyle == "\r\n") {
						wsRepr = "crlf";
					}
					configWriter.append(String.format("end_of_line = %1$s\n", wsRepr));
				}
				configWriter.append("\n");

			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
