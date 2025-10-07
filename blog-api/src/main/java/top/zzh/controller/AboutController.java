package top.zzh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zzh.annotation.VisitLogger;
import top.zzh.enums.VisitBehavior;
import top.zzh.model.vo.Result;
import top.zzh.service.AboutService;

/**
 * @Description: 关于我页面
 * @author Z
 * @Date: 2020-08-31
 */
@RestController
public class AboutController {
	@Autowired
	AboutService aboutService;

	/**
	 * 获取关于我页面信息
	 *
	 * @return
	 */
	@VisitLogger(VisitBehavior.ABOUT)
	@GetMapping("/about")
	public Result about() {
		return Result.ok("获取成功", aboutService.getAboutInfo());
	}
}
