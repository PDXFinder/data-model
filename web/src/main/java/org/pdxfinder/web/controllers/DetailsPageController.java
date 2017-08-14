package org.pdxfinder.web.controllers;

import org.pdxfinder.services.SearchService;
import org.pdxfinder.services.dto.DetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by csaba on 12/05/2017.
 */
@Controller
public class DetailsPageController {

    private SearchService searchService;

    @Autowired
    public DetailsPageController(SearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping(value = "/pdx/{dataSrc}/{modelId}")
    public String details(@PathVariable String dataSrc,@PathVariable String modelId, Model model)
    {


        DetailsDTO dto = searchService.searchForModel(dataSrc,modelId);

        //if(dto.getModelId().equals("")) return "error";

        model.addAttribute("fullData",dto);

        model.addAttribute("modelId",modelId);
        /*
        this.externalId = "";
        this.dataSource = "";
        this.patientId = "";
        this.gender = "";
        this.age = "";
        this.race = "";
        this.ethnicity = "";
        this.diagnosis = "";
        this.tumorType = "";
        this.classification = "";
        this.originTissue = "";
        this.sampleSite = "";

        this.sampleType = "";
        this.strain = "";
        this.mouseSex = "";
        this.engraftmentSite = "";
        */

        model.addAttribute("externalId", dto.getExternalId());
        model.addAttribute("dataSource", dto.getDataSource());
        model.addAttribute("patientId", dto.getPatientId());
        model.addAttribute("gender", dto.getGender());
        model.addAttribute("age", dto.getAge());
        model.addAttribute("race", dto.getRace());
        model.addAttribute("ethnicity", dto.getEthnicity());
        model.addAttribute("diagnosis", dto.getDiagnosis());
        model.addAttribute("tumorType", dto.getTumorType());
        model.addAttribute("classification", dto.getClassification());
        model.addAttribute("originTissue", dto.getOriginTissue());
        model.addAttribute("sampleSite", dto.getSampleSite());

        model.addAttribute("sampleType", dto.getSampleType());
        model.addAttribute("strain", dto.getStrain());
        model.addAttribute("mouseSex", dto.getMouseSex());
        model.addAttribute("engraftmentSite", dto.getEngraftmentSite());
        model.addAttribute("markers", dto.getCancerGenomics());
        model.addAttribute("url", dto.getExternalUrl());
        model.addAttribute("urlText", dto.getExternalUrlText());

        //TODO: return error page if sampleId does not exist
        return "details";
    }
}
