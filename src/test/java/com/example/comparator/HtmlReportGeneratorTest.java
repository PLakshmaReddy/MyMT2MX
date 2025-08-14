package com.example.comparator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlReportGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void testGenerateReport() throws IOException {
        List<ComparisonResult> results = new ArrayList<>();
        ComparisonResult resultWithDiff = new ComparisonResult("TX1");
        resultWithDiff.addDifference("Field1", "A", "B");
        results.add(resultWithDiff);

        ComparisonResult resultNoDiff = new ComparisonResult("TX2");
        results.add(resultNoDiff);

        HtmlReportGenerator reportGenerator = new HtmlReportGenerator();
        File reportFile = tempDir.resolve("report.html").toFile();
        reportGenerator.generateReport(results, reportFile.getAbsolutePath());

        String reportContent = new String(Files.readAllBytes(reportFile.toPath()));
        System.out.println(reportContent);

        assertTrue(reportContent.contains("<h1>MT202 vs pacs.009 Comparison Report</h1>"));
        assertTrue(reportContent.contains("<td>TX1</td>"));
        assertTrue(reportContent.contains("<td>Field1</td>"));
        assertTrue(reportContent.contains("<td>A</td>"));
        assertTrue(reportContent.contains("<td>B</td>"));
        assertTrue(reportContent.contains("<td>TX2</td>"));
        assertTrue(reportContent.contains("<td colspan=\"3\">No differences found</td>"));
    }
}
