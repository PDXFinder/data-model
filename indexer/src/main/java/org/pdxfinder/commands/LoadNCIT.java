package org.pdxfinder.commands;

/**
 * Created by csaba on 22/08/2017.
 */

import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONObject;
import org.pdxfinder.dao.OntologyTerm;
import org.pdxfinder.irccdatamodel.IRCCPatient;
import org.pdxfinder.utilities.LoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@Component
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class LoadNCIT implements CommandLineRunner {

    private final static Logger log = LoggerFactory.getLogger(LoadDiseaseOntology.class);

    private static final String diseasesBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C7057";
    private static final String ontologyUrl = "http://www.ebi.ac.uk/ols/api/ontologies/ncit/terms/";

    @Value("${ncitpredef.file}")
    private String ncitFile;

    private LoaderUtils loaderUtils;

    @Autowired
    public LoadNCIT(LoaderUtils loaderUtils) {
        this.loaderUtils = loaderUtils;
    }

    @Override
    public void run(String... args) throws Exception {

        log.info(args[0]);

        //http://www.ebi.ac.uk/ols/api/ontologies/ncit/terms/http%253A%252F%252Fpurl.obolibrary.org%252Fobo%252FNCIT_C7057/hierarchicalChildren
        //http://www.ebi.ac.uk/ols/api/ontologies/doid/terms/http%253A%252F%252Fpurl.obolibrary.org%252Fobo%252FNCIT_C7057/hierarchicalChildren
        if ("loadNCIT".equals(args[0]) || "-loadNCIT".equals(args[0])) {

            log.info("Loading all Disease, Disorder or Finding subnodes.");
            long startTime = System.currentTimeMillis();
            loadNCIT();
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            int seconds = (int) (totalTime / 1000) % 60 ;
            int minutes = (int) ((totalTime / (1000*60)) % 60);

            log.info("Loading finished after " + minutes + " minute(s) and " + seconds + " second(s)");
        }
        else if("loadNCITPreDef".equals(args[0]) || "-loadNCITPreDef".equals(args[0])){

            log.info("Loading predefined nodes from NCIT.");
            long startTime = System.currentTimeMillis();
            loadNCITPreDef();
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            int seconds = (int) (totalTime / 1000) % 60 ;
            int minutes = (int) ((totalTime / (1000*60)) % 60);

            log.info("Loading finished after " + minutes + " minute(s) and " + seconds + " second(s)");

        }
        else{
            log.info("Not loading disease ontology");
        }

    }



    private void loadNCIT(){

        Set<String> loadedTerms = new HashSet<>();
        Set<OntologyTerm> discoveredTerms = new HashSet<>();

        String diseaseRootLabel = "Disease, Disorder or Finding";

        int termCounter = 1;
        int requestCounter = 0;

        //create cancer root term
        OntologyTerm ot = loaderUtils.getOntologyTerm(diseasesBranchUrl,diseaseRootLabel);
        System.out.println("Creating node: "+diseaseRootLabel);

        discoveredTerms.add(ot);

        while(discoveredTerms.size()>0){
            //get term from notVisited

            OntologyTerm notYetVisitedTerm = discoveredTerms.iterator().next();
            discoveredTerms.remove(notYetVisitedTerm);

            if(loadedTerms.contains(notYetVisitedTerm.getUrl())) continue;

            loadedTerms.add(notYetVisitedTerm.getUrl());

            String parentUrlEncoded = "";
            try {
                //have to double encode the url to get the desired result
                parentUrlEncoded = URLEncoder.encode(notYetVisitedTerm.getUrl(), "UTF-8");
                parentUrlEncoded = URLEncoder.encode(parentUrlEncoded, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = ontologyUrl+parentUrlEncoded+"/hierarchicalChildren?size=100";

            System.out.println("Getting data from "+url);

            String json = parseURL(url);
            requestCounter++;
            try {
                JSONObject job = new JSONObject(json);
                if (!job.has("_embedded")) continue;
                String embedded = job.getString("_embedded");

                //if this term does not have child nodes, continue

                JSONObject job2 = new JSONObject(embedded);
                JSONArray terms = job2.getJSONArray("terms");


                for (int i = 0; i < terms.length(); i++) {

                    JSONObject term = terms.getJSONObject(i);
                    System.out.println("TERM: "+term.getString("label"));

                    OntologyTerm newTerm = loaderUtils.getOntologyTerm(term.getString("iri"), term.getString("label"));

                    JSONArray synonyms = term.getJSONArray("synonyms");
                    Set<String> synonymsSet = new HashSet<>();

                    for(int j=0; j<synonyms.length();j++){
                        synonymsSet.add(synonyms.getString(j));
                    }

                    newTerm.setSynonyms(synonymsSet);
                    discoveredTerms.add(newTerm);

                    OntologyTerm parentTerm = loaderUtils.getOntologyTerm(notYetVisitedTerm.getUrl());
                    newTerm.addSubclass(parentTerm);
                    loaderUtils.saveOntologyTerm(newTerm);

                    termCounter++;
                }

            } catch (Exception e) {
                log.error("", e);

            }

            System.out.println("Requests made: " + requestCounter);

        }

    }




    private void loadNCITPreDef(){

        String currentLine;
        long currentLineCounter = 1;
        String[] rowData;

        Map<String, OntologyTerm> ncitLoaded = new HashMap<>();

        try {
            BufferedReader buf = new BufferedReader(new FileReader(ncitFile));

            while (true) {
                currentLine = buf.readLine();
                if (currentLine == null) {
                    break;
                } else if (currentLineCounter < 2) {
                    currentLineCounter++;
                    continue;

                } else {
                    rowData = currentLine.split("\t");
                    String url = rowData[0];
                    String label = rowData[1];

                    OntologyTerm ot = loaderUtils.getOntologyTerm(url, label);
                    ncitLoaded.put(url, ot);

                    log.info("Creating "+label);
                }
                currentLineCounter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (Map.Entry<String, OntologyTerm> entry : ncitLoaded.entrySet()) {
            String key = entry.getKey();
            OntologyTerm parentTerm = entry.getValue();


            String parentUrlEncoded = "";
            try {
                //have to double encode the url to get the desired result
                parentUrlEncoded = URLEncoder.encode(parentTerm.getUrl(), "UTF-8");
                parentUrlEncoded = URLEncoder.encode(parentUrlEncoded, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = ontologyUrl+parentUrlEncoded+"/hierarchicalChildren?size=100";

            System.out.println("Getting data from "+url);

            String json = parseURL(url);

            try {
                JSONObject job = new JSONObject(json);
                if (!job.has("_embedded")) continue;
                String embedded = job.getString("_embedded");

                //if this term does not have child nodes, continue

                JSONObject job2 = new JSONObject(embedded);
                JSONArray terms = job2.getJSONArray("terms");


                for (int i = 0; i < terms.length(); i++) {

                    JSONObject term = terms.getJSONObject(i);

                    String childTermUrl = term.getString("iri");

                    if(!ncitLoaded.containsKey(childTermUrl)) continue;

                    OntologyTerm childTerm = ncitLoaded.get(childTermUrl);

                    JSONArray synonyms = term.getJSONArray("synonyms");
                    Set<String> synonymsSet = new HashSet<>();

                    for(int j=0; j<synonyms.length();j++){
                        synonymsSet.add(synonyms.getString(j));
                    }

                    childTerm.setSynonyms(synonymsSet);

                    childTerm.addSubclass(parentTerm);
                    loaderUtils.saveOntologyTerm(childTerm);

                }

            } catch (Exception e) {
                log.error("", e);

            }

        }

/*
        //build relationships
        for(OntologyTerm parentTerm:ncitLoaded){


            String parentUrlEncoded = "";
            try {
                //have to double encode the url to get the desired result
                parentUrlEncoded = URLEncoder.encode(parentTerm.getUrl(), "UTF-8");
                parentUrlEncoded = URLEncoder.encode(parentUrlEncoded, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = ontologyUrl+parentUrlEncoded+"/hierarchicalChildren?size=100";

            System.out.println("Getting data from "+url);

            String json = parseURL(url);

            try {
                JSONObject job = new JSONObject(json);
                if (!job.has("_embedded")) continue;
                String embedded = job.getString("_embedded");

                //if this term does not have child nodes, continue

                JSONObject job2 = new JSONObject(embedded);
                JSONArray terms = job2.getJSONArray("terms");


                for (int i = 0; i < terms.length(); i++) {

                    JSONObject term = terms.getJSONObject(i);

                    OntologyTerm childTerm = loaderUtils.getOntologyTerm(term.getString("iri"));

                    if(childTerm == null) continue;

                    JSONArray synonyms = term.getJSONArray("synonyms");
                    Set<String> synonymsSet = new HashSet<>();

                    for(int j=0; j<synonyms.length();j++){
                        synonymsSet.add(synonyms.getString(j));
                    }

                    childTerm.setSynonyms(synonymsSet);

                    childTerm.addSubclass(parentTerm);
                    loaderUtils.saveOntologyTerm(childTerm);

                }

            } catch (Exception e) {
                log.error("", e);

            }


        }

        */


    }

/*

TOTAL terms: 2001
TOTAL relationships: 2731
Loading finished after 2 minute(s) and 40 second(s)

Deleted 2119 nodes, deleted 2731 relationships, statement completed in 32 ms.
 */

/*
private void loadDO(){

    Set<OntologyTerm> loadedTerms = new HashSet<>();

    int termCounter = 1;
    String cancerRootLabel = "cancer";
    int relationshipCounter = 0;

    //create cancer root term
    OntologyTerm ot = loaderUtils.getOntologyTerm(cancerBranchUrl,cancerRootLabel);
    System.out.println("Creating node: "+cancerRootLabel);

    String cancerUrlEncoded = "";

    try {
        //have to double encode the url to get the desired result
        cancerUrlEncoded = URLEncoder.encode(ot.getUrl(), "UTF-8");
        cancerUrlEncoded = URLEncoder.encode(cancerUrlEncoded, "UTF-8");

    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }

    int totalPages = 1;
    boolean totalPagesDetermined = false;

    for (int p=0;p<totalPages;p++){

        String url = ontologyUrl+cancerUrlEncoded+"/descendants?size=500&page="+p;

        System.out.println("Getting data from "+url);

        String json = parseURL(url);

        //First create all nodes
        try {
            JSONObject job = new JSONObject(json);

            String embedded = job.getString("_embedded");

            JSONObject job2 = new JSONObject(embedded);
            JSONArray terms = job2.getJSONArray("terms");

            for (int i = 0; i < terms.length(); i++) {

                JSONObject term = terms.getJSONObject(i);
                System.out.println("Creating term: "+term.getString("label"));

                OntologyTerm newTerm = loaderUtils.getOntologyTerm(term.getString("iri"), term.getString("label"));
                loadedTerms.add(newTerm);

                termCounter++;
            }

            if(!totalPagesDetermined){
                String page = job.getString("page");
                JSONObject pageObj = new JSONObject(page);
                totalPages = Integer.parseInt(pageObj.getString("totalPages"))-1;
                totalPagesDetermined = true;
            }


        } catch (Exception e) {
            log.error("", e);

        }

    }



    //then create relationships

    String urlEnc = "";
    for (OntologyTerm t : loadedTerms) {

        OntologyTerm childTerm = loaderUtils.getOntologyTerm(t.getUrl());

        try {
            urlEnc = URLEncoder.encode(childTerm.getUrl(), "UTF-8");
            urlEnc = URLEncoder.encode(urlEnc, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String parentUrl = ontologyUrl+urlEnc+"/parents";

        System.out.println("Getting parents data from "+parentUrl);

        String parentJson = parseURL(parentUrl);

        try {
            JSONObject job = new JSONObject(parentJson);

            String embedded = job.getString("_embedded");

            JSONObject job2 = new JSONObject(embedded);
            JSONArray terms = job2.getJSONArray("terms");

            for (int i = 0; i < terms.length(); i++) {

                JSONObject term = terms.getJSONObject(i);


                OntologyTerm parentTerm = loaderUtils.getOntologyTerm(term.getString("iri"), term.getString("label"));
                childTerm.addSubclass(parentTerm);
                loaderUtils.saveOntologyTerm(childTerm);
                relationshipCounter++;
            }

            System.out.println("Creating relationships for: "+childTerm.getLabel());



        } catch (Exception e) {
            log.error("", e);

        }

    }


    System.out.println("TOTAL terms: "+termCounter);
    System.out.println("TOTAL relationships: "+relationshipCounter);

}
*/

    private String parseURL(String urlStr) {
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            log.error("Unable to read from URL " + urlStr, e);
        }
        return sb.toString();
    }




}