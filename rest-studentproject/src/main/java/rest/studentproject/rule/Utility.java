package rest.studentproject.rule;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import rest.studentproject.rule.constants.RequestType;
import rest.studentproject.rule.constants.SecuritySchema;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.google.common.collect.Lists;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rest.studentproject.rule.rules.SingularDocumentNameRule.PLURAL;
import static rest.studentproject.rule.rules.SingularDocumentNameRule.SINGULAR;

public class Utility {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String PATH_TO_ENGLISH_DICTIONARY =
            "src/main/java/rest/studentproject/docs/wordninja_words.txt";
    public static final String MODELS_EN_POS_MAXENT_BIN = "models/en-pos-maxent.bin";

    private Utility() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean getPathSegmentContained(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (FileReader fileReader = new FileReader(filePath)) {
            try (Scanner scanner = new Scanner(fileReader)) {
                while (scanner.hasNext()) {
                    String wordFromDictionary = scanner.next();
                    if (word.toLowerCase().contains(wordFromDictionary)) {
                        isWordInDictionary = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    public static boolean getPathSegmentMatch(String word, String filePath) {
        boolean isWordInDictionary = false;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.useDelimiter("\\Z").next().matches(word)) isWordInDictionary = true;
        } catch (Exception e) {
            logger.severe("Error on checking if a word is contained in a dictionary: " + e.getMessage());
        }
        return isWordInDictionary;
    }

    public static OpenAPI getOpenAPI(String path) {
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(path, null, null);
        return swaggerParseResult.getOpenAPI();
    }

    /**
     * Method to change the switchPathSegment from plural to singular a vice versa.
     * @param equals
     * @return
     */
    public static String getControlPathSegmentForRule(boolean equals) {
        if (equals) {
            return SINGULAR;
        } else {
            return PLURAL;
        }
    }

    /**
     * Get a token from a word using the nlp apache pos tagger library.
     * @param pathSegment
     * @return
     */
    public static String getTokenNLP(String pathSegment){
        if(pathSegment.equals("")){
            return null;
        }
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(pathSegment);
        try(InputStream modelIn = new FileInputStream(
                MODELS_EN_POS_MAXENT_BIN);) {
            POSModel posModel = new POSModel(modelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            String tags[] = posTagger.tag(tokens);
            return tags[0];
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Given a word check if it is plural or singular based on token.
     * @param token
     * @return
     */
    public static String getTokenFromWord(String token) {
        String currentSwitchPathSegment;
        if (token.equals("NNS") || token.equals("NNPS")) {
            currentSwitchPathSegment = PLURAL;
        } else if (token.equals("NN") || token.equals("NNP")) {
            currentSwitchPathSegment = SINGULAR;
        } else {
            currentSwitchPathSegment = PLURAL;
        }
        return currentSwitchPathSegment;
    }

    /**
     * Split a string into words if possible using an english dictionary to match the words.
     * @param sentence
     * @return
     * @throws IOException
     */
    public static List<String> splitContiguousWords(String sentence) throws IOException {
        String splitRegex = "[^a-zA-Z0-9']+";
        Map<String, Number> wordCost = new HashMap<>();
        List<String> dictionaryWords = new ArrayList<>();
        Stream<String> stringLines = null;
        try {
            stringLines = Files.lines(Paths.get(PATH_TO_ENGLISH_DICTIONARY), Charset.defaultCharset());
            dictionaryWords = stringLines.collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error on getting the english dictionary file {e}", e);
        } finally {
            if (stringLines != null) stringLines.close();
        }

        double naturalLogDictionaryWordsCount = Math.log(dictionaryWords.size());
        long wordIdx = 0;
        for (String word : dictionaryWords) {
            wordCost.put(word, Math.log(++wordIdx * naturalLogDictionaryWordsCount));
        }
        int maxWordLength = Collections.max(dictionaryWords, Comparator.comparing(String::length)).length();
        List<String> splitWords = new ArrayList<>();
        for (String partSentence : sentence.split(splitRegex)) {
            splitWords.add(split(partSentence, wordCost, maxWordLength));
        }
        return splitWords;
    }

    /**
     * Split a string into sub strings.
     * @param partSentence
     * @param wordCost
     * @param maxWordLength
     * @return
     */
    public static String split(String partSentence, Map<String, Number> wordCost, int maxWordLength) {
        List<ImmutablePair<Number, Number>> cost = new ArrayList<>();
        cost.add(new ImmutablePair<>(0, 0));
        for (int index = 1; index < partSentence.length() + 1; index++) {
            cost.add(bestMatch(partSentence, cost, index, wordCost, maxWordLength));
        }
        int idx = partSentence.length();
        List<String> output = new ArrayList<>();
        while (idx > 0) {
            ImmutablePair<Number, Number> candidate = bestMatch(partSentence, cost, idx, wordCost, maxWordLength);
            Number candidateCost = candidate.getKey();
            Number candidateIndexValue = candidate.getValue();
            if (candidateCost.doubleValue() != cost.get(idx).getKey().doubleValue())
                logger.log(Level.SEVERE, "Candidate cost unmatched; This should not be the case!");
            boolean newToken = true;
            String token = partSentence.substring(idx - candidateIndexValue.intValue(), idx);
            if (token.equals("'") && !output.isEmpty()) {
                String lastWord = output.get(output.size() - 1);
                if (lastWord.equalsIgnoreCase("'s") || (Character.isDigit(partSentence.charAt(idx - 1)) && Character.isDigit(lastWord.charAt(0)))) {
                    output.set(output.size() - 1, token + lastWord);
                    newToken = false;
                }
            }
            if (newToken) {
                output.add(token);
            }
            idx -= candidateIndexValue.intValue();
        }
        return String.join(" ", Lists.reverse(output));
    }

    /**
     * Get the best match for a word.
     * @param partSentence
     * @param cost
     * @param index
     * @param wordCost
     * @param maxWordLength
     * @return
     */
    public static ImmutablePair<Number, Number> bestMatch(String partSentence, List<ImmutablePair<Number, Number>> cost,
                                                    int index, Map<String, Number> wordCost, int maxWordLength) {
        List<ImmutablePair<Number, Number>> candidates = Lists.reverse(cost.subList(Math.max(0,
                index - maxWordLength), index));
        int enumerateIdx = 0;
        ImmutablePair<Number, Number> minPair = new ImmutablePair<>(Integer.MAX_VALUE, enumerateIdx);
        for (ImmutablePair<Number, Number> pair : candidates) {
            ++enumerateIdx;
            String subsequence = partSentence.substring(index - enumerateIdx, index).toLowerCase();
            Number minCost = Integer.MAX_VALUE;
            if (wordCost.containsKey(subsequence)) {
                minCost = pair.getKey().doubleValue() + wordCost.get(subsequence).doubleValue();
            }
            if (minCost.doubleValue() < minPair.getKey().doubleValue()) {
                minPair = new ImmutablePair<>(minCost.doubleValue(), enumerateIdx);
            }
        }
        return minPair;
    }

    public static HttpURLConnection createHttpConnection(URL url, RequestType requestMethod) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestMethod.name());
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred when creating the http url connection: {e}", e);
        }
        return con;
    }

    public static URL getURL(SecuritySchema securitySchema, String pw, String serverURL, String path) {
        URL url = null;
        try {
            if (securitySchema == SecuritySchema.APIKEY) {
                url = new URL(serverURL + path + "?api_key=" + pw.substring(0, pw.length() - 1));
            } else url = new URL(serverURL + path);
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Exception on trying to request: {0}", e.getMessage());
        }
        return url;
    }
}
