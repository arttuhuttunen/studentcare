package org.utu.studentcare.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides name data from CSV files.
 */
public class CSVNames {
    /**
     * male names
     */
    public final List<String> mNames = readCSV("nimet1.csv", 3);

    /**
     * female names
     */
    public final List<String> fNames = readCSV("nimet2.csv", 3);

    /**
     * lastnames
     */
    public final List<String> lastNames = readCSV("nimet3.csv", 3);

    /**
     * study programs
     */
    public final List<String> programs = readCSV("ohjelmat.csv", 3);

    /**
     * courses
     */
    public final List<String> courses = readCSV("kurssit.csv", 3);

    List<String> readCSV(String file, int skipAmount) {
        //Forced InputStreamReader to use UTF-8 as char encoding, because either Maven or Jetty doesn't automatically use it
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file), StandardCharsets.UTF_8)).lines()) {
            return stream.skip(skipAmount).map(a -> a.replaceAll("\"", "")).collect(Collectors.toList());
        } catch (Exception e) {
            throw new Error("Could not load " + file + ".\n" + e.getMessage());
        }
    }
}