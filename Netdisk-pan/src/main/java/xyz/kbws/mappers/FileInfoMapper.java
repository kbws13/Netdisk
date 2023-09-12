package xyz.kbws.mappers;

import org.apache.ibatis.annotations.Param;
import xyz.kbws.entity.po.FileInfo;

import java.util.List;

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

	/**
	 * 批量回收站
	 */
	void updateFileDelFlagBatch(@Param("bean")FileInfo fileInfo, @Param("userId") String userId,
								@Param("filePidList")List<String> filePidList,
								@Param("fileIdList") List<String> fileIdList,
								@Param("oldDelFlag") Integer oldDelFlag);

	/**
	 * 批量删除
	 */
	void delFileBatch(@Param("userId") String userId,
					  @Param("filePidList") List<String> filePidList,
					  @Param("fileIdList") List<String> fileIdList,
					  @Param("oldDelFlag") Integer oldDelFlag);
}
