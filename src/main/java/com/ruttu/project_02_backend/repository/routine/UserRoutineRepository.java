package com.ruttu.project_02_backend.repository.routine;

import com.ruttu.project_02_backend.entity.routine.UserRoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoutineRepository extends JpaRepository<UserRoutineEntity, Long> {
}
