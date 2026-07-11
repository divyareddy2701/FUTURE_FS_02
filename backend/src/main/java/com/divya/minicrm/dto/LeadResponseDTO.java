package com.divya.minicrm.dto;

import com.divya.minicrm.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String source;
    private LeadStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LeadNoteResponseDTO> notes;
}
