package org.csu.coderlee.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created by lixiang on 16/4/8.
 */
public class InputProductController implements Controller {

    private static final Log logger = LogFactory.getLog(InputProductController.class);

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        logger.info("inputProductController called");

        //只需要提供要返回的视图的名字,前缀和后缀由视图解析器去解析,详细的配置信息在spring-servlet.xml文件中
        return new ModelAndView("a");
    }
}
