package qaston.editorconfig.generator;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JavaStyleEstimatorTest {

	private WhitespaceStyleEstimator estimator;
	private WhitespaceStyle defaultStyle;

	private void expectResult(String input, Integer indentSize, Character indentChar, String newLine) {
		Reader reader = new StringReader(input);
		expectResult(reader, indentSize, indentChar, newLine);
	}

	private void expectResult(Reader reader, Integer indentSize, Character indentChar, String newLine) {
		WhitespaceStyle style = estimator.estimateStyle(reader);
		Assert.assertEquals(indentSize, style.indentSize);
		Assert.assertEquals(indentChar, style.indentStyle);
		Assert.assertEquals(newLine, style.newlineStyle);
	}

	private void expectNewline(String input, String expectedNewline) {
		expectResult(input, defaultStyle.indentSize, defaultStyle.indentStyle, expectedNewline);
	}

	@Before
	public void setUp() {
		defaultStyle = new WhitespaceStyle(null, null, null);
		estimator = new JavaStyleEstimator(defaultStyle);

	}

	@Test
	public void emptyFileHasDefaultStyle() {
		expectResult("", defaultStyle.indentSize, defaultStyle.indentStyle, defaultStyle.newlineStyle);
	}

	@Test
	public void fileWithOneLinestyleHasTheLineStyle() {
		expectNewline("\n", "\n");
		expectNewline("\r\n", "\r\n");
		expectNewline("   \n   \n", "\n");
		expectNewline("   \r\n   \r\n", "\r\n");
	}

	@Test
	public void fileWithDifferentLineStylesHasTheMajority() {
		expectNewline("   \n   \n \r\n", "\n");
		expectNewline("   \n\r\n   \r\n", "\r\n");
	}

	@Test
	public void fileWithSingleIndentationTabIndicatiorHasRecognizedIntendation() {
		expectResult("{\r\n\tpublic}", 1, '\t', "\r\n");
	}

	@Test
	public void fileWithSingleIndentationIndicatiorHasRecognizedIntendationIgnoringPadding() {
		expectResult("{\r\n\t  public}", 1, '\t', "\r\n");
	}

	@Test
	public void fileWithSpaceIndentationIndicatiorHasRecognizedIntendationIgnoringPadding() {
		expectResult("{\r\n   \tpublic}", 3, ' ', "\r\n");
	}

	@Test
	public void fileWithMultipleIntendationStyles() {
		expectResult("{\r\n  static\n\t  public\n\t int\r\n private\r\n}", 1, '\t', "\r\n");
	}
}
