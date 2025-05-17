package com.futurex.services.FutureXCourseApp;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseRepository courseRepository;
    private final Tracer tracer;
    private final MeterRegistry meterRegistry;

    public CourseController(CourseRepository courseRepository,
                            Tracer tracer,
                            MeterRegistry meterRegistry) {
        this.courseRepository = courseRepository;
        this.tracer = tracer;
        this.meterRegistry = meterRegistry;
    }

    /** Home */
    @GetMapping("/")
    public String getCourseAppHome() {
        logger.info("Received request for course app home");
        Span span = tracer.spanBuilder("getCourseAppHome").startSpan();
        try {
            return "Course App Home";
        } finally {
            span.end();
        }
    }

    /** List all courses */
    @GetMapping("/courses")
    public List<Course> getCourses() {
        logger.info("Fetching all courses");
        Span span = tracer.spanBuilder("getCourses").startSpan();
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            List<Course> courses = courseRepository.findAll();
            meterRegistry.counter("courses.accessed").increment();
            logger.info("Fetched {} courses", courses.size());
            return courses;
        } finally {
            span.end();
            timer.stop(meterRegistry.timer("courses.fetch.all"));
        }
    }

    /** Get a single course */
    @GetMapping("/{id}")
    public Course getSpecificCourse(@PathVariable BigInteger id) {
        logger.info("Fetching course {}", id);
        Span span = tracer.spanBuilder("getSpecificCourse").startSpan();
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Course course = courseRepository.getOne(id);
            meterRegistry.counter("courses.accessed.specific").increment();
            return course;
        } finally {
            span.end();
            timer.stop(meterRegistry.timer("courses.fetch.single"));
        }
    }

    /** Create / update a course */
    @PostMapping("/courses")
    public void saveCourse(@RequestBody Course course) {
        logger.info("Saving course {}", course.getCoursename());
        Span span = tracer.spanBuilder("saveCourse").startSpan();
        try {
            courseRepository.save(course);
            meterRegistry.counter("courses.saved").increment();
        } finally {
            span.end();
        }
    }

    /** Delete a course */
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable BigInteger id) {
        logger.info("Deleting course {}", id);
        Span span = tracer.spanBuilder("deleteCourse").startSpan();
        try {
            courseRepository.deleteById(id);
            meterRegistry.counter("courses.deleted").increment();
        } finally {
            span.end();
        }
    }
}