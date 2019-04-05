package com.e3shop.sso.service;
/**
 * 根据token查询用户信息
 * @author Admin
 *
 */

import com.e3shop.common.utils.E3Result;

public interface TokenService {

	E3Result getUserByToken(String token);
}
