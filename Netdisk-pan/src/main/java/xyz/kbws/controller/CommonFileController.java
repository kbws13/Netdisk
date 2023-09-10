package xyz.kbws.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kbws.component.RedisComponent;
import xyz.kbws.entity.config.AppConfig;
import xyz.kbws.entity.constants.Constants;
import xyz.kbws.entity.dto.DownloadFileDto;
import xyz.kbws.entity.enums.FileCategoryEnum;
import xyz.kbws.entity.enums.FileFolderTypeEnum;
import xyz.kbws.entity.enums.ResponseCodeEnum;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.entity.vo.ResponseVO;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.service.FileInfoService;
import xyz.kbws.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

public class CommonFileController extends ABaseController{

    private static final Logger logger= LoggerFactory.getLogger(CommonFileController.class);

    @Resource
    private AppConfig appConfig;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private RedisComponent redisComponent;

    public void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringTools.isEmpty(imageFolder) || StringUtils.isBlank(imageName)) {
            return;
        }
        String imageSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }

    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;
        if (fileId.endsWith(".ts")) {
            String[] tsAarray = fileId.split("_");
            String realFileId = tsAarray[0];
            //根据原文件的id查询出一个文件集合
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (fileInfo == null) {
                //分享的视频，ts路径记录的是原视频的id,这里通过id直接取出原视频
                FileInfoQuery fileInfoQuery = new FileInfoQuery();
                fileInfoQuery.setFileId(realFileId);
                List<FileInfo> fileInfoList = fileInfoService.findListByParam(fileInfoQuery);
                fileInfo = fileInfoList.get(0);
                if (fileInfo == null) {
                    return;
                }

                //更具当前用户id和路径去查询当前用户是否有该文件，如果没有直接返回
                fileInfoQuery = new FileInfoQuery();
                fileInfoQuery.setFilePath(fileInfo.getFilePath());
                fileInfoQuery.setUserId(userId);
                Integer count = fileInfoService.findCountByParam(fileInfoQuery);
                if (count == 0) {
                    return;
                }
            }
            String fileName = fileInfo.getFilePath();
            fileName = StringTools.getFileNameNoSuffix(fileName) + "/" + fileId;
            filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileName;
        } else {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (fileInfo == null) {
                return;
            }
            //视频文件读取.m3u8文件
            if (FileCategoryEnum.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                //重新设置文件路径
                String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Constants.M3U8_NAME;
            } else {
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        readFile(response, filePath);
    }

    protected ResponseVO getFolderInfo(String path, String userId){
        String[] pathArray = path.split("/");
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFolderType(FileFolderTypeEnum.FOLDER.getType());
        fileInfoQuery.setFileIdArray(pathArray);
        String orderBy = "field(file_id,\"" + StringUtils.join(pathArray,"\",\"") +"\")";
        fileInfoQuery.setOrderBy(orderBy);
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(fileInfoQuery);
        return getSuccessResponseVO(fileInfoList);
    }

    protected ResponseVO createDownloadUrl(String fileId, String userId){
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
        if (fileInfo == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (FileFolderTypeEnum.FOLDER.getType().equals(fileInfo.getFolderType())){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String code = StringTools.getRandomString(Constants.LENGTH_50);

        DownloadFileDto fileDto = new DownloadFileDto();
        fileDto.setDownloadCode(code);
        fileDto.setFileName(fileInfo.getFileName());
        fileDto.setFilePath(fileInfo.getFilePath());
        redisComponent.saveDownloadCode(code, fileDto);

        return getSuccessResponseVO(code);
    }

    protected void download(HttpServletRequest request, HttpServletResponse response, String code)throws Exception{
        DownloadFileDto downloadFileDto = redisComponent.getDownloadCode(code);
        if (downloadFileDto == null){
            return;
        }
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + downloadFileDto.getFilePath();
        String fileName = downloadFileDto.getFileName();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0){
            //IE浏览器
            fileName = URLEncoder.encode(fileName, "UTF-8");
        }else {
            fileName = new String(fileName.getBytes("UTF-8"),"ISO8859-1");
        }
        response.setHeader("Content-Disposition","attachment;filename=\"" + fileName + "\"");
        readFile(response,filePath);
    }

}
