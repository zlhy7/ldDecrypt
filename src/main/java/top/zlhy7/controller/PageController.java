package top.zlhy7.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author renyong
 * @date 2023/9/27 下午4:01
 * @description 页面控制器
 */
@Slf4j
@Controller
@RequestMapping("page")
public class PageController {
    /**
     * 首页访问
     * @return
     */
    @GetMapping
    public String indexPage(HttpServletRequest request){
        System.out.println(request.getRequestURI());
        return "index";
    }
}
