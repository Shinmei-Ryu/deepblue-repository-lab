package com.deepblue.rescue.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false, unique = true)
    private Animal animal;

    @Column(name = "initial_weight", nullable = false)
    private BigDecimal initialWeight;

    @Column(name = "initial_condition", nullable = false, length = 100)
    private String initialCondition;

    @Column(columnDefinition = "TEXT")
    private String injuries;

    @Column(columnDefinition = "TEXT")
    private String observations;

    public MedicalRecord() {
    }

    public MedicalRecord(BigDecimal initialWeight, String initialCondition) {
        this.initialWeight = initialWeight;
        this.initialCondition = initialCondition;
    }

    public Long getId() {
        return id;
    }

    public Animal getAnimal() {
        return animal;
    }

    public BigDecimal getInitialWeight() {
        return initialWeight;
    }

    public String getInitialCondition() {
        return initialCondition;
    }

    public String getInjuries() {
        return injuries;
    }

    public String getObservations() {
        return observations;
    }
}
