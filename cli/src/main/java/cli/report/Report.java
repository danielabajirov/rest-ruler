package cli.report;

import cli.rule.IRestRule;
import cli.rule.Violation;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * this class handles all functionality concerning a report generation.
 */
public class Report {

    private static final String OUTPUT_DIR = "out";
    private static Report instance;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private String title;


    public static Report getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return new Report();
        }
    }

    /**
     * This method is used for writing all violations in a presentable way to the console.
     * @param violationList list of violations to be displayed in console
     */
    public void displayReport(List<Violation> violationList){
        //report content for console
        StringBuilder sbConsoleReport = new StringBuilder();
        sbConsoleReport.append(new Heading("REST API Specification Report", 1)).append("\n");

        // Table heading
        Table.Builder consoleReport = new Table.Builder().addRow("Line No.", "Line", "Rule Violated");

        violationList.sort(Violation.getComparator());

        //generate Table entries for each violation
        for (Violation v : violationList) {
            IRestRule rule = v.getRule();
            consoleReport.addRow(v.getLineViolation(), v.getKeyViolation(), rule.getTitle());
        }
        //add Table to console output
        sbConsoleReport.append(consoleReport.build());

        //print to console
        System.out.println(sbConsoleReport);

        // notification
        System.out.println("----------------------------------------------");
        System.out.println("\nIn total " + violationList.size() + " rule violations were found");

    }

    /**
     * Interface method of this class allowing a list of Violation objects to be written to file
     * @param violationList list of Violation objects to be written to file
     */
    public void generateReport(List<Violation> violationList) {
        this.title = getTimestamp();
        violationList.sort(Violation.getComparator());
        writeMarkdownReport(violationList);
    }

    /**
     * Overloaded method allowing to additionally set a custom name for the report file.
     * This overloaded Interface method of this class allows a list of Violation objects to be written to file
     * @param violationList List of Violation objects to be written to file
     * @param title custom name tag for the report file
     */
    public void generateReport(List<Violation> violationList, String title) {
        this.title = cleanTitle(title);
        violationList.sort(Violation.getComparator());
        writeMarkdownReport(violationList);
    }

    /**
     * writes a report file in markdown format containing a table of all collected violations
     * @param violationList list of Violation Objects to be written to file
     */
    private void writeMarkdownReport(List<Violation> violationList) {
        //report content for File
        StringBuilder sbMDReport = new StringBuilder();
        sbMDReport.append(new Heading("REST API Specification Report", 1)).append("\n");
        //report content for console
        StringBuilder sbConsoleReport = new StringBuilder();
        sbConsoleReport.append(new Heading("REST API Specification Report", 1)).append("\n");

        // Table heading
        Table.Builder mdReport = new Table.Builder().addRow("Line No.", "Line", "Rule Violated", "Category",
                "Severity", "Rule Type", "Software Quality Attributes", "Improvement Suggestion");

        Table.Builder consoleReport = new Table.Builder().addRow("Line No.", "Line", "Rule Violated");

        //generate Table entries for each violation
        for (Violation v : violationList) {
            IRestRule rule = v.getRule();
            mdReport.addRow(v.getLineViolation(), v.getKeyViolation(), rule.getTitle(), rule.getCategory(),
                    rule.getSeverityType(), rule.getRuleType().toString().replace("[", "").replace("]", ""),
                    rule.getRuleSoftwareQualityAttribute().toString().replace("[", "").replace("]", ""),
                    v.getImprovementSuggestion());
            consoleReport.addRow(v.getLineViolation(), v.getKeyViolation(), rule.getTitle());
        }
        //add Table to report
        sbMDReport.append(mdReport.build());

        //add Table to console output
        sbConsoleReport.append(consoleReport.build());

        //print to console
        System.out.println(sbConsoleReport);

        // notification
        System.out.println("----------------------------------------------");
        System.out.println("\nIn total " + violationList.size() + " rule violations were found");

        try {
            writeReportToFile(sbMDReport);
        } catch (IOException e) {
            logger.severe("Error on writing report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeReportToFile(StringBuilder sbMDReport) throws IOException {
        Path file;
        //create directory
        Path path = Path.of(OUTPUT_DIR);
        if (!Files.isDirectory(path)) {
            path = Files.createDirectory(path);
        }

        //write file
        String filename = "Report_" + title + ".md";
        if (Files.notExists(path.resolve(filename))){
            file = Files.createFile(path.resolve(filename));
        }else{
            file = path.resolve(filename);
        }


        BufferedWriter bw = Files.newBufferedWriter(file);
        PrintWriter printWriter = new PrintWriter(bw);
        printWriter.print(sbMDReport);

        //inform user where file has been written to
        System.out.println("--> The detailed report can be found here: " + path.toAbsolutePath() + "\n");

        printWriter.close();
        bw.close();
    }

    /**
     * generates a String of the current date and time
     * @return timestamp string
     */
    private String getTimestamp(){
        //get current time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    /**
     * cleans up title of any illegal characters for files. Paths have a limit of 256 characters, limiting the length of the filename.
     * Therefore this method also limits the length of the filename to a recommended length of 31 by shortening the string if needed.
     * @param string title string to be examined and cleaned
     * @return cleaned title string
     */
    private String cleanTitle(String string){
        String illegalChar = "[\\#\\%\\&\\{\\}\\\\\\<\\>\\*\\?\\/\\$\\!\\'\\\"\\:\\@\\+\\`\\|=\\s]";
        String cleanString = string.trim();
        if (cleanString.length() > 31){
            cleanString = cleanString.substring(0, 30);
        }

        //replace all whitespaces with hyphens
        cleanString = cleanString.replaceAll("\\s+", "-");
        //remove all illegal characters
        cleanString = cleanString.replaceAll(illegalChar, "");
        return cleanString;
    }
}
