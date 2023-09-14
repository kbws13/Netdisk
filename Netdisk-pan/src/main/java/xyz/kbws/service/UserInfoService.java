package xyz.kbws.service;

import java.util.List;

import xyz.kbws.entity.dto.SessionWebUserDto;
import xyz.kbws.entity.query.UserInfoQuery;
import xyz.kbws.entity.po.UserInfo;
import xyz.kbws.entity.vo.PaginationResultVO;


/**
 * 用户信息 业务接口
 */
public interface UserInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserInfo bean,UserInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserInfoQuery param);

	/**
	 * 根据UserId查询对象
	 */
	UserInfo getUserInfoByUserId(String userId);


	/**
	 * 根据UserId修改
	 */
	Integer updateUserInfoByUserId(UserInfo bean,String userId);


	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userId);


	/**
	 * 根据Email查询对象
	 */
	UserInfo getUserInfoByEmail(String email);


	/**
	 * 根据Email修改
	 */
	Integer updateUserInfoByEmail(UserInfo bean,String email);


	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoByEmail(String email);


	/**
	 * 根据NickName查询对象
	 */
	UserInfo getUserInfoByNickName(String nickName);


	/**
	 * 根据NickName修改
	 */
	Integer updateUserInfoByNickName(UserInfo bean,String nickName);


	/**
	 * 根据NickName删除
	 */
	Integer deleteUserInfoByNickName(String nickName);


	/**
	 * 根据QqOpenId查询对象
	 */
	UserInfo getUserInfoByQqOpenId(String qqOpenId);


	/**
	 * 根据QqOpenId修改
	 */
	Integer updateUserInfoByQqOpenId(UserInfo bean,String qqOpenId);


	/**
	 * 根据QqOpenId删除
	 */
	Integer deleteUserInfoByQqOpenId(String qqOpenId);

	/**
	 * 用户注册
	 */
	void register(String email, String nickName, String password, String mailCode);

	/**
	 * 登录
	 */
	SessionWebUserDto login(String email, String password);

	/**
	 * 重置密码
	 */
	void resetPwd(String email, String password, String emailCode);
	/**
	 * QQ登录
	 */
	SessionWebUserDto qqLogin(String code);

	/**
	 * 更改用户状态
	 */
	void updateUserStatus(String userId, Integer status);

	/**
	 * 更改用户使用空间
	 */
	void changeUserSpace(String userId, Integer changeSpace);
}