package org.pdxfinder.integration;

import org.junit.Before;
import org.junit.Test;
import org.pdxfinder.BaseTest;
import org.pdxfinder.dao.*;
import org.pdxfinder.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.Instant;

/**
 * Test the integration of tumor to patient
 */
public class PatientTumorIntegrationTest extends BaseTest {

    private final static Logger log = LoggerFactory.getLogger(PatientTumorIntegrationTest.class);
    private String tumorTypeName = "TEST_TUMORTYPE";
    private String extDsName = "TEST_SOURCE";
    private String extDsNameAlternate = "ALTERNATE_TEST_SOURCE";
    private String tissueName = "TEST_TISSUE";

    @Autowired
    private TumorRepository tumorRepository;

    @Autowired
    private TissueRepository tissueRepository;

    @Autowired
    private TumorTypeRepository tumorTypeRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ExternalDataSourceRepository externalDataSourceRepository;

    @Before
    public void setupDb() {

        tumorRepository.deleteAll();
        patientRepository.deleteAll();
        externalDataSourceRepository.deleteAll();
        tissueRepository.deleteAll();

        ExternalDataSource ds = externalDataSourceRepository.findByName(extDsName);
        if (ds == null) {
            log.debug("External data source {} not found. Creating", extDsName);
            ds = new ExternalDataSource(extDsName, extDsName, extDsName, Date.from(Instant.now()));
            externalDataSourceRepository.save(ds);
        }

        ExternalDataSource dsAlt = externalDataSourceRepository.findByName(extDsNameAlternate);
        if (dsAlt == null) {
            log.debug("External data source {} not found. Creating", extDsNameAlternate);
            dsAlt = new ExternalDataSource(extDsNameAlternate, extDsNameAlternate, extDsNameAlternate, Date.from(Instant.now()));
            externalDataSourceRepository.save(dsAlt);
        }

        TumorType tumorType = tumorTypeRepository.findByName(tumorTypeName);
        if (tumorType == null) {
            log.debug("Tumor type {} not found. Creating", tumorTypeName);
            tumorType = new TumorType(tumorTypeName);
            tumorTypeRepository.save(tumorType);
        }

        Tissue tissue = tissueRepository.findByName(tissueName);
        if (tissue == null) {
            log.debug("Tissue {} not found. Creating", extDsName);
            tissue = new Tissue(tissueName);
            tissueRepository.save(tissue);
        }
    }

    @Test
    public void persistPatientAndTumor() throws Exception {

        TumorType tumorType = tumorTypeRepository.findByName(tumorTypeName);
        Tissue tissue = tissueRepository.findByName(tissueName);
        ExternalDataSource externalDataSource = externalDataSourceRepository.findByAbbreviation(extDsName);

        for (Integer i = 0; i < 20; i++) {

            String sex = i % 2 == 0 ? "M" : "F";
            Long age = Math.round(Math.random() * 90);

            Tumor tumor = new Tumor("tumor-" + i, tumorType, "TEST_DIAGNOSIS", tissue, tissue, "TEST_CLASSIFICATION", externalDataSource);
            tumorRepository.save(tumor);

            Patient patient = new Patient(Double.toString(Math.pow(i, age)), sex, age.toString(), null, null, externalDataSource);
            patient.hasTumor(tumor);
            patientRepository.save(patient);

        }

        ExternalDataSource externalDataSourceAlternate = externalDataSourceRepository.findByAbbreviation(extDsNameAlternate);

        for (Integer i = 0; i < 22; i++) {

            String sex = i % 2 == 0 ? "M" : "F";
            Long age = Math.round(Math.random() * 80);

            Tumor tumor = new Tumor("tumor-" + i, tumorType, "TEST_DIAGNOSIS", tissue, tissue, "TEST_CLASSIFICATION", externalDataSourceAlternate);
            tumorRepository.save(tumor);

            Patient patient = new Patient(Double.toString(Math.pow(i, age)), sex, age.toString(), null, null, externalDataSourceAlternate);
            patient.hasTumor(tumor);
            patientRepository.save(patient);

        }

    }

}