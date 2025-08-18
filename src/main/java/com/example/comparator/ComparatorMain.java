package com.example.comparator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComparatorMain {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: java com.example.comparator.ComparatorMain <message_type> <mt_folder> <pacs_folder>");
            System.err.println("message_type can be 'mt202' or 'mt103'");
            System.exit(1);
        }

        String messageType = args[0];
        File mtFolder = new File(args[1]);
        File pacsFolder = new File(args[2]);

        if (!mtFolder.isDirectory() || !pacsFolder.isDirectory()) {
            System.err.println("Error: Please provide valid directories.");
            System.exit(1);
        }

        List<ComparisonResult> results = new ArrayList<>();

        if ("mt202".equalsIgnoreCase(messageType)) {
            MessageComparator comparator = new MessageComparator();
            File[] mtFiles = mtFolder.listFiles((dir, name) -> name.endsWith(".mt202"));
            if (mtFiles != null) {
                for (File mtFile : mtFiles) {
                    String baseName = mtFile.getName().substring(0, mtFile.getName().lastIndexOf('.'));
                    File pacsFile = new File(pacsFolder, baseName + ".pacs009");
                    if (pacsFile.exists()) {
                        results.add(comparator.compare(mtFile, pacsFile));
                    }
                }
            }
        } else if ("mt103".equalsIgnoreCase(messageType)) {
            MessageComparator103 comparator = new MessageComparator103();
            File[] mtFiles = mtFolder.listFiles((dir, name) -> name.endsWith(".mt103"));
            if (mtFiles != null) {
                for (File mtFile : mtFiles) {
                    String baseName = mtFile.getName().substring(0, mtFile.getName().lastIndexOf('.'));
                    File pacsFile = new File(pacsFolder, baseName + ".pacs008");
                    if (pacsFile.exists()) {
                        results.add(comparator.compare(mtFile, pacsFile));
                    }
                }
            }
        } else {
            System.err.println("Error: Invalid message type. Please use 'mt202' or 'mt103'.");
            System.exit(1);
        }

        HtmlReportGenerator reportGenerator = new HtmlReportGenerator();
        reportGenerator.generateReport(results, "comparison_report.html");

        System.out.println("Comparison report generated: comparison_report.html");
    }
}
