package de.upb.crc901.proseco.commons.util;

import java.util.List;

/**
 * List utility class
 *
 * @author kadirayk
 *
 */
public class ListUtil {

	private ListUtil() {

	}

	public static <T> boolean isNotEmpty(List<T> list) {
		return list != null && !list.isEmpty();
	}

}
