package com.example.comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlReportGenerator {

    public void generateReport(List<ComparisonResult> results, String reportPath) throws IOException {
        Document doc = Jsoup.parse("<html><head><title>Comparison Report</title></head><body></body></html>");
        doc.head().append("<style>" +
                "table { border-collapse: collapse; width: 100%; }" +
                "th, td { border: 1px solid #dddddd; text-align: left; padding: 8px; }" +
                "tr:nth-child(even) { background-color: #f2f2f2; }" +
                "</style>");
        Element body = doc.body();

        body.append("<h1>MT202 vs pacs.009 Comparison Report</h1>");

        Element table = body.appendElement("table");
        Element thead = table.appendElement("thead");
        Element tr = thead.appendElement("tr");
        tr.append("<th>Transaction Reference</th>");
        tr.append("<th>Field Name</th>");
        tr.append("<th>MT202 Value</th>");
        tr.append("<th>pacs.009 Value</th>");

        Element tbody = table.appendElement("tbody");

        for (ComparisonResult result : results) {
            if (result.hasDifferences()) {
                for (FieldDifference diff : result.getDifferences()) {
                    Element row = tbody.appendElement("tr");
                    row.appendElement("td").text(result.getTransactionReference());
                    row.appendElement("td").text(diff.getFieldName());
                    row.appendElement("td").text(diff.getMtValue());
                    row.appendElement("td").text(diff.getPacsValue());
                }
            } else {
                Element row = tbody.appendElement("tr");
                row.appendElement("td").text(result.getTransactionReference());
                row.appendElement("td").attr("colspan", "3").text("No differences found");
            }
        }

        try (FileWriter writer = new FileWriter(new File(reportPath))) {
            writer.write(doc.outerHtml());
        }
    }
}
