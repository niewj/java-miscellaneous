package com.niewj.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by niewj on 2017/10/30.
 */
@Controller
@RequestMapping("/")
public class TestController {

    @RequestMapping("test")
    @ResponseBody
    public String test() {

        return "HelloNiewj";
    }
}
