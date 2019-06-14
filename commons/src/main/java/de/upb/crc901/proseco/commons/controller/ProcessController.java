package de.upb.crc901.proseco.commons.controller;

import java.io.IOException;
import java.util.UUID;

import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

public interface ProcessController {

	/**
	 * Creates a new PROSECO service construction process for a given prototype and
	 * thereby creates the neccessary process.json. In contrast to
	 * {@link ProcessController#createConstructionProcessEnvironment(String, String)}
	 * a process id is being generated.
	 * 
	 * @param domainName
	 *            the domain of the newly created process
	 * @return the environment of the resulting process that contains all paths,
	 *         files and configs of the resulting proseco project
	 * @throws IOException
	 */
	default PROSECOProcessEnvironment createConstructionProcessEnvironment(String domainName) throws Exception {
		String id = domainName + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toLowerCase();
		return createConstructionProcessEnvironment(domainName, id);
	}

	/**
	 * Creates a new PROSECO service construction process for a given prototype and
	 * thereby creates the neccessary process.json.
	 * 
	 * @param domainName
	 *            the domain of the newly created process
	 * @param processId
	 *            the id of the process, thus the resulting project resides in
	 *            <code>processes/processId</code>
	 * @return the environment of the resulting process that contains all paths,
	 *         files and configs of the resulting proseco project
	 * @throws IOException
	 */
	public PROSECOProcessEnvironment createConstructionProcessEnvironment(String domainName, String processId)
			throws Exception;

	/**
	 * Deserializes the {@link PROSECOProcessEnvironment} from the
	 * <code> process.json</code> file that resides in the specified folder.
	 * 
	 * @param id the processId that is also the folder in the <code>processes</code> directory.
	 * @return the deserialized {@link PROSECOProcessEnvironment}f
	 */
	public PROSECOProcessEnvironment getConstructionProcessEnvironment(String processId) throws Exception;
}
