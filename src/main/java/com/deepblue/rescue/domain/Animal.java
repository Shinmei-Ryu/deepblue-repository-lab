package com.deepblue.rescue.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "animals")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "animal_code", unique = true, nullable = false, length = 100)
    private String animalCode;

    @Column(name = "common_name", nullable = false, length = 100)
    private String commonName;

    @Column(name = "scientific_name", nullable = false, length = 100)
    private String scientificName;

    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private AnimalSex sex;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescue_case_id", unique = true, nullable = false)
    private RescueCase rescueCase;

    @OneToOne(mappedBy = "animal",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;

    @OneToMany(mappedBy = "animal")
    private List<Treatment> treatments;


    public Animal() {
    }

    public Animal(String animalCode, String commonName, String scientificName, AnimalSex sex) {
        this.animalCode = animalCode;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.sex = sex;
    }

    public void assignMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
        medicalRecord.setAnimal(this);
    }

    public Long getId() {
        return id;
    }

    public String getAnimalCode() {
        return animalCode;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public AnimalSex getSex() {
        return sex;
    }

    public RescueCase getRescueCase() {
        return rescueCase;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAnimalCode(String animalCode) {
        this.animalCode = animalCode;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public void setSex(AnimalSex sex) {
        this.sex = sex;
    }

    public void setRescueCase(RescueCase rescueCase) {
        this.rescueCase = rescueCase;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }
}
