package com.deepblue.rescue.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name="rescue_centers")
public class RescueCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false,unique = true,length = 100)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String city;

    @OneToMany(mappedBy = "rescueCenter")
    private List<RescueCase> cases = new ArrayList<>();

    public RescueCenter() {
    }

    public RescueCenter(String code, String name, String city) {
        this.code = code;
        this.name = name;
        this.city = city;
    }

    public void addCase(RescueCase rescueCase) {
        this.cases.add(rescueCase);
        rescueCase.setRescueCenter(this);
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public List<RescueCase> getCases() {
        return cases;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCases(List<RescueCase> cases) {
        this.cases = cases;
    }
}
