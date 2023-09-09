package xyz.kbws.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.annotation.GlobalInterceptor;
import xyz.kbws.annotation.VerifyParam;
import xyz.kbws.entity.dto.SessionWebUserDto;
import xyz.kbws.entity.dto.UploadResultDto;
import xyz.kbws.entity.enums.FileCategoryEnum;
import xyz.kbws.entity.enums.FileDelFlagEnum;
import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.entity.vo.FileInfoVO;
import xyz.kbws.entity.vo.PaginationResultVO;
import xyz.kbws.entity.vo.ResponseVO;
import xyz.kbws.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 文件信息 Controller
 */
@RestController("fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends CommonFileController{

	@Resource
	private FileInfoService fileInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category){
		FileCategoryEnum categoryEnum = FileCategoryEnum.getByCode(category);
		if (categoryEnum != null){
			query.setFileCategory(categoryEnum.getCategory());
		}
		query.setUserId(getUserInfoFromSession(session).getUserId());
		query.setOrderBy("last_update_time desc");
		query.setDelFlag(FileDelFlagEnum.USING.getFlag());
		PaginationResultVO result = fileInfoService.findListByPage(query);
		return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
	}

	/**
	 * 文件上传
	 * @param session
	 * @param fileId (非必传) 第一个分片的时候后端会反给前端fileId，下个分片上传时要携带
	 * @param file 需要上传的文件
	 * @param fileName 文件名
	 * @param filePid 父级目录
	 * @param fileMd5 切片后的文件
	 * @param chunkIndex 当前传输的第几个分片
	 * @param chunks 分片的总数量
	 * @return
	 */
	@RequestMapping("/uploadFile")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO uploadFile(HttpSession session,
								 String fileId,
								 MultipartFile file,
								 @VerifyParam(required = true) String fileName,
								 @VerifyParam(required = true) String filePid,
								 @VerifyParam(required = true) String fileMd5,
								 @VerifyParam(required = true) Integer chunkIndex,
								 @VerifyParam(required = true) Integer chunks){
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		UploadResultDto resultDto = fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
		return getSuccessResponseVO(resultDto);
	}

	/**
	 * 获取图片
	 * @param response
	 * @param imageFolder 图片文件夹
	 * @param imageName 图片名字
	 * @return
	 */
	@RequestMapping("/getImage/{imageFolder}/{imageName}")
	@GlobalInterceptor(checkParams = true)
	public void getPic(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
		super.getImage(response, imageFolder, imageName);
	}

	@RequestMapping("/ts/getVideoInfo/{fileId}")
	@GlobalInterceptor(checkParams = true)
	public void getVideoInfo(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, webUserDto.getUserId());
	}

	@RequestMapping("/getFile/{fileId}")
	@GlobalInterceptor(checkParams = true)
	public void getFile(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, webUserDto.getUserId());
	}

	@RequestMapping("/newFoloder")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO newFolder(HttpSession session,
								@VerifyParam(required = true) String filePid,
								@VerifyParam(required = true) String fileName){
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		FileInfo fileInfo = fileInfoService.newFolder(filePid, webUserDto.getUserId(), fileName);
		return getSuccessResponseVO(fileInfo);
	}

	@RequestMapping("/getFolderInfo")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO getFolderInfo(HttpSession session,
									@VerifyParam(required = true) String path){
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		return super.getFolderInfo(path, webUserDto.getUserId());
	}

	@RequestMapping("/rename")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO rename(HttpSession session,
							 @VerifyParam(required = true) String fileId,
							 @VerifyParam(required = true) String fileName){
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		FileInfo rename = fileInfoService.rename(fileId, webUserDto.getUserId(), fileName);
		return getSuccessResponseVO(rename);
	}
}