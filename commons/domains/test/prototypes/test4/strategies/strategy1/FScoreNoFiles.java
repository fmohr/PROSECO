import java.io.PrintWriter;

public class FScoreNoFiles {

	public static void main(String Args[]) throws Exception {
		String outputDir = Args[2];
		PrintWriter writer = new PrintWriter(outputDir + "/score", "UTF-8");
		String fScore = "0.7";
		System.out.print("FScoreNoFiles");
		writer.println(fScore);
		writer.close();
	}

}