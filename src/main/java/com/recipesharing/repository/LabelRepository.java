package com.recipesharing.repository;

import com.recipesharing.entity.Ingredient;
import com.recipesharing.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByName(String normalized);

    List<Label> findAllByNameIn(Set<String> names);
}

