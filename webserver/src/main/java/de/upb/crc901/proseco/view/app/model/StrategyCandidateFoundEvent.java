package de.upb.crc901.proseco.view.app.model;

public class StrategyCandidateFoundEvent {

	private String candidateID;
	private long candidateFoundTimestamp;
	private String candidateDescription;
	private double candidateEvaluation;

	public StrategyCandidateFoundEvent() {
		// intentionally do nothing.
	}

	public String getCandidateID() {
		return this.candidateID;
	}

	public void setCandidateID(final String candidateID) {
		this.candidateID = candidateID;
	}

	public long getCandidateFoundTimestamp() {
		return this.candidateFoundTimestamp;
	}

	public void setCandidateFoundTimestamp(final long candidateFoundTimestamp) {
		this.candidateFoundTimestamp = candidateFoundTimestamp;
	}

	public String getCandidateDescription() {
		return this.candidateDescription;
	}

	public void setCandidateDescription(final String candidateDescription) {
		this.candidateDescription = candidateDescription;
	}

	public double getCandidateEvaluation() {
		return this.candidateEvaluation;
	}

	public void setCandidateEvaluation(final double candidateEvaluation) {
		this.candidateEvaluation = candidateEvaluation;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Candidate ID: " + this.candidateID);
		sb.append("\n");
		sb.append("Candidate Found Timestamp: " + this.candidateFoundTimestamp);
		sb.append("\n");
		sb.append("Candidate Description: " + this.candidateDescription);
		sb.append("\n");
		sb.append("Candidate Evaluation: " + this.candidateEvaluation);
		return sb.toString();
	}

}
