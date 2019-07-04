import java.io.PrintWriter;

public class BadScoreButNoErrors {

	public static void main(String Args[]) throws Exception {
		String outputDir = Args[2];
		PrintWriter writer = new PrintWriter(outputDir + "/score", "UTF-8");
		String fScore = "0.9";
		writer.println(fScore);
		writer.close();
		String expectedFile = outputDir + "/expectedFile";
		PrintWriter writer2 = new PrintWriter(expectedFile, "UTF-8");
		writer2.println("expected output");
		writer2.close();
	}

}