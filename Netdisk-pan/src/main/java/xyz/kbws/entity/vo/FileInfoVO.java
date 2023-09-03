package xyz.kbws.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 文件信息
 */
@Data
public class FileInfoVO{

    private String fileId;

    private String filedPid;

    private Long fileSize;

    private String fileName;

    private String fileCover;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;
    /**
     * 0:文件 1:目录
     */
    private Integer folderType;

    private Integer fileType;

    private Integer status;
}
