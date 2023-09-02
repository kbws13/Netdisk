package xyz.kbws.entity.constants;

public class Constants {
    public static final Integer ZERO = 0;
    public static final Integer LENGTH_5 = 5;
    public static final Integer LENGTH_10 = 10;
    public static final Integer LENGTH_15 = 15;
    public static final Integer LENGTH_20 = 20;
    public static final Integer LENGTH_30 = 30;
    public static final String CHECK_CODE_KEY = "check_code_key";
    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";
    public static final String FILE_FOLDER_FILE = "/file/";
    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";
    public static final String AVATAR_SUFFIX = ".jpg";
    public static final String AVATAR_DEFAULT = "default_avatar.jpg";
    public static final Long MB = 1024*1024L;
    public static final String REDIS_KEY_SYS_SETTING = "netdisk:syssetting:";
    public static final String REDIS_KEY_USER_SPACE_USE = "netdisk:user:spaceuse:";
    public static final String SESSION_KEY = "session_key";
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;
    public static final String  VIEW_OBJ_RESULT_KEY = "result";
}
