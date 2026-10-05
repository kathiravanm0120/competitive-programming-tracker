package Competitive.Programming.Tracker.controller;

import Competitive.Programming.Tracker.dto.ReportSummaryResponse;
import Competitive.Programming.Tracker.service.ReportsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponse> getSummary(
            Authentication authentication) {
        return ResponseEntity.ok(
                reportsService.getSummary(authentication.getName())
        );
    }

    @GetMapping(value = "/export/problems/csv", produces = "text/csv")
    public ResponseEntity<ByteArrayResource> exportProblemsCsv(
            Authentication authentication) {
        return csvResponse(
                reportsService.exportProblemsCsv(authentication.getName()),
                "problems.csv"
        );
    }

    @GetMapping(value = "/export/goals/csv", produces = "text/csv")
    public ResponseEntity<ByteArrayResource> exportGoalsCsv(
            Authentication authentication) {
        return csvResponse(
                reportsService.exportGoalsCsv(authentication.getName()),
                "goals.csv"
        );
    }

    @GetMapping(value = "/export/planner/csv", produces = "text/csv")
    public ResponseEntity<ByteArrayResource> exportPlannerCsv(
            Authentication authentication) {
        return csvResponse(
                reportsService.exportPlannerCsv(authentication.getName()),
                "planner-tasks.csv"
        );
    }

    @GetMapping(value = "/export/platforms/csv", produces = "text/csv")
    public ResponseEntity<ByteArrayResource> exportPlatformsCsv(
            Authentication authentication) {
        return csvResponse(
                reportsService.exportPlatformsCsv(authentication.getName()),
                "platform-statistics.csv"
        );
    }

    @GetMapping(value = "/export/achievements/csv", produces = "text/csv")
    public ResponseEntity<ByteArrayResource> exportAchievementsCsv(
            Authentication authentication) {
        return csvResponse(
                reportsService.exportAchievementsCsv(authentication.getName()),
                "achievements.csv"
        );
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> exportPdf(
            Authentication authentication) {
        byte[] bytes = reportsService.generatePdf(authentication.getName());
        ByteArrayResource resource = new ByteArrayResource(bytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(bytes.length);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("competitive-programming-report.pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> csvResponse(
            String csv,
            String fileName) {
        byte[] bytes = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("text/csv; charset=UTF-8")
        );
        headers.setContentLength(bytes.length);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(fileName)
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
