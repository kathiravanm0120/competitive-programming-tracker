package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.AchievementResponse;
import Competitive.Programming.Tracker.dto.CodeforcesSubmissionResponse;
import Competitive.Programming.Tracker.dto.DashboardResponse;
import Competitive.Programming.Tracker.dto.GoalResponse;
import Competitive.Programming.Tracker.dto.PlannerTaskResponse;
import Competitive.Programming.Tracker.dto.PlatformStatistics;
import Competitive.Programming.Tracker.dto.ProblemResponse;
import Competitive.Programming.Tracker.dto.ReportSummaryResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportsService {

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final DashboardService dashboardService;
    private final ProblemService problemService;
    private final GoalService goalService;
    private final PlannerTaskService plannerTaskService;
    private final AchievementService achievementService;

    public ReportsService(
            DashboardService dashboardService,
            ProblemService problemService,
            GoalService goalService,
            PlannerTaskService plannerTaskService,
            AchievementService achievementService) {
        this.dashboardService = dashboardService;
        this.problemService = problemService;
        this.goalService = goalService;
        this.plannerTaskService = plannerTaskService;
        this.achievementService = achievementService;
    }

    public ReportSummaryResponse getSummary(String username) {
        DashboardResponse dashboard = dashboardService.getDashboard(username);
        List<GoalResponse> goals = goalService.getGoals(username);
        List<PlannerTaskResponse> tasks = plannerTaskService.getTasks(username, null);
        List<AchievementResponse> achievements = achievementService.getAchievements(username);

        ReportSummaryResponse summary = new ReportSummaryResponse();
        summary.setUsername(username);
        summary.setGeneratedAt(LocalDateTime.now());
        summary.setTrackedProblems(dashboard.getTracker().getTotalProblems());
        summary.setTrackedSolvedProblems(dashboard.getTracker().getSolvedProblems());
        summary.setPlatformsConnected(dashboard.getOverall().getPlatformsConnected());
        summary.setTotalPlatformSolved(dashboard.getOverall().getTotalSolved());
        summary.setTotalContests(dashboard.getOverall().getTotalContests());
        summary.setGoalsTotal(goals.size());
        summary.setGoalsActive((int) goals.stream().filter(g -> "ACTIVE".equalsIgnoreCase(g.getStatus())).count());
        summary.setGoalsCompleted((int) goals.stream().filter(g -> "COMPLETED".equalsIgnoreCase(g.getStatus())).count());
        summary.setPlannerTasksTotal(tasks.size());
        summary.setPlannerTasksCompleted((int) tasks.stream().filter(PlannerTaskResponse::isCompleted).count());
        summary.setAchievementsTotal(achievements.size());
        summary.setAchievementsUnlocked((int) achievements.stream().filter(AchievementResponse::isUnlocked).count());
        summary.setPlatforms(dashboard.getPlatforms());
        return summary;
    }

    public String exportProblemsCsv(String username) {
        List<ProblemResponse> rows = problemService.getUserProblems(username);
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Platform,Difficulty,Topic,Status,Favorite,URL,Notes\n");
        for (ProblemResponse p : rows) {
            csv.append(csvValue(p.getId()))
                    .append(',').append(csvValue(p.getTitle()))
                    .append(',').append(csvValue(p.getPlatform()))
                    .append(',').append(csvValue(p.getDifficulty()))
                    .append(',').append(csvValue(p.getTopic()))
                    .append(',').append(csvValue(p.getStatus()))
                    .append(',').append(p.isFavorite())
                    .append(',').append(csvValue(p.getUrl()))
                    .append(',').append(csvValue(p.getNotes()))
                    .append('\n');
        }
        return csv.toString();
    }

    public String exportGoalsCsv(String username) {
        List<GoalResponse> rows = goalService.getGoals(username);
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Description,Goal Type,Target,Current,Unit,Start Date,End Date,Platform,Topic,Status,Progress %,Overdue\n");
        for (GoalResponse g : rows) {
            csv.append(csvValue(g.getId()))
                    .append(',').append(csvValue(g.getTitle()))
                    .append(',').append(csvValue(g.getDescription()))
                    .append(',').append(csvValue(g.getGoalType()))
                    .append(',').append(csvValue(g.getTargetValue()))
                    .append(',').append(csvValue(g.getCurrentValue()))
                    .append(',').append(csvValue(g.getUnit()))
                    .append(',').append(csvValue(g.getStartDate()))
                    .append(',').append(csvValue(g.getEndDate()))
                    .append(',').append(csvValue(g.getPlatform()))
                    .append(',').append(csvValue(g.getTopic()))
                    .append(',').append(csvValue(g.getStatus()))
                    .append(',').append(g.getProgressPercent())
                    .append(',').append(g.isOverdue())
                    .append('\n');
        }
        return csv.toString();
    }

    public String exportPlannerCsv(String username) {
        List<PlannerTaskResponse> rows = plannerTaskService.getTasks(username, null);
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Description,Task Date,Priority,Completed,Completed At,Goal ID,Goal Title\n");
        for (PlannerTaskResponse t : rows) {
            csv.append(csvValue(t.getId()))
                    .append(',').append(csvValue(t.getTitle()))
                    .append(',').append(csvValue(t.getDescription()))
                    .append(',').append(csvValue(t.getTaskDate()))
                    .append(',').append(csvValue(t.getPriority()))
                    .append(',').append(t.isCompleted())
                    .append(',').append(csvValue(t.getCompletedAt()))
                    .append(',').append(csvValue(t.getGoalId()))
                    .append(',').append(csvValue(t.getGoalTitle()))
                    .append('\n');
        }
        return csv.toString();
    }

    public String exportPlatformsCsv(String username) {
        DashboardResponse dashboard = dashboardService.getDashboard(username);
        StringBuilder csv = new StringBuilder();
        csv.append("Platform,Connected,Available,Handle,Rating,Max Rating,Rank,Solved,Contests,Profile URL,Key Metrics\n");
        for (PlatformStatistics p : dashboard.getPlatforms()) {
            csv.append(csvValue(p.getPlatform()))
                    .append(',').append(p.isConnected())
                    .append(',').append(p.isAvailable())
                    .append(',').append(csvValue(p.getHandle()))
                    .append(',').append(csvValue(p.getRating()))
                    .append(',').append(csvValue(p.getMaxRating()))
                    .append(',').append(csvValue(p.getRank()))
                    .append(',').append(csvValue(p.getSolved()))
                    .append(',').append(csvValue(p.getContests()))
                    .append(',').append(csvValue(p.getProfileUrl()))
                    .append(',').append(csvValue(flattenMetrics(p.getMetrics())))
                    .append('\n');
        }
        return csv.toString();
    }

    public String exportAchievementsCsv(String username) {
        List<AchievementResponse> rows = achievementService.getAchievements(username);
        StringBuilder csv = new StringBuilder();
        csv.append("Code,Title,Description,Category,Icon,Current,Target,Progress %,Unlocked\n");
        for (AchievementResponse a : rows) {
            csv.append(csvValue(a.getCode()))
                    .append(',').append(csvValue(a.getTitle()))
                    .append(',').append(csvValue(a.getDescription()))
                    .append(',').append(csvValue(a.getCategory()))
                    .append(',').append(csvValue(a.getIcon()))
                    .append(',').append(csvValue(a.getCurrentValue()))
                    .append(',').append(csvValue(a.getTargetValue()))
                    .append(',').append(csvValue(a.getProgressPercent()))
                    .append(',').append(a.isUnlocked())
                    .append('\n');
        }
        return csv.toString();
    }

    public byte[] generatePdf(String username) {
        DashboardResponse dashboard = dashboardService.getDashboard(username);
        List<ProblemResponse> problems = problemService.getUserProblems(username);
        List<GoalResponse> goals = goalService.getGoals(username);
        List<PlannerTaskResponse> tasks = plannerTaskService.getTasks(username, null);
        List<AchievementResponse> achievements = achievementService.getAchievements(username);

        try (PDDocument document = new PDDocument()) {
            PdfWriter writer = new PdfWriter(document);

            writer.title("Competitive Programming Progress Report");
            writer.subtitle("User: " + username);
            writer.text("Generated: " + LocalDateTime.now().format(DATE_TIME));
            writer.space(8);

            writer.section("Overall Summary");
            writer.text("Tracked problems: " + dashboard.getTracker().getTotalProblems());
            writer.text("Tracked solved problems: " + dashboard.getTracker().getSolvedProblems());
            writer.text("Platforms connected: " + dashboard.getOverall().getPlatformsConnected());
            writer.text("Platform solved total: " + dashboard.getOverall().getTotalSolved());
            writer.text("Total contests: " + dashboard.getOverall().getTotalContests());
            writer.text("Note: platform solved totals may overlap when the same problem exists on multiple platforms.");

            writer.section("Platform Statistics");
            for (PlatformStatistics p : dashboard.getPlatforms()) {
                writer.subsection(p.getPlatform());
                writer.text("Handle: " + safe(p.getHandle()));
                writer.text("Connected: " + p.isConnected() + " | Available: " + p.isAvailable());
                writer.text("Rating: " + safe(p.getRating()) + " | Max rating: " + safe(p.getMaxRating()));
                writer.text("Rank: " + safe(p.getRank()) + " | Solved: " + safe(p.getSolved()) + " | Contests: " + safe(p.getContests()));
                if (!p.getMetrics().isEmpty()) {
                    writer.text("Key metrics: " + flattenMetrics(p.getMetrics()));
                }
            }

            writer.section("Goals");
            if (goals.isEmpty()) {
                writer.text("No goals found.");
            } else {
                for (GoalResponse g : goals) {
                    writer.bullet(g.getTitle() + " - " + g.getProgressPercent() + "% - " + g.getStatus());
                }
            }

            writer.section("Planner");
            long completedTasks = tasks.stream().filter(PlannerTaskResponse::isCompleted).count();
            writer.text("Tasks completed: " + completedTasks + " / " + tasks.size());
            for (PlannerTaskResponse t : tasks.stream().limit(20).toList()) {
                writer.bullet(t.getTaskDate() + " - " + t.getTitle() + " - " + (t.isCompleted() ? "Completed" : "Pending"));
            }

            writer.section("Achievements");
            long unlocked = achievements.stream().filter(AchievementResponse::isUnlocked).count();
            writer.text("Unlocked: " + unlocked + " / " + achievements.size());
            for (AchievementResponse a : achievements) {
                if (a.isUnlocked()) {
                    writer.bullet(a.getTitle() + " - " + a.getDescription());
                }
            }

            writer.section("Tracked Problems");
            writer.text("Total tracked problems: " + problems.size());
            for (ProblemResponse p : problems.stream().limit(25).toList()) {
                writer.bullet(p.getTitle() + " | " + safe(p.getPlatform()) + " | " + safe(p.getDifficulty()) + " | " + safe(p.getStatus()));
            }

            if (dashboard.getCodeforces() != null && !dashboard.getCodeforces().getRecentSubmissions().isEmpty()) {
                writer.section("Recent Codeforces Submissions");
                for (CodeforcesSubmissionResponse s : dashboard.getCodeforces().getRecentSubmissions().stream().limit(20).toList()) {
                    writer.bullet(safe(s.getProblemName()) + " | " + safe(s.getVerdict()) + " | " + safe(s.getProgrammingLanguage()));
                }
            }

            return writer.finish();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF report", e);
        }
    }

    private String flattenMetrics(Map<String, Object> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return "";
        }
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            values.add(entry.getKey() + "=" + safe(entry.getValue()));
        }
        return String.join("; ", values);
    }

    private String csvValue(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        String escaped = text.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private String safe(Object value) {
        if (value == null) {
            return "-";
        }
        String text = String.valueOf(value);
        return text.replaceAll("[^\\x20-\\x7E]", " ").replaceAll("\\s+", " ").trim();
    }

    private static class PdfWriter {
        private static final float MARGIN = 48f;
        private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
        private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
        private static final float BOTTOM = 48f;

        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream stream;
        private float y;
        private int pageNumber;
        private final PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private final ByteArrayOutputStream output = new ByteArrayOutputStream();

        PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        void title(String text) throws IOException {
            ensureSpace(36);
            stream.beginText();
            stream.setFont(bold, 18);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(clean(text));
            stream.endText();
            y -= 26;
        }

        void subtitle(String text) throws IOException {
            ensureSpace(20);
            stream.beginText();
            stream.setFont(bold, 11);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(clean(text));
            stream.endText();
            y -= 17;
        }

        void section(String text) throws IOException {
            ensureSpace(28);
            y -= 4;
            stream.setNonStrokingColor(40, 160, 190);
            stream.addRect(MARGIN, y - 4, PAGE_WIDTH - 2 * MARGIN, 18);
            stream.fill();
            stream.setNonStrokingColor(255, 255, 255);
            stream.beginText();
            stream.setFont(bold, 11);
            stream.newLineAtOffset(MARGIN + 8, y);
            stream.showText(clean(text));
            stream.endText();
            stream.setNonStrokingColor(0, 0, 0);
            y -= 22;
        }

        void subsection(String text) throws IOException {
            ensureSpace(22);
            stream.beginText();
            stream.setFont(bold, 10);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(clean(text));
            stream.endText();
            y -= 15;
        }

        void text(String text) throws IOException {
            writeWrapped(clean(text), 9, 12);
        }

        void bullet(String text) throws IOException {
            writeWrapped("- " + clean(text), 9, 12);
        }

        void space(float amount) {
            y -= amount;
        }

        byte[] finish() throws IOException {
            footer();
            if (stream != null) {
                stream.close();
                stream = null;
            }
            document.save(output);
            return output.toByteArray();
        }

        private void writeWrapped(String text, float fontSize, float leading) throws IOException {
            String normalized = text == null ? "" : text;
            if (normalized.isBlank()) {
                y -= leading;
                return;
            }

            float maxWidth = PAGE_WIDTH - 2 * MARGIN;
            String[] words = normalized.split("\\s+");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (regular.getStringWidth(candidate) / 1000f * fontSize > maxWidth && !line.isEmpty()) {
                    drawLine(line.toString(), fontSize);
                    y -= leading;
                    line = new StringBuilder(word);
                    ensureSpace(leading + 2);
                } else {
                    line = new StringBuilder(candidate);
                }
            }

            if (!line.isEmpty()) {
                drawLine(line.toString(), fontSize);
                y -= leading;
            }
        }

        private void drawLine(String text, float fontSize) throws IOException {
            ensureSpace(fontSize + 4);
            stream.beginText();
            stream.setFont(regular, fontSize);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(clean(text));
            stream.endText();
        }

        private void ensureSpace(float needed) throws IOException {
            if (y - needed < BOTTOM) {
                footer();
                newPage();
            }
        }

        private void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            pageNumber++;
            stream = new PDPageContentStream(document, page);
            y = PAGE_HEIGHT - MARGIN;
        }

        private void footer() throws IOException {
            if (stream == null) {
                return;
            }
            stream.beginText();
            stream.setFont(regular, 8);
            stream.newLineAtOffset(MARGIN, 28);
            stream.showText("Competitive Programming Tracker - Page " + pageNumber);
            stream.endText();
        }

        private String clean(String value) {
            if (value == null) {
                return "";
            }
            return value.replaceAll("[^\\x20-\\x7E]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
        }
    }
}
