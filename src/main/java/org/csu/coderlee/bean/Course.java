package org.csu.coderlee.bean;

/**
 * Created by lixiang on 16/5/6.
 */
public class Course {

    private String CourseId;
    private String CourseName;
    private String description;

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String courseId) {
        CourseId = courseId;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
