package com.momoclass.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description
 * @date 2024/03/22 11:16
 */
@Controller
public class FreeMarkerController {
    @GetMapping("/testfreemarker")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "小明");
        modelAndView.setViewName("test");
        return modelAndView;
    }
}
