package xyz.kbws.controller;

import java.util.List;

import xyz.kbws.entity.query.EmailCodeQuery;
import xyz.kbws.entity.po.EmailCode;
import xyz.kbws.entity.vo.ResponseVO;
import xyz.kbws.service.EmailCodeService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 邮箱验证码 Controller
 */
@RestController("emailCodeController")
@RequestMapping("/emailCode")
public class EmailCodeController extends ABaseController{

	@Resource
	private EmailCodeService emailCodeService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(EmailCodeQuery query){
		return getSuccessResponseVO(emailCodeService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(EmailCode bean) {
		emailCodeService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<EmailCode> listBean) {
		emailCodeService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<EmailCode> listBean) {
		emailCodeService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据EmailAndCode查询对象
	 */
	@RequestMapping("/getEmailCodeByEmailAndCode")
	public ResponseVO getEmailCodeByEmailAndCode(String email,String code) {
		return getSuccessResponseVO(emailCodeService.getEmailCodeByEmailAndCode(email,code));
	}

	/**
	 * 根据EmailAndCode修改对象
	 */
	@RequestMapping("/updateEmailCodeByEmailAndCode")
	public ResponseVO updateEmailCodeByEmailAndCode(EmailCode bean,String email,String code) {
		emailCodeService.updateEmailCodeByEmailAndCode(bean,email,code);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据EmailAndCode删除
	 */
	@RequestMapping("/deleteEmailCodeByEmailAndCode")
	public ResponseVO deleteEmailCodeByEmailAndCode(String email,String code) {
		emailCodeService.deleteEmailCodeByEmailAndCode(email,code);
		return getSuccessResponseVO(null);
	}
}