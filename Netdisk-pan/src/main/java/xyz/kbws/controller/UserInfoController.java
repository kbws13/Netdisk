package xyz.kbws.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.annotation.GlobalInterceptor;
import xyz.kbws.annotation.VerifyParam;
import xyz.kbws.component.RedisComponent;
import xyz.kbws.entity.config.AppConfig;
import xyz.kbws.entity.constants.Constants;
import xyz.kbws.entity.dto.CreateImageCode;
import xyz.kbws.entity.dto.SessionWebUserDto;
import xyz.kbws.entity.dto.UserSpaceDto;
import xyz.kbws.entity.enums.VerifyRegexEnum;
import xyz.kbws.entity.query.UserInfoQuery;
import xyz.kbws.entity.po.UserInfo;
import xyz.kbws.entity.vo.ResponseVO;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.service.EmailCodeService;
import xyz.kbws.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户信息 Controller
 */
@RestController("userInfoController")
public class UserInfoController extends ABaseController{

	private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

	private static final String CONTENT_TYPES = "Content-Type";
	private static final String CONTENT_TYPES_VALUE = "application/json;charset=UTF-8";

	@Resource
	private UserInfoService userInfoService;
	@Resource
	private EmailCodeService emailCodeService;
	@Resource
	private AppConfig appConfig;
	@Resource
	private RedisComponent redisComponent;

	@RequestMapping("/checkCode")
	public void checkCode(HttpServletResponse response, HttpSession session, Integer type)throws IOException {
		CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires",0);
		response.setContentType("image/jpeg");
		String code = vCode.getCode();
		if(type == null || type == 0){
			session.setAttribute(Constants.CHECK_CODE_KEY, code);
		}else {
			session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
		}
		vCode.write(response.getOutputStream());
	}

	@RequestMapping("/sendEmailCode")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO sendEmailCode(HttpSession session,
									@VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
									@VerifyParam(required = true) String checkCode,
									@VerifyParam(required = true) Integer type){
		try {
			if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))){
				throw new BusinessException("图片验证码不正确");
			}
			emailCodeService.sendEmailCode(email, type);
			return getSuccessResponseVO(null);
		}finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
		}
	}

	@RequestMapping("/register")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO register(HttpSession session,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
							   @VerifyParam(required = true) String nickName,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
							   @VerifyParam(required = true) String checkCode,
							   @VerifyParam(required = true) String emailCode){
		try {
			if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
				throw new BusinessException("图片验证码不正确");
			}
			userInfoService.register(email,nickName,password,emailCode);
			return getSuccessResponseVO(null);
		}finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}
	}

	@RequestMapping("/login")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO login(HttpSession session,
							   @VerifyParam(required = true) String email,
							   @VerifyParam(required = true) String password,
							   @VerifyParam(required = true) String checkCode){
		try {
			if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
				throw new BusinessException("图片验证码不正确");
			}
			SessionWebUserDto login = userInfoService.login(email, password);
			session.setAttribute(Constants.SESSION_KEY, login);
			return getSuccessResponseVO(login);
		}finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}
	}

	@RequestMapping("/resetPwd")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO resetPwd(HttpSession session,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
							   @VerifyParam(required = true) String checkCode,
							   @VerifyParam(required = true) String emailCode){
		try {
			if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
				throw new BusinessException("图片验证码不正确");
			}
			userInfoService.resetPwd(email,password,emailCode);
			return getSuccessResponseVO(null);
		}finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}
	}

	@RequestMapping("/getAvatar/{userId}")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public void getAvatar(HttpServletResponse response,
								@VerifyParam(required = true) @PathVariable("userId") String userId) {
		String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
		File folder = new File(appConfig.getProjectFolder()+avatarFolderName);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
		File file = new File(avatarPath);
		if (!file.exists()) {
			if (!new File(appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT).exists()) {
				printNoDefaultImage(response);
			}
			avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT;
		}
		response.setContentType("image/jpg");
		readFile(response, avatarPath);
	}

	private void printNoDefaultImage(HttpServletResponse response){
		response.setHeader(CONTENT_TYPES,CONTENT_TYPES_VALUE);
		response.setStatus(HttpStatus.OK.value());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print("请在头像目录下防止默认头像default_avatar.jpg");
			writer.close();
		}catch (Exception e){
			logger.error("输出默认图失败",e);
		}finally {
			writer.close();
		}
	}

	@RequestMapping("/getUserInfo")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO getUserInfo(HttpSession session){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		return getSuccessResponseVO(sessionWebUserDto);
	}

	@RequestMapping("/getUseSpace")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO getUserSpace(HttpSession session){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId());
		return getSuccessResponseVO(spaceDto);
	}

	@RequestMapping("/logout")
	public ResponseVO logout(HttpSession session){
		session.invalidate();
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/updateUserAvatar")
	@GlobalInterceptor
	public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar){
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
		File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
		File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + Constants.AVATAR_SUFFIX);
		if (!targetFileFolder.exists()){
			targetFileFolder.mkdirs();
		}
		try {
			avatar.transferTo(targetFile);
		}catch (Exception e){
			logger.error("上传头像失败");
		}

		UserInfo userInfo = new UserInfo();
		userInfo.setQqAvatar("");
		userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
		webUserDto.setAvatar(null);
		session.setAttribute(Constants.SESSION_KEY, webUserDto);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/updatePassword")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO updatePassword(HttpSession session,
									@VerifyParam(required=true, regex = VerifyRegexEnum.PASSWORD, min = 0, max = 10)String password){
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		UserInfo userInfo = new UserInfo();
		userInfo.setPassword(StringTools.encodingByMd5(password));
		userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/qqlogin")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO qqLogin(HttpSession session, String callbackUrl) throws UnsupportedEncodingException {
		String state = StringTools.getRandomNumber(Constants.LENGTH_30);
		if (!StringTools.isEmpty(callbackUrl)){
			session.setAttribute(state, callbackUrl);
		}
		String url = String.format(appConfig.getQqUrlAuthorization(), appConfig.getQqAppId(),
				URLEncoder.encode(appConfig.getQqUrlRedirect(), "utf-8"), state);
		return getSuccessResponseVO(url);
	}

	@RequestMapping("/qqlogin/callback")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO qqLoginCallback(HttpSession session, @VerifyParam(required = true) String code,
									  @VerifyParam(required = true) String state){
		SessionWebUserDto sessionWebUserDto = userInfoService.qqLogin(code);
		session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
		Map<String, Object> result = new HashMap<>();
		result.put("callbackUrl", session.getAttribute(state));
		result.put("userInfo", sessionWebUserDto);
		return getSuccessResponseVO(result);
	}
}