package com.example.Vox.Viridis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Vox.Viridis.model.Analysis;
import com.example.Vox.Viridis.service.AnalysisService;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    @GetMapping
    public ResponseEntity<Analysis> getAnalysis() {
        return ResponseEntity.ok(analysisService.getAnalysis());
    }
}
