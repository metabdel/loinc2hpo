package org.monarchinitiative.loinc2hpo.controller;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.model.Model;

import java.io.File;

@Singleton
public class SetupTabController {
    private static final Logger logger = LogManager.getLogger();

    private Model model=null;

    @FXML private TextFlow textflow;

    @FXML private Button locationLoincHpoFileButton;

    @Inject MainController mainController;//@Inject PickerController picker;

    public void injectMainController(MainController mainController){
        this.mainController = mainController;
    }

    @FXML private void initialize() {
//        consoleTabController.injectMainController(this);
        initTextFlow();
    }

    public void setModel(Model m) {
        model=m;
    }


    private void initTextFlow() {
        Text text1 = new Text("\n\tLOINC2HPO: An app for biocuration LOINC to HPO mappings");
        text1.setFont(new Font(15));
        text1.setFill(Color.DARKSLATEBLUE);
        Text text2 = new Text("\n\n\t\t1. Use the edit menu to download hp.obo");
        Text text3 = new Text("\n\t\t2. Use the edit menu to tell the app where the LOINC file is located");
        Text text4 = new Text("\n\t\t3. Use the edit menu to indicate your biocurator id");
        Text text5 = new Text("\n\t\t4. Indicate location of loinc2hpo.tab curation file (usually the file from GitHub)");

        ObservableList list = textflow.getChildren();
        list.addAll(text1,text2,text3,text4,text5);
    }



    @FXML private void setLocationLoincHpoFile(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose loinc2hpo.tab annotation file");
        File f = chooser.showOpenDialog(null);
        if (model==null) {
            logger.error("model is null");
            return;
        }
        if (f != null) {
            String path = f.getAbsolutePath();
            model.setPathToAnnotationFile(path);
            logger.trace(String.format("Setting path to loinc2hpo.tab annotation file to %s",path));
        } else {
            logger.error("Unable to set path to loinc2hpo.tab annotation file");
            return;
        }
        model.writeSettings();
        e.consume();
    }

}



