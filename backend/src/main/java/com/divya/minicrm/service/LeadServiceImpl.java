package com.divya.minicrm.service;

import com.divya.minicrm.dto.LeadNoteRequestDTO;
import com.divya.minicrm.dto.LeadNoteResponseDTO;
import com.divya.minicrm.dto.LeadRequestDTO;
import com.divya.minicrm.dto.LeadResponseDTO;
import com.divya.minicrm.entity.Lead;
import com.divya.minicrm.entity.LeadNote;
import com.divya.minicrm.enums.LeadStatus;
import com.divya.minicrm.exception.ResourceNotFoundException;
import com.divya.minicrm.repository.LeadNoteRepository;
import com.divya.minicrm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadNoteRepository leadNoteRepository;

    @Override
    @Transactional
    public LeadResponseDTO createLead(LeadRequestDTO dto) {
        Lead lead = Lead.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .source(dto.getSource())
                .status(dto.getStatus() != null ? dto.getStatus() : LeadStatus.NEW)
                .build();

        Lead saved = leadRepository.save(lead);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadResponseDTO> getAllLeads(LeadStatus statusFilter, String search) {
        List<Lead> leads;

        if (search != null && !search.isBlank()) {
            leads = leadRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search);
        } else if (statusFilter != null) {
            leads = leadRepository.findByStatus(statusFilter);
        } else {
            leads = leadRepository.findAll();
        }

        return leads.stream()
                .sorted(Comparator.comparing(Lead::getCreatedAt).reversed())
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponseDTO getLeadById(Long id) {
        Lead lead = findLeadOrThrow(id);
        return toResponseDTO(lead);
    }

    @Override
    @Transactional
    public LeadResponseDTO updateLead(Long id, LeadRequestDTO dto) {
        Lead lead = findLeadOrThrow(id);

        lead.setName(dto.getName());
        lead.setEmail(dto.getEmail());
        lead.setPhone(dto.getPhone());
        lead.setSource(dto.getSource());
        if (dto.getStatus() != null) {
            lead.setStatus(dto.getStatus());
        }

        Lead saved = leadRepository.save(lead);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteLead(Long id) {
        Lead lead = findLeadOrThrow(id);
        leadRepository.delete(lead);
    }

    @Override
    @Transactional
    public LeadNoteResponseDTO addNote(Long leadId, LeadNoteRequestDTO dto) {
        Lead lead = findLeadOrThrow(leadId);

        LeadNote note = LeadNote.builder()
                .lead(lead)
                .content(dto.getContent())
                .build();

        LeadNote saved = leadNoteRepository.save(note);
        return toNoteResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadNoteResponseDTO> getNotes(Long leadId) {
        findLeadOrThrow(leadId); // ensures the lead exists, 404s otherwise
        return leadNoteRepository.findByLeadIdOrderByCreatedAtDesc(leadId)
                .stream()
                .map(this::toNoteResponseDTO)
                .collect(Collectors.toList());
    }

    // ---- helpers ----

    private Lead findLeadOrThrow(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
    }

    private LeadResponseDTO toResponseDTO(Lead lead) {
        List<LeadNoteResponseDTO> notes = lead.getNotes() == null ? List.of() :
                lead.getNotes().stream()
                        .sorted(Comparator.comparing(LeadNote::getCreatedAt).reversed())
                        .map(this::toNoteResponseDTO)
                        .collect(Collectors.toList());

        return LeadResponseDTO.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .source(lead.getSource())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .notes(notes)
                .build();
    }

    private LeadNoteResponseDTO toNoteResponseDTO(LeadNote note) {
        return LeadNoteResponseDTO.builder()
                .id(note.getId())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
