package com.divya.minicrm.service;

import com.divya.minicrm.dto.LeadNoteRequestDTO;
import com.divya.minicrm.dto.LeadNoteResponseDTO;
import com.divya.minicrm.dto.LeadRequestDTO;
import com.divya.minicrm.dto.LeadResponseDTO;
import com.divya.minicrm.enums.LeadStatus;

import java.util.List;

public interface LeadService {

    LeadResponseDTO createLead(LeadRequestDTO dto);

    List<LeadResponseDTO> getAllLeads(LeadStatus statusFilter, String search);

    LeadResponseDTO getLeadById(Long id);

    LeadResponseDTO updateLead(Long id, LeadRequestDTO dto);

    void deleteLead(Long id);

    LeadNoteResponseDTO addNote(Long leadId, LeadNoteRequestDTO dto);

    List<LeadNoteResponseDTO> getNotes(Long leadId);
}
