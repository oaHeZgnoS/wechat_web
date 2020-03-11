package com.szh.wechat.service;

import java.util.List;

import com.szh.wechat.model.User;

public interface UserService {

	public User selectByAccountAndPassword(String account, String password);

	public List<User> getAllUsers();

	public User selectByUserId(String userId);
}
