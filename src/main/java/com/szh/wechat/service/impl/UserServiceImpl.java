package com.szh.wechat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.szh.wechat.mapper.UserMapper;
import com.szh.wechat.model.User;
import com.szh.wechat.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Override
	public User selectByAccountAndPassword(String account, String password) {
		return userMapper.selectByAccountAndPassword(account, password);
	}

	@Override
	public List<User> getAllUsers() {
		return userMapper.getAllUsers();
	}

	@Override
	public User selectByUserId(String userId) {
		return userMapper.selectByUserId(userId);
	}
}
