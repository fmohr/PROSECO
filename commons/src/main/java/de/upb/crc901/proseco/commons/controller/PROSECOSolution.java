package de.upb.crc901.proseco.commons.controller;

import java.io.File;
import java.util.SortedMap;

/**
 * PROSECOSolution describes a solution found at the end of the composition process.
 * <br>
 * parameters:<br>
 * <b>processId:</b> an Id that consists of a domain name and a randomly generated 10-digit alpha-numeric value (e.g. test-00dc91ae4d)<br>
 * <b>winningStrategyId:</b> Id of the winning strategy<br>
 * <b>winningScore:</b> score of the winning strategy<br>
 * <b>backupStrategies:</b> an ordered list of backup strategies that will be used in grounding process if winning strategy fails at grounding<br>
 * <b>winningStrategyFolder</b> folder of the winning strategy<br>
 * <b>backupStrategyFolders</b> folders of backup strategies<br>
 *
 * @author kadirayk
 *
 */
public class PROSECOSolution {
	private String processId;
	private String winningStrategyId;
	private Double winningScore;
	private SortedMap<Double, String> backupStrategies; // keep an ordered list of backup Strategies by their score
	private File winningStrategyFolder;
	private SortedMap<Double, File> backupStrategyFolders;

	public Double getWinningScore() {
		return this.winningScore;
	}

	public void setWinningScore(final Double winningScore) {
		this.winningScore = winningScore;
	}

	public File getWinningStrategyFolder() {
		return this.winningStrategyFolder;
	}

	public void setWinningStrategyFolder(final File winningStrategyFolder) {
		this.winningStrategyFolder = winningStrategyFolder;
	}

	public SortedMap<Double, File> getBackupStrategyFolders() {
		return this.backupStrategyFolders;
	}

	public void setBackupStrategyFolders(final SortedMap<Double, File> backupStrategyFolders) {
		this.backupStrategyFolders = backupStrategyFolders;
	}

	public String getProcessId() {
		return this.processId;
	}

	public void setProcessId(final String processId) {
		this.processId = processId;
	}

	public String getWinningStrategyId() {
		return this.winningStrategyId;
	}

	public void setWinningStrategyId(final String winningStrategyId) {
		this.winningStrategyId = winningStrategyId;
	}

	public SortedMap<Double, String> getBackupStrategies() {
		return this.backupStrategies;
	}

	public void setBackupStrategies(final SortedMap<Double, String> backupStrategies) {
		this.backupStrategies = backupStrategies;
	}

}
