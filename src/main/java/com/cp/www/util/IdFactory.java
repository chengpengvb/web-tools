package com.cp.www.util;

import java.util.UUID;

public final class IdFactory {

	public static String buildId() {
		return UUID.randomUUID().toString().replaceAll("-", "c").substring(0, 16);
	}

}
