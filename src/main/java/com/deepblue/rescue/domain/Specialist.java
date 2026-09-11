package com.deepblue.rescue.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "specialists")
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "professional_code", unique = true, nullable = false, length = 100)
    private String professionalCode;

    @Column(name = "first_name", nullable = false, length = 150)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "specialist_expertise",
            joinColumns = @JoinColumn(name = "specialist_id"),
            inverseJoinColumns = @JoinColumn(name = "expertise_id")
    )
    private Set<Expertise> expertiseAreas = new HashSet<>();

    @OneToMany(mappedBy = "specialist")
    private List<Treatment> treatments;

    public Specialist() {
    }

    public Specialist(String professionalCode, String firstName, String lastName, String email, Boolean active) {
        this.professionalCode = professionalCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
    }

    public void addExpertise(Expertise expertise) {
        this.expertiseAreas.add(expertise);
        expertise.getSpecialists().add(this);
    }

    public Long getId() {
        return id;
    }

    public String getProfessionalCode() {
        return professionalCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getActive() {
        return active;
    }

    public Set<Expertise> getExpertiseAreas() {
        return expertiseAreas;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProfessionalCode(String professionalCode) {
        this.professionalCode = professionalCode;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setExpertiseAreas(Set<Expertise> expertiseAreas) {
        this.expertiseAreas = expertiseAreas;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }
}
