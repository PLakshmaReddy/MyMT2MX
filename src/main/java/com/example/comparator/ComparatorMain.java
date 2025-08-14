package com.example.comparator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComparatorMain {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java com.example.comparator.ComparatorMain <mt202_folder> <pacs009_folder>");
            System.exit(1);
        }

        File mtFolder = new File(args[0]);
        File pacsFolder = new File(args[1]);

        if (!mtFolder.isDirectory() || !pacsFolder.isDirectory()) {
            System.err.println("Error: Please provide valid directories.");
            System.exit(1);
        }

        MessageComparator comparator = new MessageComparator();
        List<ComparisonResult> results = new ArrayList<>();

        File[] mtFiles = mtFolder.listFiles((dir, name) -> name.endsWith(".mt202"));
        if (mtFiles != null) {
            for (File mtFile : mtFiles) {
                String baseName = mtFile.getName().substring(0, mtFile.getName().lastIndexOf('.'));
                File pacsFile = new File(pacsFolder, baseName + ".pacs009");

                if (pacsFile.exists()) {
                    results.add(comparator.compare(mtFile, pacsFile));
                } else {
                    System.err.println("Warning: No corresponding pacs.009 file found for " + mtFile.getName());
                }
            }
        }

        HtmlReportGenerator reportGenerator = new HtmlReportGenerator();
        reportGenerator.generateReport(results, "comparison_report.html");

        System.out.println("Comparison report generated: comparison_report.html");
    }
}
