package com.futurex.services.FutureXCourseApp;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private Tracer tracer;

    @RequestMapping("/")
    public String getCourseAppHome() {
        Span span = tracer.spanBuilder("getCourseAppHome").startSpan();
        try {
            return "Course App Home";
        } finally {
            span.end();
        }
    }

    @RequestMapping("/courses")
    public List<Course> getCourses() {
        Span span = tracer.spanBuilder("getCourses").startSpan();
        try {
            return courseRepository.findAll();
        } finally {
            span.end();
        }
    }

    @RequestMapping("/{id}")
    public Course getSpecificCourse(@PathVariable("id") BigInteger id) {
        return courseRepository.getOne(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/courses")
    public void saveCourse(@RequestBody Course course) {
        courseRepository.save(course);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteCourse(@PathVariable BigInteger id) {
        courseRepository.deleteById(id);
    }
}