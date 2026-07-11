package com.divya.minicrm.controller;

import com.divya.minicrm.dto.LeadNoteRequestDTO;
import com.divya.minicrm.dto.LeadNoteResponseDTO;
import com.divya.minicrm.dto.LeadRequestDTO;
import com.divya.minicrm.dto.LeadResponseDTO;
import com.divya.minicrm.enums.LeadStatus;
import com.divya.minicrm.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // tighten this to your actual frontend origin before going to production
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    public ResponseEntity<LeadResponseDTO> createLead(@Valid @RequestBody LeadRequestDTO dto) {
        LeadResponseDTO created = leadService.createLead(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> getAllLeads(
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(leadService.getAllLeads(status, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> updateLead(@PathVariable Long id, @Valid @RequestBody LeadRequestDTO dto) {
        return ResponseEntity.ok(leadService.updateLead(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<LeadNoteResponseDTO> addNote(@PathVariable Long id, @Valid @RequestBody LeadNoteRequestDTO dto) {
        LeadNoteResponseDTO created = leadService.addNote(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<LeadNoteResponseDTO>> getNotes(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getNotes(id));
    }
}
