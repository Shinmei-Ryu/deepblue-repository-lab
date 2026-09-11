ALTER TABLE animals ADD COLUMN tracking_device_code VARCHAR(50);

ALTER TABLE animals
    ADD CONSTRAINT UQ_animals_tracking_device_code UNIQUE (tracking_device_code);