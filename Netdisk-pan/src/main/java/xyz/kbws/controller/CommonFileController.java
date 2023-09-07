package xyz.kbws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kbws.entity.config.AppConfig;
import xyz.kbws.entity.constants.Constants;
import xyz.kbws.entity.enums.FileCategoryEnum;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.service.FileInfoService;
import xyz.kbws.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class CommonFileController extends ABaseController{

    private static final Logger logger= LoggerFactory.getLogger(CommonFileController.class);

    @Resource
    private AppConfig appConfig;

    @Resource
    private FileInfoService fileInfoService;

    public void getPic(HttpServletResponse response, String imageFolder, String imageName){
        if (StringTools.isEmpty(imageFolder) || StringTools.isEmpty(imageName) || !StringTools.pathIsOk(imageFolder)
                                    || !StringTools.pathIsOk(imageName)){
            return;
        }
        logger.error("获取缩略图");
        String imageSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".","");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control","max-age=2592000");
        readFile(response, filePath);
    }

    protected void getFile(HttpServletResponse response, String fieldId, String userId){
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fieldId, userId);
        String filePath = null;
        if (fieldId == null){
            return;
        }
        if (FileCategoryEnum.VIDEO.getCategory().equals(fileInfo.getFileCateory())){
            String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
            filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE +
                    fileNameNoSuffix + "/" + Constants.M3U8_NAME;
        }
        File file = new File(filePath);
        if (!file.exists()){
            return;
        }
        readFile(response, filePath);
    }
}
