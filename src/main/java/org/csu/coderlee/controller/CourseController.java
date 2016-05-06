package org.csu.coderlee.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.csu.coderlee.bean.Course;
import org.csu.coderlee.service.CourseService;
import org.csu.coderlee.service.CourseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lixiang on 16/5/6.
 */
@Controller
@RequestMapping("/course")
public class CourseController{

    private static final Log logger = LogFactory.getLog(InputProductController.class);

    private CourseService courseService;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @RequestMapping("/getCourseById")
    public String getCourseById(Model model){
        Course course = courseService.getCourseById("123");
        model.addAttribute("course",course);
        logger.info(course.getCourseName());
        return "a";
    }
}
