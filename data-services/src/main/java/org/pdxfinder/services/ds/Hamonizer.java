package org.pdxfinder.services.ds;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONObject;
import org.pdxfinder.graph.dao.QualityAssurance;
import org.pdxfinder.services.DataImportService;
import org.pdxfinder.services.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hamonizer {


    static final String hci = "PDXNet-HCI-BCM";
    static final String mdAnderson = "PDXNet-MDAnderson";
    static final String irccCrc = "IRCC-CRC";
    static final String wustl = "PDXNet-WUSTL";
    static final String jax = "JAX";

    @Autowired
    private static DataImportService dataImportService;


    public static JSONArray getTreament(JSONObject data, String ds) throws Exception {

        JSONArray treatments = new JSONArray();

        if (ds.equals(hci)){

            try {
                if (data.has("Treatments")) {
                    JSONObject treatmentObj = data.optJSONObject("Treatments");
                    //if the treatment attribute is not an object = it is an array
                    if (treatmentObj == null && data.optJSONArray("Treatments") != null) {
                        treatments = data.getJSONArray("Treatments");
                    }
                }
            }catch (Exception e){}
        }
        return treatments;
    }





    public static String getStage(JSONObject data,String ds) throws Exception {
        String tumorStage = Standardizer.NOT_SPECIFIED;

        if (ds.equals(jax)) {
            tumorStage = data.getString("Tumor Stage");
        } else {
            tumorStage = Standardizer.getValue("Stage", data);
        }
        return tumorStage;
    }

    public static JSONArray getSpecimens(JSONObject data,String ds) throws Exception {

        JSONArray specimens = new JSONArray();

        if (ds.equals(irccCrc)){
            specimens = data.getJSONArray("Specimens");
        }
        return specimens;
    }


    public static String getFingerprinting(JSONObject data,String ds) throws Exception {
        String fingerprinting = Standardizer.NOT_SPECIFIED;

        if (ds.equals(irccCrc)){
            fingerprinting = data.getString("Fingerprinting");
        }
        return fingerprinting;
    }


    public static String getSampleID(JSONObject data,String ds) throws Exception {
        String sampleID = Standardizer.NOT_SPECIFIED;

        if (ds.equals(hci)){
            sampleID = data.getString("Sample ID");
        }

        if (ds.equals(irccCrc) || ds.equals(mdAnderson) || ds.equals(jax)){
            sampleID = data.getString("Model ID");
        }

        return sampleID;
    }

    public static String getDiagnosis(JSONObject data,String ds) throws Exception {
        String diagnosis = data.getString("Clinical Diagnosis");

        if (ds.equals(mdAnderson)){
            // mdAnderson preference is for histology
            String histology = data.getString("Histology");
            if (histology.trim().length() > 0) {
                if ("ACA".equals(histology)) {
                    diagnosis = "Adenocarcinoma";
                } else {
                    diagnosis = histology;
                }
            }
        }

        if (ds.equals(wustl)){
            // Preference is for Histology
            String histology = data.getString("Histology");
            if (histology.trim().length() > 0) {
                diagnosis = histology;
            }
        }

        if (ds.equals(jax)){
            // the preference is for clinical diagnosis but if not available use initial diagnosis
            if (diagnosis.trim().length() == 0 || "Not specified".equals(diagnosis)) {
                diagnosis = data.getString("Initial Diagnosis");
            }
        }





        return diagnosis;
    }


    public static String getEthnicity(JSONObject data,String ds) throws Exception {
        String ethnicity = Standardizer.NOT_SPECIFIED;

        if (ds.equals(hci)) {
            ethnicity = data.getString("Ethnicity");
        }

        if (ds.equals(jax)) {
            ethnicity = Standardizer.fixNotString(data.getString("Ethnicity"));
        }

        if (ds.equals(mdAnderson) || ds.equals(wustl)){
            ethnicity = Standardizer.getValue("Race",data);
            try {
                if (data.getString("Ethnicity").trim().length() > 0) {
                    ethnicity = data.getString("Ethnicity");
                }
            } catch (Exception e) {}
        }
        return ethnicity;
    }



    public static String getClassification(JSONObject data,String ds) throws Exception {
        String classification = Standardizer.NOT_SPECIFIED;

        if (ds.equals(mdAnderson) || ds.equals(wustl)){
            classification = data.getString("Stage") + "/" + data.getString("Grades");
        }

        if (ds.equals(irccCrc)){
            classification = data.getString("Stage");
        }

        if (ds.equals(jax)){
            classification = data.getString("Tumor Stage") + "/" + data.getString("Grades");
        }

        return classification;
    }




    public static String getImplantationType(JSONObject data,String ds) throws Exception {
        String implantationTypeStr = Standardizer.NOT_SPECIFIED;

        if (ds.equals(hci)){
            implantationTypeStr = Standardizer.getValue("Implantation Type", data);
        }

        if (ds.equals(mdAnderson) || ds.equals(wustl)){
            implantationTypeStr =  Standardizer.getValue("Tumor Prep",data);
        }

        return implantationTypeStr;
    }


    public static String getEngraftmentSite(JSONObject data,String ds) throws Exception {
        String implantationSite = Standardizer.NOT_SPECIFIED;

        if (ds.equals(hci) || ds.equals(mdAnderson) || ds.equals(wustl)){
            implantationSite = Standardizer.getValue("Engraftment Site", data);
        }
        return implantationSite;
    }


    public static String getMarkerPlatform(JSONObject data,String ds) throws Exception {
        String markerPlatform = Standardizer.NOT_SPECIFIED;
        if (ds.equals(mdAnderson) || ds.equals(wustl)){
            markerPlatform = data.getString("Marker Platform");
        }
        return markerPlatform;
    }


    public static String getMarkerStr(JSONObject data,String ds) throws Exception {
        String markerStr = Standardizer.NOT_SPECIFIED;

        if (ds.equals(mdAnderson)){
            markerStr = data.getString("Markers");
        }

        return markerStr;
    }


    public static String getQAPassage(JSONObject data,String ds) throws Exception {
        String passage = Standardizer.NOT_SPECIFIED;
        if (ds.equals(mdAnderson)){
            passage = data.getString("QA Passage").replaceAll("P", "");
        }
        return passage;
    }







}
