package xyz.kbws.component;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import org.springframework.stereotype.Component;
import xyz.kbws.entity.constants.Constants;
import xyz.kbws.entity.dto.DownloadFileDto;
import xyz.kbws.entity.dto.SysSettingsDto;
import xyz.kbws.entity.dto.UserSpaceDto;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.mappers.FileInfoMapper;

import javax.annotation.Resource;

@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    public SysSettingsDto getSysSettingDto(){
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null){
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto){
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE+userId,userSpaceDto,Constants.REDIS_KEY_EXPIRES_DAY);
    }

    public UserSpaceDto getUserSpaceUse(String userId){
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE+userId);
        if (spaceDto == null){
            spaceDto = new UserSpaceDto();
            Long useSpace = fileInfoMapper.selectUseSpace(userId);
            spaceDto.setUserSpace(useSpace);
            spaceDto.setTotalSpace(getSysSettingDto().getUserInitUseSpace() * Constants.MB);
            saveUserSpaceUse(userId,spaceDto);
        }
        return spaceDto;
    }

    /**
     * 保存文件
     * @param userId
     * @param fileId
     * @param fileSize
     */
    public void saveFileTempSize(String userId, String fileId, Long fileSize){
        Long currentSize = getFileTempSize(userId, fileId);
        redisUtils.setex(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize,
                Constants.REDIS_KEY_EXPIRES_OEN_HOUR);
    }

    /**
     * 获取临时文件大小
     * @param userId
     * @param fileId
     * @return
     */
    public Long getFileTempSize(String userId, String fileId){
        Long currentSize = getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE+userId+fileId);
        return currentSize;
    }

    private Long getFileSizeFromRedis(String key){
        Object sizeObj = redisUtils.get(key);
        if (sizeObj == null){
            return 0L;
        }
        if (sizeObj instanceof  Integer){
            return ((Integer) sizeObj).longValue();
        }else if (sizeObj instanceof Long){
            return (Long) sizeObj;
        }
        return 0L;
    }

    public void saveDownloadCode(String code, DownloadFileDto downloadFileDto){
        redisUtils.setex(Constants.REDIS_KEY_DOWNLOAD+code,downloadFileDto, Constants.REDIS_KEY_EXPIRES_FIVE_MIN);
    }

    public DownloadFileDto getDownloadCode(String code){
        return (DownloadFileDto) redisUtils.get(Constants.REDIS_KEY_DOWNLOAD + code);
    }
}
