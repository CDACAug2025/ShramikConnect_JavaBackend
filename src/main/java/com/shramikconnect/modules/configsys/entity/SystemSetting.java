package com.shramikconnect.modules.configsys.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "system_settings")
@Data
public class SystemSetting {
    @Id
    @Column(name = "setting_key") // "key" is a reserved word in SQL
    private String key;

    private String value;
    private String description;
    private String type; // "BOOLEAN", "NUMBER", "TEXT"
}
