package com.clinica.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "system_configurations",
       uniqueConstraints = @UniqueConstraint(name = "uk_config_key", columnNames = "config_key"))
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class SystemConfiguration extends BaseEntity {

    @NotBlank
    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean editable;

    @Column(name = "data_type", length = 30)
    private String dataType;
}
