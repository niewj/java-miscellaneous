package com.niewj.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.niewj.demo.model.User;

public interface UserMapper {
	/**
	 * 根据用户名查询
	 * @param userName
	 * @return
	 */
	List<User> selectUserByName(@Param("name") String userName);

}