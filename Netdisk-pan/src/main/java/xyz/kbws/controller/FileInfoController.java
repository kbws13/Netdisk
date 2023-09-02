package xyz.kbws.controller;

import java.util.List;

import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.entity.vo.ResponseVO;
import xyz.kbws.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文件信息 Controller
 */
@RestController("fileInfoController")
@RequestMapping("/fileInfo")
public class FileInfoController extends ABaseController{

	@Resource
	private FileInfoService fileInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(FileInfoQuery query){
		return getSuccessResponseVO(fileInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(FileInfo bean) {
		fileInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<FileInfo> listBean) {
		fileInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<FileInfo> listBean) {
		fileInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileIdAndUserId查询对象
	 */
	@RequestMapping("/getFileInfoByFileIdAndUserId")
	public ResponseVO getFileInfoByFileIdAndUserId(String fileId,String userId) {
		return getSuccessResponseVO(fileInfoService.getFileInfoByFileIdAndUserId(fileId,userId));
	}

	/**
	 * 根据FileIdAndUserId修改对象
	 */
	@RequestMapping("/updateFileInfoByFileIdAndUserId")
	public ResponseVO updateFileInfoByFileIdAndUserId(FileInfo bean,String fileId,String userId) {
		fileInfoService.updateFileInfoByFileIdAndUserId(bean,fileId,userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileIdAndUserId删除
	 */
	@RequestMapping("/deleteFileInfoByFileIdAndUserId")
	public ResponseVO deleteFileInfoByFileIdAndUserId(String fileId,String userId) {
		fileInfoService.deleteFileInfoByFileIdAndUserId(fileId,userId);
		return getSuccessResponseVO(null);
	}
}