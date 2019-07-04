package de.upb.crc901.proseco.commons.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;

/**
 * Implements IDomainScoreComputer<T> interface for String type.
 *
 * @author kadirayk
 *
 */
public class DefaultDomainScoreComputer implements IDomainScoreComputer<String> {

	@Override
	public Double getDomainScore(final String description, final String domain) throws DomainCouldNotBeDetectedException {
		if (StringUtils.isEmpty(description) || StringUtils.isEmpty(domain)) {
			throw new DomainCouldNotBeDetectedException();
		}
		if (description.contains(domain)) {
			return 1.0;
		}
		return 0.0;
	}

	/**
	 * Returns list of available domains in the domain folder
	 *
	 * @param prosecoConfig {@link PROSECOConfig} prosecoConfig
	 * @return list of available domains
	 */
	public List<String> getAvailableDomains(final PROSECOConfig prosecoConfig) {
		final List<String> domains = new ArrayList<>();
		final File domainsDir = prosecoConfig.getDirectoryForDomains();
		for (final File domain : domainsDir.listFiles()) {
			domains.add(domain.getName());
		}
		return domains;
	}

}
