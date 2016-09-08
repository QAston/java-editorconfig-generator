package qaston.editorconfig.generator;

import java.io.File;

public class WhitespaceEstimatorFactory {
	public WhitespaceStyleEstimator forFile(File path) {
		if (path.toString().endsWith(".java")) {
			return new JavaStyleEstimator(new WhitespaceStyle(' ', 2, "\n"));
		}
		return null;
	}
}
