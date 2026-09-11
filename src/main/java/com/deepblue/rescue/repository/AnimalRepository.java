package com.deepblue.rescue.repository;

import com.deepblue.rescue.domain.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public class AnimalRepository extends JpaRepository<Animal, Long>
{
}
