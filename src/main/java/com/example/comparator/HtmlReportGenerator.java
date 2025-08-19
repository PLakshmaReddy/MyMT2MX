package com.example.comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlReportGenerator {

    public void generateReport(List<ComparisonResult> results, String reportPath, int totalCompared, long withDifferences) throws IOException {
        Document doc = Jsoup.parse("<html><head><title>Comparison Report</title></head><body></body></html>");
        doc.head().append("<style>" +
                "body { font-family: sans-serif; }" +
                "table { border-collapse: collapse; width: 100%; margin-top: 20px; }" +
                "th, td { border: 1px solid #dddddd; text-align: left; padding: 8px; }" +
                "tr:nth-child(even) { background-color: #f2f2f2; }" +
                ".difference { background-color: #ffdddd; }" +
                ".summary { margin-bottom: 20px; }" +
                ".expandable { cursor: pointer; }" +
                ".details { display: none; }" +
                "th.sortable { cursor: pointer; }" +
                "</style>");

        Element body = doc.body();

        body.append("<h1>MT/pacs Comparison Report</h1>");

        // Summary
        Element summary = body.appendElement("div").addClass("summary");
        summary.append("<h2>Summary</h2>");
        summary.append("<p>Total transactions compared: " + totalCompared + "</p>");
        summary.append("<p>Transactions with differences: " + withDifferences + "</p>");
        summary.append("<p>Transactions without differences: " + (totalCompared - withDifferences) + "</p>");

        // Filter
        body.append("<h2>Details</h2>");
        body.append("<input type='text' id='filterInput' onkeyup='filterTable()' placeholder='Filter by transaction reference...'>");

        Element table = body.appendElement("table").id("reportTable");
        Element thead = table.appendElement("thead");
        Element tr = thead.appendElement("tr");
        tr.appendElement("th").text("Transaction Reference");
        tr.appendElement("th").text("Status");

        Element tbody = table.appendElement("tbody");

        for (ComparisonResult result : results) {
            Element row = tbody.appendElement("tr").addClass("expandable").attr("onclick", "toggleDetails(this)");
            row.appendElement("td").text(result.getTransactionReference());
            if (result.hasDifferences()) {
                row.appendElement("td").text("Differences found").addClass("difference");
            } else {
                row.appendElement("td").text("No differences");
            }

            Element detailsRow = tbody.appendElement("tr").addClass("details");
            Element detailsCell = detailsRow.appendElement("td").attr("colspan", "2");
            if (result.hasDifferences()) {
                Element detailsTable = detailsCell.appendElement("table");
                Element detailsThead = detailsTable.appendElement("thead");
                Element detailsTr = detailsThead.appendElement("tr");
                detailsTr.appendElement("th").text("Field Name");
                detailsTr.appendElement("th").text("MT Value");
                detailsTr.appendElement("th").text("pacs Value");
                Element detailsTbody = detailsTable.appendElement("tbody");
                for (FieldDifference diff : result.getDifferences()) {
                    Element diffRow = detailsTbody.appendElement("tr");
                    diffRow.appendElement("td").text(diff.getFieldName());
                    diffRow.appendElement("td").text(diff.getMtValue());
                    diffRow.appendElement("td").text(diff.getPacsValue());
                }
            }
        }

        // JavaScript
        body.append("<script>" +
                "function filterTable() {" +
                "  var input, filter, table, tr, td, i, txtValue;" +
                "  input = document.getElementById('filterInput');" +
                "  filter = input.value.toUpperCase();" +
                "  table = document.getElementById('reportTable');" +
                "  tr = table.getElementsByTagName('tr');" +
                "  for (i = 1; i < tr.length; i+=2) {" + // Start from 1 to skip header, increment by 2 for expandable rows
                "    td = tr[i].getElementsByTagName('td')[0];" +
                "    if (td) {" +
                "      txtValue = td.textContent || td.innerText;" +
                "      if (txtValue.toUpperCase().indexOf(filter) > -1) {" +
                "        tr[i].style.display = '';" +
                "        tr[i+1].style.display = tr[i].classList.contains('active') ? '' : 'none';" +
                "      } else {" +
                "        tr[i].style.display = 'none';" +
                "        tr[i+1].style.display = 'none';" +
                "      }" +
                "    }" +
                "  }" +
                "}" +
                "function toggleDetails(row) {" +
                "  row.classList.toggle('active');" +
                "  var detailsRow = row.nextElementSibling;" +
                "  if (detailsRow.style.display === 'table-row') {" +
                "    detailsRow.style.display = 'none';" +
                "  } else {" +
                "    detailsRow.style.display = 'table-row';" +
                "  }" +
                "}" +
                "</script>");


        try (FileWriter writer = new FileWriter(new File(reportPath))) {
            writer.write(doc.outerHtml());
        }
    }
}
