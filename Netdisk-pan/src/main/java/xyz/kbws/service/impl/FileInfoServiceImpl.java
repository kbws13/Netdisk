package xyz.kbws.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.RandomAccess;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.component.RedisComponent;
import xyz.kbws.entity.config.AppConfig;
import xyz.kbws.entity.constants.Constants;
import xyz.kbws.entity.dto.SessionWebUserDto;
import xyz.kbws.entity.dto.UploadResultDto;
import xyz.kbws.entity.dto.UserSpaceDto;
import xyz.kbws.entity.enums.*;
import xyz.kbws.entity.po.UserInfo;
import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.entity.query.UserInfoQuery;
import xyz.kbws.entity.vo.PaginationResultVO;
import xyz.kbws.entity.query.SimplePage;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mappers.FileInfoMapper;
import xyz.kbws.mappers.UserInfoMapper;
import xyz.kbws.service.FileInfoService;
import xyz.kbws.utils.DateUtil;
import xyz.kbws.utils.StringTools;


/**
 * 文件信息 业务接口实现
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {

	private static final Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private RedisComponent redisComponent;
	@Resource
	private AppConfig appConfig;
	@Resource
	@Lazy
	private FileInfoServiceImpl fileInfoService;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<FileInfo> findListByParam(FileInfoQuery param) {
		return this.fileInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(FileInfoQuery param) {
		return this.fileInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<FileInfo> list = this.findListByParam(param);
		PaginationResultVO<FileInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(FileInfo bean) {
		return this.fileInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<FileInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<FileInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(FileInfo bean, FileInfoQuery param) {
		StringTools.checkParam(param);
		return this.fileInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(FileInfoQuery param) {
		StringTools.checkParam(param);
		return this.fileInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据FileIdAndUserId获取对象
	 */
	@Override
	public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
		return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId修改
	 */
	@Override
	public Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId) {
		return this.fileInfoMapper.updateByFileIdAndUserId(bean, fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId删除
	 */
	@Override
	public Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId) {
		return this.fileInfoMapper.deleteByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 文件上传
	 * @param webUserDto
	 * @param fileId (非必传) 第一个分片的时候后端会反给前端fileId，下个分片上传时要携带
	 * @param file 需要上传的文件
	 * @param fileName 文件名
	 * @param filePid 父级目录
	 * @param fileMd5 切片后的文件
	 * @param chunkIndex 当前传输的第几个分片
	 * @param chunks 分片的总数量
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file,
									  String fileName, String filePid, String fileMd5, Integer chunkIndex,
									  Integer chunks) {
		UploadResultDto resultDto = new UploadResultDto();
		Boolean uploadSuccess = true;
		File tempFileFolder = null;
		try {
			if (StringTools.isEmpty(fileId)){
				fileId = StringTools.getRandomString(Constants.LENGTH_10);
			}
			resultDto.setFileId(fileId);
			Date curDate = new Date();
			UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());

			if (chunkIndex == 0) {
				FileInfoQuery infoQuery = new FileInfoQuery();
				infoQuery.setFileMd5(fileMd5);
				infoQuery.setSimplePage(new SimplePage(0, 1));
				infoQuery.setStatus(FileStatusEnum.USING.getStatus());
				List<FileInfo> dbFileList = this.fileInfoMapper.selectList(infoQuery);
				//秒传
				if (!dbFileList.isEmpty()) {
					FileInfo dbFile = dbFileList.get(0);
					//判断文件大小
					if (dbFile.getFileSize() + spaceDto.getUserSpace() > spaceDto.getTotalSpace()) {
						throw new BusinessException(ResponseCodeEnum.CODE_904);
					}
					dbFile.setFileId(fileId);
					dbFile.setFilePid(filePid);
					dbFile.setUserId(webUserDto.getUserId());
					dbFile.setCreateTime(curDate);
					dbFile.setLastUpdateTime(curDate);
					dbFile.setStatus(FileStatusEnum.USING.getStatus());
					dbFile.setDelFlag(FileDelFlagEnum.USING.getFlag());
					dbFile.setFileMd5(fileMd5);
					//文件重命名
					fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
					dbFile.setFileName(fileName);
					this.fileInfoMapper.insert(dbFile);
					resultDto.setStatus(UploadStatusEnum.UPLOAD_SECONDS.getCode());

					//更新用户使用空间
					updateUserSpace(webUserDto, dbFile.getFileSize());
					return resultDto;
				}
			}
			//判断磁盘空间
			Long currentTempSize = redisComponent.getFileTempSize(webUserDto.getUserId(),  fileId);
			if (file.getSize() + currentTempSize + spaceDto.getUserSpace() > spaceDto.getTotalSpace()){
				throw new BusinessException(ResponseCodeEnum.CODE_904);
			}

			//暂存临时目录
			String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
			String currentUserFolderName = webUserDto.getUserId() + fileId;

			tempFileFolder = new File(tempFolderName + currentUserFolderName);
			if (!tempFileFolder.exists()){
				tempFileFolder.mkdirs();
			}

			File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
			file.transferTo(newFile);
			if (chunkIndex<chunks-1){
				resultDto.setStatus(UploadStatusEnum.UPLOADING.getCode());
				//保存临时大小
				redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
				return resultDto;
			}

			//最后一个文件上传完成，记录数据，异步合并分片
			String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
			String fileSuffix = StringTools.getFileSuffix(fileName);
			//真实文件名
			String realFileName = currentUserFolderName + fileSuffix;
			FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
			//自动重命名
			fileName = autoRename(filePid, webUserDto.getUserId(), fileName);

			FileInfo fileInfo = new FileInfo();
			fileInfo.setFileId(fileId);
			fileInfo.setUserId(webUserDto.getUserId());
			fileInfo.setFileMd5(fileMd5);
			fileInfo.setFileName(fileName);
			fileInfo.setFilePath(month + "/" + realFileName);
			fileInfo.setFilePid(filePid);
			fileInfo.setCreateTime(curDate);
			fileInfo.setLastUpdateTime(curDate);
			fileInfo.setFileCateory(fileTypeEnum.getCategory().getCategory());
			fileInfo.setFileType(fileTypeEnum.getType());
			fileInfo.setStatus(FileStatusEnum.TRANSFER.getStatus());
			fileInfo.setFolderType(FileFolderTypeEnum.FILE.getType());
			fileInfo.setDelFlag(FileDelFlagEnum.USING.getFlag());
			this.fileInfoMapper.insert(fileInfo);

			Long totalSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
			updateUserSpace(webUserDto, totalSize);

			resultDto.setStatus(UploadStatusEnum.UPLOAD_FINISH.getCode());

			// 在提交事务之后调用方法
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					//直接调用方法并不是异步执行，需要托管给Spring然后调用才是执行异步方法
					fileInfoService.transferFile(fileInfo.getFileId(), webUserDto);
				}
			});

			return resultDto;
		}catch (BusinessException e){
			logger.error("文件上传失败",e);
			uploadSuccess = false;
			throw e;
		}catch (Exception e){
			logger.error("文件上传失败",e);
			uploadSuccess = false;
		}finally {
			if (!uploadSuccess && tempFileFolder!= null){
				try {
					FileUtils.deleteDirectory(tempFileFolder);
				} catch (IOException e) {
					logger.error("删除临时目录失败",e);
				}
			}
		}
		return resultDto;
	}

	/**
	 * 自动重命名文件
	 * @param filePid 文件id
	 * @param userId 用户id
	 * @param fileName 文件名
	 * @return 重命名后的文件名
	 */
	private String autoRename(String filePid, String userId, String fileName){
		FileInfoQuery fileInfoQuery = new FileInfoQuery();
		fileInfoQuery.setUserId(userId);
		fileInfoQuery.setFileId(filePid);
		fileInfoQuery.setDelFlag(FileDelFlagEnum.USING.getFlag());
		fileInfoQuery.setFileName(fileName);
		Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
		if (count > 0){
			fileName = StringTools.rename(fileName);
		}
		return fileName;
	}

	/**
	 * 更新用户使用空间
	 */
	private void updateUserSpace(SessionWebUserDto webUserDto, Long useSpace){
		Integer count = userInfoMapper.updateUserSpace(webUserDto.getUserId(), useSpace, null);
		if (count == 0){
			throw new BusinessException(ResponseCodeEnum.CODE_904);
		}
		UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
		spaceDto.setUserSpace(spaceDto.getUserSpace()+useSpace);
		redisComponent.saveUserSpaceUse(webUserDto.getUserId(), spaceDto);
	}

	/**
	 * 异步合并文件
	 * @param fileId
	 * @param webUserDto
	 */
	@Async
	public void transferFile(String fileId, SessionWebUserDto webUserDto){
		Boolean transferSuccess = true;
		String targetFilePath = null;
		String cover = null;
		FileTypeEnum fileTypeEnum = null;
		FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, webUserDto.getUserId());
		try {
			if (fileInfo == null || !FileStatusEnum.TRANSFER.getStatus().equals(fileInfo.getStatus())){
				return;
			}
			//临时目录
			String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
			String currentUserFolderName = webUserDto.getUserId() + fileId;
			File fileFolder = new File(tempFolderName + currentUserFolderName);

			String fileSuffix = StringTools.getFileSuffix(fileInfo.getFileName());
			String month = DateUtil.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYYMM.getPattern());
			//目标目录
			String targetFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
			File targetFolder = new File(targetFolderName + "/" + month);
			if (!targetFolder.exists()){
				targetFolder.mkdirs();
			}
			//真实的文件名
			String realFileName = currentUserFolderName + fileSuffix;
			targetFilePath = targetFolder.getPath() + "/" + realFileName;
			//合并文件
			union(fileFolder.getPath(), targetFilePath,fileInfo.getFileName(),true);
		}catch (Exception e){

		}
	}

	private void union(String dirPath, String toFilePath, String fileName, Boolean delSource){
		File dir = new File(dirPath);
		if (!dir.exists()){
			throw new BusinessException("目录不存在");
		}

		File[] fileList = dir.listFiles();
		File targetFile = new File(toFilePath);
		RandomAccessFile writeFile = null;
		try {
			writeFile = new RandomAccessFile(targetFile, "rv");
			byte[] b = new byte[1024 * 10];
			for (int i = 0; i < fileList.length; i++) {
				int len = -1;
				File chunkFile = new File(dirPath + "/" + i);
				RandomAccessFile readFile = null;
				try {
					readFile = new RandomAccessFile(chunkFile, "r");
					while ((len = readFile.read(b)) != -1){
						writeFile.write(b,0,len);
					}
				}catch (Exception e){
					logger.error("合并分片失败",e);
					throw new BusinessException("合并分片失败");
				}finally {
					readFile.close();
				}
			}
		}catch (Exception e){
			logger.error("合并文件:{}失败",fileName);
			throw new BusinessException("合并文件"+fileName+"出错了");
		}finally {
			if (writeFile != null){
				try {
					writeFile.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
			if (delSource && dir.exists()){
				try {
					FileUtils.deleteDirectory(dir);
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}