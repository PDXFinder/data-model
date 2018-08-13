package org.pdxfinder.commands;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neo4j.ogm.session.Session;

import org.pdxfinder.dao.*;
import org.pdxfinder.services.DataImportService;

import org.pdxfinder.services.ds.Standardizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;

/*
 * Created by csaba on 06/08/2018.
 */
@Component
@Order(value = 0)
/**
 *
 * aka UPDOG: Universal PdxData tO Graph
 */
public class UniversalLoader implements CommandLineRunner {

    private final static Logger log = LoggerFactory.getLogger(UniversalLoader.class);

    private Options options;
    private CommandLineParser parser;
    private CommandLine cmd;
    private HelpFormatter formatter;

    private DataImportService dataImportService;
    private Session session;

    @Value("${universal.template.files}")
    private String templateFiles;

    /**
     * Placeholder for the data stored in the "patient" tab
     */
    private List<List<String>> patientSheetData;


    /**
     * Placeholder for the data stored in the "patienttumor at collection" tab
     */
    private List<List<String>> patientTumorSheetData;

    /**
     * Placeholder for the data stored in the "patient treatment information" tab
     */
    private List<List<String>> patientTreatmentSheetData;
    private List<List<String>> pdxModelSheetData;
    private List<List<String>> pdxModelVariationSheetData;

    private Group ds;

    @PostConstruct
    public void init() {
        formatter = new HelpFormatter();
    }

    public UniversalLoader(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @Override
    public void run(String... args) throws Exception {

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parser.accepts("loadUniversal", "Run universal loader");
        //TODO: enable this when loader is finished
        //parser.accepts("loadALL", "Load all, run universal data");
        OptionSet options = parser.parse(args);

        if (options.has("loadUniversal")) {

            log.info("Running universal");
            FileInputStream excelFile = new FileInputStream(new File(templateFiles));

            Workbook workbook = new XSSFWorkbook(excelFile);
            log.info("Loading template");
            initializeTemplateData(workbook);

            loadTemplateData();

            workbook.close();
            excelFile.close();

        }
    }


    /**
     * Loads the data from the spreadsheet and stores it in lists
     *
     * @param workbook
     */
    private void initializeTemplateData(Workbook workbook) {


        ds = null;
        patientSheetData = new ArrayList<>();
        patientTumorSheetData = new ArrayList<>();
        patientTreatmentSheetData = new ArrayList<>();
        pdxModelSheetData = new ArrayList<>();
        pdxModelVariationSheetData = new ArrayList<>();

        initializeSheetData(workbook.getSheetAt(1), "patientSheetData");
        initializeSheetData(workbook.getSheetAt(2), "patientTumorSheetData");
        initializeSheetData(workbook.getSheetAt(3), "patientTreatmentSheetData");
        initializeSheetData(workbook.getSheetAt(4), "pdxModelSheetData");
        initializeSheetData(workbook.getSheetAt(5), "pdxModelVariationSheetData");
    }

    /**
     * Loads the data from a spreadsheet tab into a placeholder
     * @param sheet
     * @param sheetName
     */
    private void initializeSheetData(Sheet sheet, String sheetName) {

        Iterator<Row> iterator = sheet.iterator();
        int rowCounter = 0;
        while (iterator.hasNext()) {

            Row currentRow = iterator.next();
            rowCounter++;

            if (rowCounter < 6) continue;

            Iterator<Cell> cellIterator = currentRow.iterator();
            List dataRow = new ArrayList();
            boolean isFirstColumn = true;
            while (cellIterator.hasNext()) {

                Cell currentCell = cellIterator.next();
                //skip the first column
                if (isFirstColumn) {
                    isFirstColumn = false;
                    continue;
                }

                //getCellTypeEnum shown as deprecated for version 3.15
                //getCellTypeEnum will be renamed to getCellType starting from version 4.0

                String value = null;
                switch (currentCell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        value = currentCell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        value = String.valueOf(currentCell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        value = String.valueOf(currentCell.getNumericCellValue());
                        break;
                }

                dataRow.add(value);
            }
            //check if there is some data in the row and they are not all nulls
            if(dataRow.size() > 0 && !isRowOfNulls(dataRow)){

                //insert the row to the appropriate placeholder
                if (sheetName.equals("patientSheetData")) {

                    patientSheetData.add(dataRow);
                } else if (sheetName.equals("patientTumorSheetData")) {

                    patientTumorSheetData.add(dataRow);
                } else if (sheetName.equals("patientTreatmentSheetData")) {

                    patientTreatmentSheetData.add(dataRow);
                } else if (sheetName.equals("pdxModelSheetData")) {

                    pdxModelSheetData.add(dataRow);
                } else if (sheetName.equals("pdxModelVariationSheetData")) {

                    pdxModelVariationSheetData.add(dataRow);
                }

            }



        }
    }

    /**
     * Loads the data from the lists into the DB
     */
    private void loadTemplateData() {

        createDataSourceGroup();
        createPatients();
        createPatientTumors();
        createPatientTreatments();
        createPdxModels();
        createPdxModelValidations();

    }


    private void createDataSourceGroup() {

        //TODO: this data has to come from the spreadsheet, I am using constants for now

        ds = dataImportService.getProviderGroup("TRACE", "TR", "Trace data from template", "", "", "", "", "");

    }


    private void createPatients() {

        log.info("Creating Patients");
        for (List<String> patientRow : patientSheetData) {

            String patientId = patientRow.get(0);
            String sex = patientRow.get(1);
            String ethnicity = patientRow.get(2);

            if (patientId != null && ds != null) {

                dataImportService.createPatient(patientId, ds, sex, "", ethnicity);

            }
        }
    }


    private void createPatientTumors() {
        log.info("Creating Patient samples and snapshots");
        int row = 6;
        log.info("Tumor row number: "+patientTumorSheetData.size());
        for (List<String> patientTumorRow : patientTumorSheetData) {

            try {
                String patientId = patientTumorRow.get(0);
                String sampleId = patientTumorRow.get(1);
                String modelId = patientTumorRow.get(19);

                //skip rows where patient, model or sample id is null
                if (patientId == null || sampleId == null || modelId == null) continue;

                String dateOfCollection = patientTumorRow.get(2);
                String collectionEvent = patientTumorRow.get(3);
                String elapsedTime = patientTumorRow.get(4);
                String ageAtCollection = patientTumorRow.get(5);
                String diagnosis = patientTumorRow.get(6);
                String tumorType = patientTumorRow.get(7);
                String originTissue = patientTumorRow.get(8);
                String collectionSite = patientTumorRow.get(9);
                String stage = patientTumorRow.get(10);
                String stageClassification = patientTumorRow.get(11);
                String grade = patientTumorRow.get(12);
                String gradeClassification = patientTumorRow.get(13);


                //hack to avoid 0.0 values and negative numbers
                elapsedTime = elapsedTime.replaceAll("[^0-9]", "");

                Patient patient = dataImportService.findPatient(patientId, ds);

                if (patient == null) {

                    log.error("Patient does not exist, can not create tumor for " + patientId);
                    continue;
                }

                PatientSnapshot ps = dataImportService.getPatientSnapshot(patient, ageAtCollection, dateOfCollection, collectionEvent, elapsedTime);

                //have the correct snapshot, create a human sample and link it to the snapshot
                tumorType = Standardizer.getTumorType(tumorType);


                //String sourceSampleId, String dataSource,  String typeStr, String diagnosis, String originStr,
                //String sampleSiteStr, String extractionMethod, Boolean normalTissue, String stage, String stageClassification,
                // String grade, String gradeClassification
                Sample sample = dataImportService.getSample(sampleId, ds.getAbbreviation(), tumorType, diagnosis, originTissue,
                        collectionSite, "", false, stage, stageClassification, grade, gradeClassification);

                ps.addSample(sample);


                ModelCreation mc = new ModelCreation();

                mc.setSourcePdxId(modelId);
                mc.setDataSource(ds.getAbbreviation());
                mc.setSample(sample);
                mc.addRelatedSample(sample);

                dataImportService.savePatientSnapshot(ps);
                dataImportService.saveModelCreation(mc);
                row++;

            } catch (Exception e) {
                log.error("Exception in row: "+row);
                e.printStackTrace();

            }

        }

    }

    private void createPatientTreatments() {

    }

    private void createPdxModels() {

    }

    private void createPdxModelValidations() {

    }


    boolean isRowOfNulls(List list){
        for(Object o: list)
            if(!(o == null))
                return false;
        return true;
    }

}
