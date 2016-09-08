package qaston.editorconfig.generator;

public class WhitespaceStyle {
	public final Character indentStyle;
	public final Integer indentSize;
	public final String newlineStyle;

	public WhitespaceStyle(Character indentStyle, Integer indentSize, String newlineStyle) {
		super();
		this.indentStyle = indentStyle;
		this.indentSize = indentSize;
		this.newlineStyle = newlineStyle;
	}

	public boolean isEmpty() {
		return indentStyle == null && indentSize == null && newlineStyle == null;
	}
}
