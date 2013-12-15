package org.teiath.web.util;

import java.util.HashMap;
import java.util.Map;

public class HexColorBuilder {

	public static Map hexCodeGenerator(int colorCount) {
		HashMap<Integer, String> hexColorMap = new HashMap<>();

		String[] letters = new String[15];
		letters = "0123456789ABCDEF".split("");

		for (int k = 0; k < colorCount; k++) {
			String code = "";
			for (int i = 0; i < 6; i++) {
				double ind = Math.random() * 15;
				int index = (int) Math.round(ind);
				code += letters[index];
			}
			if (code.length() < 6) {
				for (int i = code.length(); i < 6; i++) {
					code += "0";
				}
			}
			hexColorMap.put(k, code);
		}

		return hexColorMap;
	}
}
