package qaston.editorconfig.generator;

import java.io.Reader;

public interface WhitespaceStyleEstimator {

	WhitespaceStyle estimateStyle(Reader in);

}