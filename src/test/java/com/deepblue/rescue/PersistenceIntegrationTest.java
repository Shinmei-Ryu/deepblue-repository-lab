package com.deepblue.rescue;


import com.deepblue.rescue.domain.*;
import com.deepblue.rescue.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Transactional
public class PersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer(
                    "postgres:18-alpine")
                    .withDatabaseName("deepblue_test")
                    .withUsername("deepblue")
                    .withPassword("deepblue");

    @Autowired
    private RescueCenterRepository rescueCenterRepository;

    @Autowired
    private RescueCaseRepository rescueCaseRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private ExpertiseRepository expertiseRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    // Test 1: Métodos heredados
    @Test
    void testInheritedMethods() {
        RescueCenter center = new RescueCenter("DB-CAR", "DeepBlue Caribbean", "Santa Marta");

        // save
        RescueCenter saved = rescueCenterRepository.save(center);
        assertThat(saved.getId()).isNotNull();

        // findById
        var retrieved = rescueCenterRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getCode()).isEqualTo("DB-CAR");

        // existsById
        boolean exists = rescueCenterRepository.existsById(saved.getId());
        assertThat(exists).isTrue();

        // count
        long count = rescueCenterRepository.count();
        assertThat(count).isGreaterThan(0);
    }

    // Test 2: Relación 1:N
    @Test
    void testOneToManyRelationship() {
        RescueCenter center = new RescueCenter("DB-TEST", "Test Center", "Test City");

        RescueCase case1 = new RescueCase("RES-001", LocalDate.now(), "Location 1", RescueStatus.ADMITTED);
        RescueCase case2 = new RescueCase("RES-002", LocalDate.now(), "Location 2", RescueStatus.ADMITTED);

        center.addCase(case1);
        center.addCase(case2);

        rescueCenterRepository.save(center);

        RescueCenter retrieved = rescueCenterRepository.findById(center.getId()).get();
        assertThat(retrieved.getCases()).hasSize(2);
    }

    // Test 3: Relación 1:1 RescueCase-Animal
    @Test
    void testOneToOneRescueCaseAnimal() {
        RescueCenter center = new RescueCenter("DB-ANIM", "Animal Center", "Test");
        RescueCase rescueCase = new RescueCase("RES-2026-001", LocalDate.of(2026, 8, 18),
                "Bahía Concha", RescueStatus.IN_REHABILITATION);
        center.addCase(rescueCase);

        Animal animal = new Animal("AN-2026-001", "Green Sea Turtle", "Chelonia mydas", AnimalSex.FEMALE);
        rescueCase.assignAnimal(animal);

        rescueCenterRepository.save(center);

        RescueCase retrieved = rescueCaseRepository.findById(rescueCase.getId()).get();
        assertThat(retrieved.getAnimal()).isNotNull();
        assertThat(retrieved.getAnimal().getCommonName()).isEqualTo("Green Sea Turtle");

        Animal retrievedAnimal = animalRepository.findById(animal.getId()).get();
        assertThat(retrievedAnimal.getRescueCase()).isNotNull();
        assertThat(retrievedAnimal.getRescueCase().getCaseCode()).isEqualTo("RES-2026-001");
    }

    // Test 4: Relación 1:1 Animal-MedicalRecord
    @Test
    void testOneToOneAnimalMedicalRecord() {
        RescueCenter center = new RescueCenter("DB-MED", "Medical Center", "Test");
        RescueCase rescueCase = new RescueCase("RES-MED-001", LocalDate.now(), "Location", RescueStatus.ADMITTED);
        center.addCase(rescueCase);

        Animal animal = new Animal("AN-MED-001", "Dolphin", "Tursiops", AnimalSex.UNKNOWN);
        rescueCase.assignAnimal(animal);

        MedicalRecord record = new MedicalRecord(new BigDecimal("28.40"), "STABLE");
        record.setInjuries("Left front flipper injury");
        animal.assignMedicalRecord(record);

        rescueCenterRepository.save(center);

        Animal retrieved = animalRepository.findById(animal.getId()).get();
        assertThat(retrieved.getMedicalRecord()).isNotNull();
        assertThat(retrieved.getMedicalRecord().getInitialWeight()).isEqualByComparingTo(new BigDecimal("28.40"));
    }

    // Test 5: Relación N:M
    @Test
    void testManyToManyRelationship() {
        Expertise trauma = expertiseRepository.findByNameIgnoreCase("Trauma").get();
        Expertise rehab = expertiseRepository.findByNameIgnoreCase("Rehabilitation").get();

        Specialist elena = new Specialist("SPEC-001", "Elena", "Vargas", "elena@deepblue.org", true);
        elena.addExpertise(trauma);
        elena.addExpertise(rehab);

        specialistRepository.save(elena);

        Specialist retrieved = specialistRepository.findById(elena.getId()).get();
        assertThat(retrieved.getExpertiseAreas()).hasSize(2);
    }

    // Test 6: Query Method simple
    @Test
    void findByStatusShouldReturnMatchingRescueCases() {
        RescueCenter center = new RescueCenter("DB-QM1", "DeepBlue QM Center", "Santa Marta");
        rescueCenterRepository.save(center);

        RescueCase res1 = new RescueCase("RES-001", LocalDate.of(2026, 4, 1), "Zona 1", RescueStatus.IN_REHABILITATION);
        RescueCase res2 = new RescueCase("RES-002", LocalDate.of(2026, 4, 2), "Zona 2", RescueStatus.READY_FOR_RELEASE);
        RescueCase res3 = new RescueCase("RES-003", LocalDate.of(2026, 4, 3), "Zona 3", RescueStatus.IN_REHABILITATION);
        center.addCase(res1);
        center.addCase(res2);
        center.addCase(res3);
        rescueCaseRepository.saveAll(List.of(res1, res2, res3));

        List<RescueCase> inRehabilitation =
                rescueCaseRepository.findByStatusOrderByRescueDateAsc(RescueStatus.IN_REHABILITATION);

        assertThat(inRehabilitation).hasSize(2);
        assertThat(inRehabilitation)
                .extracting(RescueCase::getCaseCode)
                .containsExactly("RES-001", "RES-003");
    }

    // Test 7: Query Method navegando relaciones
    @Test
    void findByRescueCaseRescueCenterCodeShouldOnlyReturnAnimalsFromThatCenter() {
        RescueCenter caribbean = new RescueCenter("DB-CAR", "DeepBlue Caribbean", "Santa Marta");
        RescueCenter pacific = new RescueCenter("DB-PAC", "DeepBlue Pacific", "Buenaventura");
        rescueCenterRepository.saveAll(List.of(caribbean, pacific));

        RescueCase caseCar = new RescueCase("RES-CAR-01", LocalDate.of(2026, 5, 1),
                "Bahía Concha", RescueStatus.ADMITTED);
        caribbean.addCase(caseCar);
        Animal animalCar = new Animal("AN-CAR-01", "Green Sea Turtle", "Chelonia mydas", AnimalSex.FEMALE);
        caseCar.assignAnimal(animalCar);
        rescueCaseRepository.save(caseCar);

        RescueCase casePac = new RescueCase("RES-PAC-01", LocalDate.of(2026, 5, 2),
                "Bahía Málaga", RescueStatus.ADMITTED);
        pacific.addCase(casePac);
        Animal animalPac = new Animal("AN-PAC-01", "Olive Ridley Turtle", "Lepidochelys olivacea", AnimalSex.MALE);
        casePac.assignAnimal(animalPac);
        rescueCaseRepository.save(casePac);

        List<Animal> animalsFromCaribbean = animalRepository.findByRescueCaseRescueCenterCode("DB-CAR");

        assertThat(animalsFromCaribbean)
                .extracting(Animal::getAnimalCode)
                .containsExactly("AN-CAR-01")
                .doesNotContain("AN-PAC-01");
    }

    // Test 8: @Query JPQL especialistas
    @Test
    void findActiveByExpertiseShouldReturnOnlySpecialistsWithTrauma() {
        Expertise trauma = expertiseRepository.findByNameIgnoreCase("Trauma").orElseThrow();
        Expertise rehabilitation = expertiseRepository.findByNameIgnoreCase("Rehabilitation").orElseThrow();
        Expertise marineMammals = expertiseRepository.findByNameIgnoreCase("Marine Mammals").orElseThrow();
        Expertise marineBirds = expertiseRepository.findByNameIgnoreCase("Marine Birds").orElseThrow();

        Specialist elena = new Specialist("SPEC-910", "Elena", "Vargas", "elena.910@deepblue.org",true);
        elena.addExpertise(trauma);
        elena.addExpertise(rehabilitation);

        Specialist mateo = new Specialist("SPEC-911", "Mateo", "Restrepo", "mateo.911@deepblue.org",true);
        mateo.addExpertise(marineMammals);
        mateo.addExpertise(rehabilitation);

        Specialist sofia = new Specialist("SPEC-912", "Sofia", "Londono", "sofia.912@deepblue.org",true);
        sofia.addExpertise(marineBirds);
        sofia.addExpertise(trauma);

        specialistRepository.saveAll(List.of(elena, mateo, sofia));

        List<Specialist> traumaSpecialists = specialistRepository.findActiveByExpertise("trauma");

        assertThat(traumaSpecialists)
                .extracting(Specialist::getFirstName)
                .containsExactly("Elena", "Sofia");
    }

    // Test 9: insercion de tratamientos y jpql para intervalos
    @Test
    void treatmentsForAnimalShouldBeOrderedChronologically() {
        RescueCenter center = new RescueCenter("DB-TRT", "DeepBlue Treatment Center", "Santa Marta");
        rescueCenterRepository.save(center);

        RescueCase rescueCase = new RescueCase("RES-TRT-01", LocalDate.of(2026, 6, 1),
                "Zona X", RescueStatus.IN_REHABILITATION);
        center.addCase(rescueCase);
        Animal animal = new Animal("AN-TRT-01", "Green Sea Turtle", "Chelonia mydas", AnimalSex.FEMALE);
        rescueCase.assignAnimal(animal);
        rescueCaseRepository.save(rescueCase);

        Specialist elena = new Specialist("SPEC-920", "Elena", "Vargas", "elena.920@deepblue.org",true);
        Specialist mateo = new Specialist("SPEC-921", "Mateo", "Restrepo", "mateo.921@deepblue.org",true);
        specialistRepository.saveAll(List.of(elena, mateo));

        Treatment treatment1 = new Treatment(animal, elena,
                LocalDateTime.of(2026, 6, 2, 9, 0), TreatmentType.WOUND_CARE, "Treatment 1");
        Treatment treatment2 = new Treatment(animal, elena,
                LocalDateTime.of(2026, 6, 3, 9, 0), TreatmentType.HYDRATION, "Treatment 2");
        Treatment treatment3 = new Treatment(animal, mateo,
                LocalDateTime.of(2026, 6, 4, 9, 0), TreatmentType.OBSERVATION, "Treatment 3");
        treatmentRepository.saveAll(List.of(treatment1, treatment2, treatment3));

        List<Treatment> treatments = treatmentRepository.findByAnimalIdOrderByPerformedAtAsc(animal.getId());

        assertThat(treatments)
                .extracting(Treatment::getDescription)
                .containsExactly("Treatment 1", "Treatment 2", "Treatment 3");
    }
}
