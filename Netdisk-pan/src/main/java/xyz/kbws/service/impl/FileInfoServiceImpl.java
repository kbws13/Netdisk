package xyz.kbws.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.component.RedisComponent;
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
import xyz.kbws.utils.StringTools;


/**
 * 文件信息 业务接口实现
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {

	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private RedisComponent redisComponent;

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
									  String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
		UploadResultDto resultDto = new UploadResultDto();
		if (StringTools.isEmpty(fileId)){
			fileId = StringTools.getRandomNumber(Constants.LENGTH_10);
		}
		resultDto.setFileId(fileId);
		Date curDate = new Date();
		UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());

		if (chunkIndex == 0){
			FileInfoQuery infoQuery = new FileInfoQuery();
			infoQuery.setFileMd5(fileMd5);
			infoQuery.setSimplePage(new SimplePage(0,1));
			infoQuery.setStatus(FileStatusEnum.USING.getStatus());
			List<FileInfo> dbFileList = this.fileInfoMapper.selectList(infoQuery);
			//秒传
			if (!dbFileList.isEmpty()){
				FileInfo dbFile = dbFileList.get(0);
				//判断文件大小
				if (dbFile.getFileSize()+spaceDto.getUserSpace() > spaceDto.getTotalSpace()){
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

		return null;
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
}