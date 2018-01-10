package org.monarchinitiative.loinc2hpo.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Let users provide a file containing their interested loinc codes, so that
 * they can just play annotate their own subset
 */
public class LoincOfInterest {

    private String pathToLoincOfInterest;
    private HashSet<String> LoincOfInterest;
    private static final Logger logger = LogManager.getLogger();

    public LoincOfInterest(String path) throws FileNotFoundException {
        this.pathToLoincOfInterest = path;
        this.LoincOfInterest = new HashSet<>();

        readLoincFromFile();
    }

    private void readLoincFromFile() throws FileNotFoundException {

        BufferedReader reader = new BufferedReader(new FileReader(this
                    .pathToLoincOfInterest));

        String newLine;

        try {
            newLine = reader.readLine();
            while (newLine != null) {
                if (newLine.trim().length() > 0 && Character.isDigit(newLine
                        .charAt
                        (0))) {
                    this.LoincOfInterest.add(newLine);
                }
                newLine = reader.readLine();
            }

        } catch (IOException e) {
            logger.error("cannot read in a loinc line");
        }
    }

    public HashSet<String> getLoincOfInterest() {
        return new HashSet<>(this.LoincOfInterest);
    }
}