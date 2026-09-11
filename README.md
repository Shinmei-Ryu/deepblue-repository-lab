# DeepBlue Rescue - Persistencia con Spring Boot

Plataforma para organizaciones dedicadas al rescate y rehabilitación de fauna marina.

## Descripción del Proyecto

DeepBlue Rescue es un sistema de persistencia desarrollado con Java 21, Spring Boot 4.1.1, Spring Data JPA, Hibernate, Flyway y PostgreSQL.

El proyecto implementa la capa de persistencia para gestionar:

- Centros de rescate
- Casos de rescate
- Animales rescatados
- Expedientes médicos
- Especialistas y sus áreas de experiencia
- Tratamientos realizados

## Modelo de Datos

### Entidades Principales

```
RescueCenter  1 ───────── N  RescueCase
RescueCase    1 ───────── 1  Animal
Animal        1 ───────── 1  MedicalRecord
Animal        1 ───────── N  Treatment
Specialist    1 ───────── N  Treatment
Specialist    N ───────── M  Expertise
```

### Tablas

| Tabla | Propósito |
|---|---|
| `rescue_centers` | Centros de rescate |
| `rescue_cases` | Casos de rescate |
| `animals` | Animales rescatados |
| `medical_records` | Expedientes médicos |
| `specialists` | Especialistas en rescate |
| `expertise` | Áreas de especialidad |
| `specialist_expertise` | Relación N:M especialistas-expertise |
| `treatments` | Tratamientos realizados |

## Relaciones

**1:N - RescueCenter → RescueCase**
- Un centro puede tener múltiples casos
- Foreign key: `rescue_center_id`

**1:1 - RescueCase → Animal**
- Un caso tiene exactamente un animal
- Foreign key: `rescue_case_id` (UNIQUE)

**1:1 - Animal → MedicalRecord**
- Un animal tiene exactamente un expediente
- Foreign key: `animal_id` (UNIQUE)
- Cascade `ALL` + `orphanRemoval` desde `Animal`

**1:N - Animal → Treatment**
- Un animal puede recibir múltiples tratamientos
- Foreign key: `animal_id`

**1:N - Specialist → Treatment**
- Un especialista puede realizar múltiples tratamientos
- Foreign key: `specialist_id`

**N:M - Specialist ↔ Expertise**
- Tabla asociativa: `specialist_expertise`
- Un especialista puede tener múltiples áreas de experiencia
- Un área puede tener múltiples especialistas

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- Flyway (migraciones)
- Testcontainers (testing)
- PostgreSQL
- Maven

## Estructura del Proyecto

```
deepblue-rescue/
├── pom.xml
├── README.md
├── src/main/java/com/deepblue/rescue/
│   ├── DeepblueRescueApplication.java
│   ├── domain/
│   │   ├── Animal.java
│   │   ├── AnimalSex.java
│   │   ├── Expertise.java
│   │   ├── MedicalRecord.java
│   │   ├── RescueCase.java
│   │   ├── RescueCenter.java
│   │   ├── RescueStatus.java
│   │   ├── Specialist.java
│   │   ├── Treatment.java
│   │   └── TreatmentType.java
│   └── repository/
│       ├── AnimalRepository.java
│       ├── ExpertiseRepository.java
│       ├── MedicalRecordRepository.java
│       ├── RescueCaseRepository.java
│       ├── RescueCenterRepository.java
│       ├── SpecialistRepository.java
│       └── TreatmentRepository.java
├── src/main/resources/
│   ├── application.yaml
│   └── db/migration/
│       ├── V1__create_schema.sql
│       ├── V2__insert_expertise_catalog.sql
│       └── V3__add_tracking_device_to_animal.sql
└── src/test/java/com/deepblue/rescue/
    ├── DeepblueRescueApplicationTests.java
    ├── TestDeepblueRescueApplication.java
    └── TestcontainersConfiguration.java
```

## Configuración

`application.yaml` solo define el nombre de la aplicación; la conexión a la base de datos se resuelve vía variables de entorno o Testcontainers:

```yaml
spring:
  application:
    name: deepblue-rescue
```

Para correr la app fuera de tests, se recomienda añadir:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/deepblue}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
  flyway:
    enabled: true
    locations: classpath:db/migration
```

## Migraciones Flyway

**V1__create_schema.sql** — Crea todas las tablas con primary keys, foreign keys, unique constraints, check constraints (estados válidos) e índices.

**V2__insert_expertise_catalog.sql** — Inserta el catálogo de expertise: Marine Reptiles, Marine Mammals, Marine Birds, Trauma, Rehabilitation, Toxicology.

**V3__add_tracking_device_to_animal.sql** — Evolución del esquema: agrega la columna `tracking_device_code` (nullable, UNIQUE) mediante `ALTER TABLE`. Demuestra que el esquema evoluciona con nuevas migraciones y no modificando V1.

Flyway ejecuta los scripts en orden de versión al arrancar el contexto y registra el historial en `flyway_schema_history`, garantizando que el esquema esté siempre sincronizado con el código.

## Testcontainers

Los tests corren contra una instancia **real** de PostgreSQL en Docker, no contra H2 ni mocks:

- `TestcontainersConfiguration` define un bean `PostgreSQLContainer` con `@ServiceConnection`, que Spring Boot usa para inyectar la conexión automáticamente.
- `DeepblueRescueApplicationTests` importa esa configuración (`@Import` + `@SpringBootTest`), levantando el contexto completo con Flyway aplicando las migraciones antes de cada ejecución.
- `TestDeepblueRescueApplication` permite arrancar la app completa en modo desarrollo apoyada en el mismo contenedor, sin configurar PostgreSQL manualmente.

## Query Methods Implementados

**RescueCenterRepository**
```java
Optional<RescueCenter> findByCode(String code);
```

**RescueCaseRepository**
```java
Optional<RescueCase> findByCaseCode(String caseCode);
List<RescueCase> findByStatusOrderByRescueDateAsc(RescueStatus status);
List<RescueCase> findByRescueCenterCode(String centerCode);
List<RescueCase> findByRescueDateAfterOrderByRescueDateDesc(LocalDate rescueDate);
boolean existsByCaseCode(String caseCode);
```

**AnimalRepository**
```java
Optional<Animal> findByAnimalCode(String animalCode);
List<Animal> findByCommonNameIgnoreCase(String commonName);
List<Animal> findByCommonNameContainingIgnoreCase(String text);
List<Animal> findByRescueCaseStatus(RescueStatus status);
List<Animal> findByRescueCaseRescueCenterCode(String centerCode);
```

**ExpertiseRepository**
```java
Optional<Expertise> findByNameIgnoreCase(String name);
```

**TreatmentRepository**
```java
List<Treatment> findByAnimalIdOrderByPerformedAtAsc(Long animalId);
```

`MedicalRecordRepository` y `SpecialistRepository` no definen Query Methods propios más allá de los heredados de `JpaRepository`.

## Consultas JPQL

**Especialistas activos por experiencia**
```java
@Query("""
    select distinct s
    from Specialist s
    join s.expertiseAreas e
    where s.active = true
    and lower(e.name) = lower(:expertiseName)
    order by s.lastName, s.firstName
    """)
List<Specialist> findActiveByExpertise(String expertiseName);
```
Uso: encontrar especialistas activos con una experiencia específica.

**Especialistas por experiencia (activos o no)**
```java
@Query("""
    select distinct s
    from Specialist s
    join s.expertiseAreas e
    where lower(e.name) = lower(:expertiseName)
    order by s.lastName, s.firstName
    """)
List<Specialist> findByExpertise(String expertiseName);
```
Uso: variante sin filtrar por especialistas activos.

**Tratamientos en intervalo de fechas**
```java
@Query("""
    select t
    from Treatment t
    where t.performedAt >= :start
    and t.performedAt <= :end
    order by t.performedAt asc
    """)
List<Treatment> findTreatmentsBetweenDates(LocalDateTime start, LocalDateTime end);
```
Uso: auditoría de tratamientos en períodos específicos.

**Tratamientos por centro de rescate**
```java
@Query("""
    select t
    from Treatment t
    where t.animal.rescueCase.rescueCenter.code = :centerCode
    order by t.performedAt asc
    """)
List<Treatment> findTreatmentsByRescueCenter(String centerCode);
```
Uso: navega `Treatment → Animal → RescueCase → RescueCenter` para listar tratamientos de un centro.

**Tratamientos por experiencia del especialista**
```java
@Query("""
    select distinct t
    from Treatment t
    join t.specialist s
    join s.expertiseAreas e
    where lower(e.name) = lower(:expertiseName)
    order by t.performedAt asc
    """)
List<Treatment> findTreatmentsBySpecialistExpertise(String expertiseName);
```
Uso: análisis de tratamientos aplicados por especialistas de un área específica. `findBySpecialistExpertise` es una variante equivalente con el mismo JPQL.

## Ejecución

### Requisitos previos
- Java 21 JDK
- Docker (para Testcontainers / PostgreSQL)
- Maven Wrapper incluido (`./mvnw`)

### Construir el proyecto
```bash
./mvnw clean install
```

### Ejecutar tests
```bash
./mvnw test
```
Testcontainers levanta un contenedor PostgreSQL efímero automáticamente; no se necesita base de datos configurada manualmente.

### Ejecutar con PostgreSQL local
```bash
# 1. Levantar PostgreSQL
docker run --name deepblue-postgres -e POSTGRES_DB=deepblue \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:latest

# 2. Configurar variables de entorno
export DB_URL=jdbc:postgresql://localhost:5432/deepblue
export DB_USER=postgres
export DB_PASSWORD=postgres

# 3. Ejecutar la aplicación
./mvnw spring-boot:run
```

### Verificar migraciones
Al iniciar, Flyway registra en los logs la ejecución de cada script:
```
Executing migration: V1__create_schema
Executing migration: V2__insert_expertise_catalog
Executing migration: V3__add_tracking_device_to_animal
```

## Convenciones del Proyecto

- Sin Lombok (getters/setters generados manualmente)
- `BigDecimal` para pesos
- `LocalDate` para fechas, `LocalDateTime` para timestamps
- `Enum` para estados y tipos (`RescueStatus`, `AnimalSex`, `TreatmentType`)
- Nombres en inglés (convención Spring)
- Métodos helper (`addCase`, `assignAnimal`, `assignMedicalRecord`, `addExpertise`) mantienen la bidireccionalidad de las relaciones
