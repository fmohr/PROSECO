

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Grounding {
	public static void main(String[] Args) throws Exception {
		String outputDir = Args[1];
		String expectedFile = outputDir + "/expectedFile";
		File file = new File(expectedFile);
		Files.readAllLines(file.toPath());
	}
}
