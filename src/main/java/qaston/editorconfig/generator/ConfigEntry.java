package qaston.editorconfig.generator;

import java.io.File;

public class ConfigEntry {
	public final File path;
	public final WhitespaceStyle style;

	public ConfigEntry(File path, WhitespaceStyle style) {
		super();
		this.path = path;
		this.style = style;
	}

	public boolean isEmpty() {
		return style.isEmpty();
	}
}
