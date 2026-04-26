package com.utils;

import org.apache.commons.lang3.StringUtils;

import com.dto.LoginRequest;

/**
 * 合并 query 与 JSON 登录参数；JSON 中非空字段优先生效。
 */
public final class LoginRequestUtil {

	private LoginRequestUtil() {
	}

	public static String[] merge(String username, String password, String captcha, LoginRequest body) {
		if (body != null) {
			if (StringUtils.isNotBlank(body.getUsername())) {
				username = body.getUsername();
			}
			if (body.getPassword() != null) {
				password = body.getPassword();
			}
			if (body.getCaptcha() != null) {
				captcha = body.getCaptcha();
			}
		}
		if (username != null) {
			username = username.trim();
		}
		if (password != null) {
			password = password.trim();
		}
		if (captcha != null) {
			captcha = captcha.trim();
		}
		return new String[] { username, password, captcha };
	}
}
