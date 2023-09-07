package xyz.kbws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kbws.entity.config.AppConfig;
import xyz.kbws.entity.constants.Constants;
import xyz.kbws.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommonFileController extends ABaseController{

    private static final Logger logger= LoggerFactory.getLogger(CommonFileController.class);

    @Resource
    private AppConfig appConfig;

    public void getImage(HttpServletResponse response, String imageFolder, String imageName){
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
}
