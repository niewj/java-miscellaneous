package com.niewj.demo.service;

import com.niewj.demo.model.User;

import java.util.List;

public interface IUserService {

	/**
	 * 模糊查询
	 * @param nameLike
	 * @return
	 */
	List<User> selectUser(String nameLike);
}