package com.divya.minicrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LeadNoteRequestDTO {

    @NotBlank(message = "Note content cannot be empty")
    @Size(max = 1000, message = "Note must be under 1000 characters")
    private String content;
}
