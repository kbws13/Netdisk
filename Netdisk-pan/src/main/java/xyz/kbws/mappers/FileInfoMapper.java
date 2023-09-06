package xyz.kbws.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 文件信息 数据库操作接口
 */
public interface FileInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据FileIdAndUserId更新
	 */
	 Integer updateByFileIdAndUserId(@Param("bean") T t,@Param("fileId") String fileId,@Param("userId") String userId);


	/**
	 * 根据FileIdAndUserId删除
	 */
	 Integer deleteByFileIdAndUserId(@Param("fileId") String fileId,@Param("userId") String userId);


	/**
	 * 根据FileIdAndUserId获取对象
	 */
	 T selectByFileIdAndUserId(@Param("fileId") String fileId,@Param("userId") String userId);

	/**
	 * 根据id查询用户使用空间
	 */
	Long selectUseSpace(@Param("userId") String userId);

	/**
	 * 乐观锁
	 * @param fileId
	 * @param userId
	 * @param t
	 * @param oldStatus
	 */
	void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId,
									   @Param("bean") T t, @Param("oldStatus") Integer oldStatus);
}
