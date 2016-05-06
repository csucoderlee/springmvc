package org.csu.coderlee.service;

import org.csu.coderlee.bean.Course;
import org.springframework.stereotype.Service;

/**
 * Created by lixiang on 16/5/6.
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Override
    public Course getCourseById(String id) {
        Course course = new Course();
        course.setCourseId(id);
        course.setCourseName("java");
        course.setDescription("java programmer");
        return course;
    }
}
