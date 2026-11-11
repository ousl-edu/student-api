package com.example.studentapi.controller;

import com.example.studentapi.model.Student;
import com.example.studentapi.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentRepository repo;

    @InjectMocks
    private StudentController controller;

    private final StudentRequest req = new StudentRequest("Nimal", "nimal@ousl.lk", "Cloud Computing");

    @Test
    void allReturnsList() {
        when(repo.findAll()).thenReturn(List.of(new Student()));
        assertEquals(1, controller.all().size());
    }

    @Test
    void oneFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Student()));
        assertEquals(200, controller.one(1L).getStatusCode().value());
    }

    @Test
    void oneNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertEquals(404, controller.one(1L).getStatusCode().value());
    }

    @Test
    void createSavesStudent() {
        when(repo.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        Student saved = controller.create(req);
        assertEquals("Nimal", saved.getName());
        verify(repo).save(any(Student.class));
    }

    @Test
    void updateFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Student()));
        when(repo.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        var res = controller.update(1L, req);
        assertEquals(200, res.getStatusCode().value());
        assertEquals("Cloud Computing", res.getBody().getCourse());
    }

    @Test
    void updateNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertEquals(404, controller.update(1L, req).getStatusCode().value());
    }

    @Test
    void deleteFound() {
        when(repo.existsById(1L)).thenReturn(true);
        assertEquals(204, controller.delete(1L).getStatusCode().value());
        verify(repo).deleteById(1L);
    }

    @Test
    void deleteNotFound() {
        when(repo.existsById(1L)).thenReturn(false);
        assertEquals(404, controller.delete(1L).getStatusCode().value());
        verify(repo, never()).deleteById(any());
    }
}
