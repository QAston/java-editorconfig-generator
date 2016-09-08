package qaston.editorconfig.generator;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import qaston.editorconfig.javagrammar.Java8Lexer;

public class JavaStyleEstimator implements WhitespaceStyleEstimator {
	private WhitespaceStyle defaultStyle;

	public JavaStyleEstimator(WhitespaceStyle defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	@Override
	public WhitespaceStyle estimateStyle(Reader in) {

		Lexer lexer;
		try {
			lexer = new Java8Lexer(new ANTLRInputStream(in));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		lexer.setChannel(Token.HIDDEN_CHANNEL);

		BufferedTokenStream bufferedTokenStream = new BufferedTokenStream(lexer);

		int crlfCount = 0;
		int lfCount = 0;
		boolean justAfterNewline = false;
		int indentLevel = 0;
		Map<String, Integer> intendationCount = new HashMap<>();

		bufferedTokenStream.fill();
		List<? extends Token> tokens = bufferedTokenStream.getTokens();
		for (Token t : tokens) {
			if (t.getType() == Java8Lexer.LBRACE) {
				indentLevel++;
			}
			if (t.getType() == Java8Lexer.RBRACE) {
				indentLevel--;
			}
			if (justAfterNewline) {
				justAfterNewline = false;
				if (indentLevel == 1) {
					if (t.getType() == Java8Lexer.WS) {
						String indentation = removePadding(t.getText());
						intendationCount.putIfAbsent(indentation, 0);
						intendationCount.compute(indentation, (String s, Integer count) -> count + 1);
					}
				}
			}
			if (t.getType() == Java8Lexer.CRLF) {
				crlfCount++;
				justAfterNewline = true;
			}
			if (t.getType() == Java8Lexer.LF) {
				lfCount++;
				justAfterNewline = true;
			}
		}

		Optional<String> mostPopularWs = intendationCount.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
				.map(Map.Entry::getKey);

		Character identStyle = null;
		Integer identSize = null;
		if (mostPopularWs.isPresent()) {
			identStyle = mostPopularWs.get().charAt(0);
			if (identStyle == ' ')
				identSize = mostPopularWs.get().length();
		}

		String lineEnding = null;

		if (crlfCount > lfCount) {
			lineEnding = "\r\n";
		} else if (crlfCount < lfCount) {
			lineEnding = "\n";
		}

		return new WhitespaceStyle(identStyle, identSize, lineEnding);
	}

	private String removePadding(String text) {
		char identStyle = text.charAt(0);
		if (identStyle == '\t')
			return "\t";
		int identSize = 1;
		while (identSize < text.length()) {
			if (text.charAt(identSize) != identStyle)
				break;
			identSize++;
		}
		return text.substring(0, identSize);
	}

}
