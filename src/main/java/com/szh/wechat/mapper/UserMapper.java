package com.szh.wechat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.szh.wechat.model.User;

public interface UserMapper {

	public User selectByAccountAndPassword(@Param("account") String account, @Param("password") String password);

	public List<User> getAllUsers();

	public User selectByUserId(String userId);

}