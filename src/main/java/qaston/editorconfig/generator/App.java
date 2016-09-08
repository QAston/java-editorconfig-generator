package qaston.editorconfig.generator;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		File root = new File(args[0]);
		Collection<File> subdirs = FileUtils.listFilesAndDirs(root, new IOFileFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return false;
			}

			@Override
			public boolean accept(File file) {
				return false;
			}
		}, new IOFileFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return false;
			}

			@Override
			public boolean accept(File file) {
				return file.isDirectory() && file.canWrite();
			}
		});
		DirectoryConfigGenerator generator = new DirectoryConfigGenerator(new WhitespaceEstimatorFactory());
		generator.run(root, true);
		for (File dir : subdirs) {
			generator.run(dir, false);
		}
	}
}
