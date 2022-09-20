package rest.studentproject.rule.underscoreTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.rules.UnderscoreRule;
import rest.studentproject.rule.Violation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnderscoreRuleTest {

    RestAnalyzer restAnalyzer;
    UnderscoreRule underscore;

    @Test
    @DisplayName("Test that checks if no violation is detected when there is a correct OpenAPI definition.")
    void validFile() {
        String url = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
        List<Violation> violations = runMethodUnderTest(url);
        assertEquals(0, violations.size(), "There should be no rule violation for the valid openAPI definition.");
    }


    @Test
    @DisplayName("Test that checks if the four underscore rule violations are detected.")
    void invalidFile() {

        String url = "src/test/java/rest/studentproject/rule/underscoretest/InvalidOpenAPIUnderscroeRule.json";

        List<Violation> violations = runMethodUnderTest(url);
        assertEquals(4, violations.size(), "There should be four rule violations. Three of the seven paths are valid.");
    }

    private List<Violation> runMethodUnderTest(String url) {

        this.restAnalyzer = new RestAnalyzer(url);
        this.underscore = new UnderscoreRule(true);

        return this.restAnalyzer.runAnalyse(List.of(this.underscore), false);
    }
}