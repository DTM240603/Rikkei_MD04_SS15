package ra.edu.hw.md04.ss15.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ra.edu.hw.md04.ss15.dto.CreateCourseRequest;
import ra.edu.hw.md04.ss15.model.Course;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final AtomicLong nextId = new AtomicLong(1);
    private final List<Course> courses = new CopyOnWriteArrayList<>(
            List.of(
                    new Course(nextId.getAndIncrement(), "Java Fundamentals", "Core Java programming"),
                    new Course(nextId.getAndIncrement(), "Spring Boot", "Building services with Spring Boot")
            )
    );

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")
    public List<Course> getCourses() {
        return List.copyOf(courses);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Course createCourse(@jakarta.validation.Valid @RequestBody CreateCourseRequest request) {
        Course course = new Course(nextId.getAndIncrement(), request.name(), request.description());
        courses.add(course);
        return course;
    }
}
