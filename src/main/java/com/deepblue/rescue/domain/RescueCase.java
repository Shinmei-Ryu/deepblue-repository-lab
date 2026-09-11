package com.deepblue.rescue.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="rescue_cases")
public class RescueCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescue_center_id", nullable = false)
    private RescueCenter rescueCenter;

    @Column(name = "case_code", unique = true, nullable = false, length = 100)
    private String caseCode;

    @Column(name = "recue_date", nullable = false)
    private LocalDate rescueDate;

    @Column(name = "rescue_location", nullable = false, length = 100)
    private String rescueLocation;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private RescueStatus status;

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

    public void setId(long id) {
        this.id = id;
    }

    public void setRescueCenter(RescueCenter rescueCenter) {
        this.rescueCenter = rescueCenter;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public void setRescueDate(LocalDate rescueDate) {
        this.rescueDate = rescueDate;
    }

    public void setRescueLocation(String rescueLocation) {
        this.rescueLocation = rescueLocation;
    }

    public void setStatus(RescueStatus status) {
        this.status = status;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}
