package xyz.kbws.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.kbws.entity.enums.FileDelFlagEnum;
import xyz.kbws.entity.po.FileInfo;
import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.service.FileInfoService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileCleanTask {
    @Resource
    private FileInfoService fileInfoService;

    @Scheduled(fixedDelay = 1000 * 60 * 3)
    public void execute(){
        FileInfoQuery query = new FileInfoQuery();
        query.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        query.setQueryExpire(true);
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(query);
        Map<String, List<FileInfo>> filenfoMap = fileInfoList.stream().collect(Collectors.groupingBy(FileInfo::getUserId));
        for (Map.Entry<String, List<FileInfo>> entry : filenfoMap.entrySet()){
            List<String> fileIds = entry.getValue().stream().map(p -> p.getFileId()).collect(Collectors.toList());
            fileInfoService.delFileBatch(entry.getKey(), String.join(",", fileIds), false);
        }
    }
}
