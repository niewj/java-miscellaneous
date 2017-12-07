package com.niewj.demo.service.impl;

import com.niewj.demo.dao.UserMapper;
import com.niewj.demo.model.User;
import com.niewj.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public List<User> selectUser(String nameLike) {
		String userName = "'" + nameLike + "%'";

		return userMapper.selectUserByName(userName);
	}

}