package com.cooksys.group_project_1_team_1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HashtagDto {
    private String label;

    private Timestamp firstUsed;

    private Timestamp lastUsed;
}
