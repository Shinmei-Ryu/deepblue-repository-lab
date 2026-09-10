package com.deepblue.rescue.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="rescue_cases")
public class RescueCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "case_code", unique = true, nullable = false, length = 100)
    private String caseCode;

    @Column(name = "recue_date", nullable = false)
    private LocalDate rescueDate;

    @Column(name = "rescue_location", nullable = false, length = 100)
    private String rescueLocation;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private RescueStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescue_center_id", nullable = false)
    private RescueCenter rescueCenter;

    @OneToOne(
            mappedBy = "rescueCase",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Animal animal;


    public RescueCase() {
    }

    public RescueCase(String caseCode, LocalDate rescueDate, String rescueLocation, RescueStatus status) {
        this.caseCode = caseCode;
        this.rescueDate = rescueDate;
        this.rescueLocation = rescueLocation;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public LocalDate getRescueDate() {
        return rescueDate;
    }

    public String getRescueLocation() {
        return rescueLocation;
    }

    public RescueStatus getStatus() {
        return status;
    }

    public RescueCenter getRescueCenter() {
        return rescueCenter;
    }

    public Animal getAnimal() {
        return animal;
    }
}
