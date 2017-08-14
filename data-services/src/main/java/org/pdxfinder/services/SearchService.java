package org.pdxfinder.services;


import org.pdxfinder.dao.*;
import org.pdxfinder.repositories.*;
import org.pdxfinder.services.dto.DetailsDTO;
import org.pdxfinder.services.dto.SearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {


    private SampleRepository sampleRepository;

    private PatientRepository patientRepository;
    private PatientSnapshotRepository patientSnapshotRepository;
    private ModelCreationRepository modelCreationRepository;
    private OntologyTermRepository ontologyTermRepositoryRepository;
    private final String JAX_URL = "http://tumor.informatics.jax.org/mtbwi/pdxSearch.do";
    private final String JAX_URL_TEXT = "View data at JAX";
    private final String IRCC_URL = "mailto:andrea.bertotti@unito.it?subject=";
    private final String IRCC_URL_TEXT = "Contact IRCC here";


    @Autowired
    public SearchService(SampleRepository sampleRepository, PatientRepository patientRepository,
                         PatientSnapshotRepository patientSnapshotRepository, ModelCreationRepository modelCreationRepository,
                         OntologyTermRepository ontologyTermRepository) {
        this.sampleRepository = sampleRepository;
        this.patientRepository = patientRepository;
        this.patientSnapshotRepository = patientSnapshotRepository;
        this.modelCreationRepository = modelCreationRepository;
        this.ontologyTermRepositoryRepository = ontologyTermRepository;

    }


    public int modelCount() {

        int pdxCount = modelCreationRepository.countAllModels();
        return  pdxCount;

    }


    public Boolean isExistingOntologyTerm(String query) {

        if (ontologyTermRepositoryRepository.findByLabel(query) != null) return true;
        return false;

    }


    public List<SearchDTO> searchWithOntology(String query, String[] markers, String[] datasources, String[] origintumortypes) {

        Collection<ModelCreation> models = modelCreationRepository.findByOntology(query, markers, datasources, origintumortypes);

        return createSearchResult(models, query);

    }

    public List<SearchDTO> searchForModelsWithFilters(String diag, String[] markers, String[] datasources, String[] origintumortypes) {

        Collection<ModelCreation> models = modelCreationRepository.findByMultipleFilters(diag, markers, datasources, origintumortypes);

        return createSearchResult(models, diag);
    }

    public List<SearchDTO> search(String query, String[] markers, String[] datasources, String[] origintumortypes) {

        if (isExistingOntologyTerm(query)) return searchWithOntology(query, markers, datasources, origintumortypes);

        else return searchForModelsWithFilters(query, markers, datasources, origintumortypes);

    }


    public List<SearchDTO> createSearchResult(Collection<ModelCreation> models, String query) {

        List<SearchDTO> results = new ArrayList<>();

        for (ModelCreation model : models) {

            SearchDTO sdto = new SearchDTO();

            if (model.getSourcePdxId() != null) {
                sdto.setModelId(model.getSourcePdxId());
            }

            if (model.getSample() != null && model.getSample().getDataSource() != null) {
                sdto.setDataSource(model.getSample().getDataSource());
            }

            if (model.getSample() != null && model.getSample().getSourceSampleId() != null) {
                sdto.setTumorId(model.getSample().getSourceSampleId());
            }

            if (model.getSample() != null && model.getSample().getDiagnosis() != null) {
                sdto.setDiagnosis(model.getSample().getDiagnosis());
            }

            if (model.getSample() != null && model.getSample().getOriginTissue() != null) {
                sdto.setTissueOfOrigin(model.getSample().getOriginTissue().getName());
            }

            if (model.getSample() != null && model.getSample().getType() != null) {
                sdto.setTumorType(model.getSample().getType().getName());
            }

            if (model.getSample() != null && model.getSample().getClassification() != null) {
                sdto.setClassification(model.getSample().getClassification());
            }

            sdto.setSearchParameter(query);


            if (model.getSample() != null && model.getSample().getMolecularCharacterizations() != null) {
                Set<String> markerSet = new HashSet<>();

                for (MolecularCharacterization mc : model.getSample().getMolecularCharacterizations()) {
                    for (MarkerAssociation ma : mc.getMarkerAssociations()) {
                        markerSet.add(ma.getMarker().getName());
                    }
                }
                sdto.setCancerGenomics(new ArrayList<>(markerSet));

            }

            results.add(sdto);

        }

        return results;


    }


    public DetailsDTO searchForModel(String dataCenter, String modelId) {


        Sample sample = sampleRepository.findBySourcePdxId(dataCenter,modelId);
        Patient patient = patientRepository.findByModelId(dataCenter,modelId);
        PatientSnapshot ps = patientSnapshotRepository.findByModelId(dataCenter,modelId);
        ModelCreation pdx = modelCreationRepository.findBySourcePdxId(modelId);

        DetailsDTO dto = new DetailsDTO();

                        /*
                        this.modelId = "";
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


        if (sample.getSourceSampleId() != null) {
            dto.setExternalId(sample.getSourceSampleId());
        }

        if (sample.getDataSource() != null) {
            dto.setDataSource(sample.getDataSource());
        }

        if (patient.getExternalId() != null) {
            dto.setPatientId(patient.getExternalId());
        }

        if (patient.getSex() != null) {
            dto.setGender(patient.getSex());
        }

        if (ps.getAge() != null) {
            dto.setAge(ps.getAge());
        }

        if (patient.getRace() != null) {
            dto.setRace(patient.getRace());
        }

        if (patient.getEthnicity() != null) {
            dto.setEthnicity(patient.getEthnicity());
        }

        if (sample.getDiagnosis() != null) {
            dto.setDiagnosis(sample.getDiagnosis());
        }

        if (sample.getType() != null) {
            dto.setTumorType(sample.getType().getName());
        }

        if (sample.getClassification() != null) {
            dto.setClassification(sample.getClassification());
        }

        if (sample.getOriginTissue() != null) {
            dto.setOriginTissue(sample.getOriginTissue().getName());
        }
        if (sample.getSampleSite() != null) {
            dto.setSampleSite(sample.getSampleSite().getName());
        }

        if (pdx != null && pdx.getImplantationType() != null) {
            dto.setSampleType(pdx.getImplantationType().getName());
        }

        if (pdx != null && pdx.getBackgroundStrain() != null) {
            dto.setStrain(pdx.getBackgroundStrain().getName());
        }

        if (pdx != null && pdx.getImplantationSite() != null) {
            dto.setEngraftmentSite(pdx.getImplantationSite().getName());
        }

        if (pdx != null && pdx.getSourcePdxId() != null) {
            dto.setModelId(pdx.getSourcePdxId());
        }

        if (sample.getMolecularCharacterizations() != null) {
            List<String> markerList = new ArrayList<>();

            for (MolecularCharacterization mc : sample.getMolecularCharacterizations()) {
                for (MarkerAssociation ma : mc.getMarkerAssociations()) {

                    if (ma.getDescription().equals("None")) {
                        markerList.add("None");
                    } else {
                        markerList.add(ma.getMarker().getName() + " status: " + ma.getDescription());
                    }

                }
            }
            Collections.sort(markerList);
            dto.setCancerGenomics(markerList);

        }

        if (sample.getDataSource().equals("JAX")) {
            dto.setExternalUrl(JAX_URL);
            dto.setExternalUrlText(JAX_URL_TEXT);
        } else if (sample.getDataSource().equals("IRCC")) {
            dto.setExternalUrl(IRCC_URL + dto.getExternalId());
            dto.setExternalUrlText(IRCC_URL_TEXT);
        }


        return dto;
    }


}
