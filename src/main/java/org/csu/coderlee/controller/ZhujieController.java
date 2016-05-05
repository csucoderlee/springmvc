package org.csu.coderlee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by lixiang on 16/5/5.
 */

@Controller
//@RequestMapping("a"), 注解也可以用来注释一个controller, localhost:9955/spring-mvc/a/Zhujie, 将URI映射到类级别的方法上
public class ZhujieController {

    //RequestMapping 将URI映射到方法上, localhost:9955/spring-mvc/Zhujie就可以调用到改方法
    //@RequestMapping("/Zhujie")
    @RequestMapping(value="/Zhujie", method={RequestMethod.GET,RequestMethod.POST})
    public String toB(){
        return "a";
    }
}
