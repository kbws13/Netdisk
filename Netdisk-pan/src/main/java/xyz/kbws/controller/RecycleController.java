package xyz.kbws.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.GlobalInterceptor;
import xyz.kbws.annotation.VerifyParam;
import xyz.kbws.entity.dto.SessionWebUserDto;
import xyz.kbws.entity.enums.FileDelFlagEnum;
import xyz.kbws.entity.query.FileInfoQuery;
import xyz.kbws.entity.vo.FileInfoVO;
import xyz.kbws.entity.vo.PaginationResultVO;
import xyz.kbws.entity.vo.ResponseVO;
import xyz.kbws.service.FileInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ABaseController{
    @Resource
    private FileInfoService fileInfoService;


    @RequestMapping("/loadRecycleList")
    @GlobalInterceptor
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize){
        FileInfoQuery query = new FileInfoQuery();
        query.setPageSize(pageSize);
        query.setPageNo(pageNo);
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("recovery_time desc");
        query.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        PaginationResultVO result = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    @RequestMapping("/recoverFile")
    @GlobalInterceptor
    public ResponseVO recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds){
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.recoverFileBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.delFileBatch(webUserDto.getUserId(), fileIds,false);
        return getSuccessResponseVO(null);
    }
}
