package com.exemple.backend_spring.controller;

import com.exemple.backend_spring.dto.PropositionDTO;
import com.exemple.backend_spring.service.PropositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/propositions")
public class PropositionController {


    private final PropositionService propositionService;

    @PostMapping
    public ResponseEntity<PropositionDTO> createProposition(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("title") String title,
                                                            @RequestParam("userId") Long userId) throws IOException {
        PropositionDTO propositionDTO = new PropositionDTO();
        propositionDTO.setTitle(title);
        propositionDTO.setUserId(userId);
        PropositionDTO createdProposition = propositionService.createProposition(propositionDTO, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProposition);
    }

    @GetMapping
    public ResponseEntity<List<PropositionDTO>> getAllPropositions() {
        List<PropositionDTO> propositions = propositionService.getAllPropositions();
        return ResponseEntity.ok(propositions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropositionDTO> getPropositionById(@PathVariable Long id) {
        PropositionDTO proposition = propositionService.getPropositionById(id);
        return ResponseEntity.ok(proposition);
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<PropositionDTO> updateProposition(
            @PathVariable Long id,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "title", required = false) String title,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        PropositionDTO updatedProposition = propositionService.updateProposition(id, userId, title, file);
        return ResponseEntity.ok(updatedProposition);
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<PropositionDTO> updateProposition(
            @PathVariable Long id,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "title", required = false) String title,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        PropositionDTO updatedProposition = propositionService.updateProposition(id, userId, title, file);
        return ResponseEntity.ok(updatedProposition);
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Resolve the file path relative to the upload directory
            Path filePath = Paths.get("resources/uploads").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                System.out.println("File not found: " + filePath); // Debugging log
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage()); // Debugging log
            return ResponseEntity.internalServerError().build();
        }
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposition(@PathVariable Long id) {
        propositionService.deleteProposition(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PropositionDTO>> getPropositionsByUser(@PathVariable Long userId) {
        List<PropositionDTO> propositions = propositionService.getPropositionsByUser(userId);
        return ResponseEntity.ok(propositions);
    }

    @PatchMapping("/{id}/validate")
    public ResponseEntity<PropositionDTO> validateProposition(@PathVariable Long id) {
        PropositionDTO updatedProposition = propositionService.validateProposition(id);
        return ResponseEntity.ok(updatedProposition);
    }

    @GetMapping("/validated")
    public ResponseEntity<List<PropositionDTO>> getValidatedPropositions() {
        List<PropositionDTO> validatedPropositions = propositionService.getValidatedPropositions();
        return ResponseEntity.ok(validatedPropositions);
    }




}
