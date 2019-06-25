package de.upb.crc901.proseco.commons.processstatus;

public enum EProcessState {

	INIT("init"), CREATED("created"), DOMAIN_DEFINITION("domain"), INTERVIEW("interview"), COMPOSITION("composition"), PROTOTYPE_EXTRACTED("prototype"), STRATEGY_CHOSEN("chosen"), GROUNDING("grounding"), DEPLOYMENT("deployment"), DONE(
			"done");

	private String name;

	private EProcessState(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
