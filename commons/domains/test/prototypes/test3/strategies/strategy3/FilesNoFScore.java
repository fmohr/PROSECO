import java.io.PrintWriter;

public class FilesNoFScore {

	public static void main(String Args[]) throws Exception {
		String outputDir = Args[2];
		String expectedFile = outputDir + "/expectedFile";
		PrintWriter writer = new PrintWriter(expectedFile, "UTF-8");
		writer.println("expected output");
		writer.close();
	}

}