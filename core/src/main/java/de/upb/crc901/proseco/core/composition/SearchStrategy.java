package de.upb.crc901.proseco.core.composition;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.SerializationUtil;

public abstract class SearchStrategy implements Runnable {

	private static final Logger L = LoggerFactory.getLogger(SearchStrategy.class);

	private final String strategyName;
	private final File dirOfInputs;
	private final File dirOfOutputs;
	private final PROSECOProcessEnvironment environment;
	private final long deadline;

	public SearchStrategy(final String[] args) throws IOException {
		if (args.length != 4) {
			throw new IllegalArgumentException(
					"A search strategy must be invoked with an array of exactly four arguments (folder with PROSECO conf and process id, input folder, output folder, and timeout in seconds).\nThe following arguments were given: "
							+ Arrays.toString(args));
		}
		File environmentDir = new File(args[0]); // the process folder
		this.environment = new PROSECOProcessEnvironment(environmentDir);
		this.dirOfInputs = new File(args[1]);
		this.dirOfOutputs = new File(args[2]);
		this.strategyName = this.dirOfOutputs.getName();
		L.debug("Recognized strategy {} by output dir name. Using {} as input folder.", this.strategyName, this.dirOfInputs.getAbsolutePath());
		this.deadline = System.currentTimeMillis() + Integer.valueOf(args[3]) * 1000;
	}

	public PROSECOProcessEnvironment getEnvironment() {
		return this.environment;
	}

	public File getDirOfInputs() {
		return this.dirOfInputs;
	}

	public File getDirOfOutputs() {
		return this.dirOfOutputs;
	}

	public String getStrategyName() {
		return this.strategyName;
	}

	public InterviewFillout getInterview() {
		return SerializationUtil.readAsJSON(this.getEnvironment().getInterviewStateFile());
	}

	protected boolean checkCandidate(final File candidateOutputFolder) throws InterruptedException, IOException {
		File analysisProcessFile = this.environment.verificationExecutable();
		if (L.isDebugEnabled()) {
			L.debug("Running analysis {} on {}", analysisProcessFile.getAbsolutePath(), candidateOutputFolder.getAbsolutePath());
		}
		ProcessBuilder sb = new ProcessBuilder(analysisProcessFile.getAbsolutePath(), candidateOutputFolder.getAbsolutePath());
		Process p = sb.start();
		int exitCode = p.waitFor();
		if (L.isDebugEnabled()) {
			L.debug("Result code is {}.", exitCode);
		}
		return exitCode == 0;
	}

	protected void writeOutputObject(final String filename, final Object o) throws IOException {
		File outFile = new File(this.dirOfOutputs + File.separator + filename);
		if (!outFile.getParentFile().exists()) {
			FileUtils.forceMkdir(outFile.getParentFile());
		}
		BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream(outFile));
		try (ObjectOutputStream os = new ObjectOutputStream(fw)) {
			os.writeObject(o);
		}
	}

	protected void writeStringOutput(final String filename, final String output) throws IOException {
		File outFile = new File(this.dirOfOutputs + File.separator + filename);
		if (!outFile.getParentFile().exists()) {
			FileUtils.forceMkdir(outFile.getParentFile());
		}
		FileUtils.writeStringToFile(outFile, output, Charset.defaultCharset());
	}

	protected void writeScore(final double score) throws IOException {
		this.writeStringOutput("score", "" + score);
	}

	protected File getStrategyDirectory() throws IOException {
		return new File(this.getEnvironment().getStrategyDirectory() + File.separator + this.getStrategyName()).getCanonicalFile();
	}

	protected int getRemainingSeconds() {
		return (int) ((this.deadline - System.currentTimeMillis()) / 1000);
	}
}
