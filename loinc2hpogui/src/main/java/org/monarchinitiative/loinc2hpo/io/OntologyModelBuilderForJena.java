package org.monarchinitiative.loinc2hpo.io;

import javafx.concurrent.Task;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

public class OntologyModelBuilderForJena extends Task<Model> {

    private String pathToOntology;
    private static final Logger logger = LogManager.getLogger();

    public OntologyModelBuilderForJena(String pathToOntology) {
        this.pathToOntology = pathToOntology;
    }


    @Override
    protected Model call() throws Exception {

        //explicitely state that the model is Jena RDF model
        org.apache.jena.rdf.model.Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(pathToOntology);
            try {
                model.read(in, null);
            } catch (Exception e) {
                logger.error("cannot read in data to model");
            }
        } catch (JenaException je) {
            logger.entry("cannot open hpo.owl");
        }
        return model;
    }

}