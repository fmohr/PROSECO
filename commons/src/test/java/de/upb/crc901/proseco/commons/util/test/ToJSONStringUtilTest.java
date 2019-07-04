package de.upb.crc901.proseco.commons.util.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import de.upb.crc901.proseco.commons.util.ToJSONStringUtil;

public class ToJSONStringUtilTest {
	@Test
	public void toJSONStringTest() {
		final Map<String, Object> fields = new HashMap<>();
		fields.put("obj1", new Object());
		fields.put("obj2", null);
		fields.put("obj3", new JsonNodeFactory(true).arrayNode());
		final List<String> list = new ArrayList<>();
		list.add("val");
		fields.put("obj4", list);
		final String[] arr = { "val" };
		fields.put("obj5", arr);
		final Map<String, String> map = new HashMap<>();
		map.put("key", "val");
		fields.put("obj6", map);
		final String result = ToJSONStringUtil.toJSONString(fields);
		assertTrue(result.contains("StackTraceElement"));
		assertTrue(result.contains("obj1"));
		assertTrue(result.contains("obj2"));
		assertTrue(result.contains("obj3"));
		assertTrue(result.contains("obj4"));
		assertTrue(result.contains("obj5"));
		assertTrue(result.contains("obj6"));
	}
}
