package com.divya.minicrm.dto;

import com.divya.minicrm.enums.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LeadRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must be under 120 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 20, message = "Phone must be under 20 characters")
    private String phone;

    @NotBlank(message = "Source is required")
    private String source;

    // Optional on create (defaults to NEW); required conceptually on status-update calls.
    private LeadStatus status;
}
