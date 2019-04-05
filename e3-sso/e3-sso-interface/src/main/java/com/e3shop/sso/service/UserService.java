package com.e3shop.sso.service;
/**
 * 用户接口
 * @author Admin
 *
 */

import com.e3shop.common.utils.E3Result;
import com.e3shop.pojo.TbUser;

public interface UserService {

	/**
	 * 数据监测
	 * @param param
	 * @param type
	 * @return
	 */
	E3Result checkData(String param,int type);
	/**
	 * 注册
	 * @param tbUser
	 * @return
	 */
	E3Result registUser(TbUser tbUser);
	/**
	 * 登录
	 * @return
	 */
	E3Result login(String username,String password);
}
