package xyz.kbws.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.entity.dto.SessionWebUserDto;
import xyz.kbws.entity.dto.UploadResultDto;
import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.entity.vo.PaginationResultVO;


/**
 * 文件信息 业务接口
 */
public interface FileInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<FileInfo> findListByParam(FileInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(FileInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(FileInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<FileInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<FileInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(FileInfo bean,FileInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(FileInfoQuery param);

	/**
	 * 根据FileIdAndUserId查询对象
	 */
	FileInfo getFileInfoByFileIdAndUserId(String fileId,String userId);


	/**
	 * 根据FileIdAndUserId修改
	 */
	Integer updateFileInfoByFileIdAndUserId(FileInfo bean,String fileId,String userId);


	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteFileInfoByFileIdAndUserId(String fileId,String userId);

	/**
	 * 文件上传
	 */
	UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file,
							   String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

	/**
	 * 新建目录
	 * @param filePid 父级id
	 * @param userId 用户id
	 * @param folderName 文件夹名
	 * @return
	 */
	FileInfo newFolder(String filePid, String userId, String folderName);

	/**
	 * 重命名文件夹
	 * @param fileId 文件id
	 * @param userId 用户id
	 * @param fileName 文件夹名
	 * @return
	 */
	FileInfo rename(String fileId, String userId, String fileName);

	/**
	 * 修改文件目录、移动文件
	 * @param fileIds 需要移动的文件id列表
	 * @param filePid 父级目录id
	 * @param userId 用户id
	 */
	void changeFileFolder(String fileIds, String filePid, String userId);

	/**
	 * 移动文件到回收站
	 * @param userId 用户id
	 * @param fileIds 文件id
	 */
	void removeFile2RecycleBatch(String userId, String fileIds);

	/**
	 * 还原文件
	 * @param userId
	 * @param fileIds
	 */
	void recoverFileBatch(String userId, String fileIds);

	/**
	 * 彻底删除文件
	 * @param userId 用户id
	 * @param fileIds 文件id
	 * @param adminOp 是否是管理员
	 */
	void delFileBatch(String userId, String fileIds, Boolean adminOp);
}