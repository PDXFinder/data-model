package org.pdxfinder.graph.dao;

import java.util.ArrayList;
import java.util.List;

public class MolecularData {


    private String biotype;
    private String codingSequenceChange;
    private String variantClass;
    private String codonChange;
    private String aminoAcidChange;
    private String consequence;
    private String functionalPrediction;
    private String readDepth;
    private String alleleFrequency;
    private String chromosome;
    private String seqStartPosition;
    private String refAllele;
    private String altAllele;
    private String ucscGeneId;
    private String ncbiGeneId;
    private String ncbiTranscriptId;
    private String ensemblGeneId;
    private String ensemblTranscriptId;
    private String existingVariations;
    private String genomeAssembly;
    private String nucleotideChange;

    private String zscore;

    private String seqPosition;
    private String seqEndPosition;
    private String strand;

    private String cdsChange;
    private String type;
    private String annotation;

    private String cytogeneticsResult;
    private String microsatelliteResult;

    private String probeIDAffymetrix;

    private String cnaLog10RCNA;
    private String cnaLog2RCNA;
    private String cnaCopyNumberStatus;
    private String cnaGisticValue;
    private String cnaPicnicValue;
    private String fold_change;

    private String cytoGenFishResult;

    protected String rnaSeqCoverage;
    protected String rnaSeqFPKM;
    protected String rnaSeqTPM;
    protected String rnaSeqCount;
    protected String affyHGEAProbeId;
    protected String affyHGEAExpressionValue;
    protected String illuminaHGEAProbeId;
    protected String illuminaHGEAExpressionValue;

    private String fusionGeneSymbol;
    private String fusionGenePartner1;
    private String fusionGenePartner2;
    private String translocationNomenclature;

    private String pValue;
    private String markerStatusComment;

    private String marker;


    public MolecularData() {
    }



    /**
     * @return the chromosome
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * @return the seqPosition
     */
    public String getSeqPosition() {
        return seqPosition;
    }

    /**
     * @param seqPosition the seqPosition to set
     */
    public void setSeqPosition(String seqPosition) {
        this.seqPosition = seqPosition;
    }

    /**
     * @return the refAllele
     */
    public String getRefAllele() {
        return refAllele;
    }

    /**
     * @param refAllele the refAllele to set
     */
    public void setRefAllele(String refAllele) {
        this.refAllele = refAllele;
    }

    /**
     * @return the consequence
     */
    public String getConsequence() {
        return consequence;
    }

    /**
     * @param consequence the consequence to set
     */
    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }

    /**
     * @return the aminoAcidChange
     */
    public String getAminoAcidChange() {
        return aminoAcidChange;
    }

    /**
     * @param aminoAcidChange the aminoAcidChange to set
     */
    public void setAminoAcidChange(String aminoAcidChange) {
        this.aminoAcidChange = aminoAcidChange;
    }

    /**
     * @return the rsIdVariants
     */
    public String getExistingVariations() {
        return existingVariations;
    }

    /**
     * @param rsIdVariants the rsIdVariants to set
     */
    public void setExistingVariations(String rsIdVariants) {
        this.existingVariations = rsIdVariants;
    }

    /**
     * @return the readDepth
     */
    public String getReadDepth() {
        return readDepth;
    }

    /**
     * @param readDepth the readDepth to set
     */
    public void setReadDepth(String readDepth) {
        this.readDepth = readDepth;
    }

    /**
     * @return the alleleFrequency
     */
    public String getAlleleFrequency() {
        return alleleFrequency;
    }

    /**
     * @param alleleFrequency the alleleFrequency to set
     */
    public void setAlleleFrequency(String alleleFrequency) {
        this.alleleFrequency = alleleFrequency;
    }

    /**
     * @return the altAllele
     */
    public String getAltAllele() {
        return altAllele;
    }

    /**
     * @param altAllele the altAllele to set
     */
    public void setAltAllele(String altAllele) {
        this.altAllele = altAllele;
    }

    public String getNucleotideChange() {
        return nucleotideChange;
    }

    public void setNucleotideChange(String nucleotideChange) {
        this.nucleotideChange = nucleotideChange;
    }

    /**
     * @return the genomeAssembly
     */
    public String getGenomeAssembly() {
        return genomeAssembly;
    }

    /**
     * @param genomeAssembly the refAssembly to set
     */
    public void setGenomeAssembly(String genomeAssembly) {
        this.genomeAssembly = genomeAssembly;
    }

    public String getZscore() {
        return zscore;
    }

    public void setZscore(String zscore) {
        this.zscore = zscore;
    }

    public String getSeqStartPosition() {
        return seqStartPosition;
    }

    public void setSeqStartPosition(String seqStartPosition) {
        this.seqStartPosition = seqStartPosition;
    }

    public String getSeqEndPosition() {
        return seqEndPosition;
    }

    public void setSeqEndPosition(String seqEndPosition) {
        this.seqEndPosition = seqEndPosition;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getEnsemblTranscriptId() {
        return ensemblTranscriptId;
    }

    public void setEnsemblTranscriptId(String ensemblTranscriptId) {
        this.ensemblTranscriptId = ensemblTranscriptId;
    }

    public String getUcscGeneId() {
        return ucscGeneId;
    }

    public void setUcscGeneId(String ucscTranscriptId) {
        this.ucscGeneId = ucscTranscriptId;
    }

    public String getNcbiTranscriptId() {
        return ncbiTranscriptId;
    }

    public void setNcbiTranscriptId(String ncbiTranscriptId) {
        this.ncbiTranscriptId = ncbiTranscriptId;
    }

    public String getCdsChange() {
        return cdsChange;
    }

    public void setCdsChange(String cdsChange) {
        this.cdsChange = cdsChange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getCytogeneticsResult() {
        return cytogeneticsResult;
    }

    public void setCytogeneticsResult(String cytogeneticsResult) {
        this.cytogeneticsResult = cytogeneticsResult;
    }

    public String getMicrosatelliteResult() {
        return microsatelliteResult;
    }

    public void setMicrosatelliteResult(String microsatelliteResult) {
        this.microsatelliteResult = microsatelliteResult;
    }

    public String getProbeIDAffymetrix() {
        return probeIDAffymetrix;
    }

    public void setProbeIDAffymetrix(String probeIDAffymetrix) {
        this.probeIDAffymetrix = probeIDAffymetrix;
    }

    public String getCnaLog10RCNA() {
        return cnaLog10RCNA;
    }

    public void setCnaLog10RCNA(String cnaLog10RCNA) {
        this.cnaLog10RCNA = cnaLog10RCNA;
    }

    public String getCnaLog2RCNA() {
        return cnaLog2RCNA;
    }

    public void setCnaLog2RCNA(String cnaLog2RCNA) {
        this.cnaLog2RCNA = cnaLog2RCNA;
    }

    public String getCnaCopyNumberStatus() {
        return cnaCopyNumberStatus;
    }

    public void setCnaCopyNumberStatus(String cnaCopyNumberStatus) {
        this.cnaCopyNumberStatus = cnaCopyNumberStatus;
    }

    public String getCnaGisticValue() {
        return cnaGisticValue;
    }

    public void setCnaGisticValue(String cnaGisticValue) {
        this.cnaGisticValue = cnaGisticValue;
    }

    public String getCnaPicnicValue() {
        return cnaPicnicValue;
    }

    public void setCnaPicnicValue(String cnaPicnicValue) {
        this.cnaPicnicValue = cnaPicnicValue;
    }


    public String getCytoGenFishResult() {
        return cytoGenFishResult;
    }

    public void setCytoGenFishResult(String cytoGenFishResult) {
        this.cytoGenFishResult = cytoGenFishResult;
    }

    public String getRnaSeqCoverage() {
        return rnaSeqCoverage;
    }

    public void setRnaSeqCoverage(String rnaSeqCoverage) {
        this.rnaSeqCoverage = rnaSeqCoverage;
    }

    public String getRnaSeqFPKM() {
        return rnaSeqFPKM;
    }

    public void setRnaSeqFPKM(String rnaSeqFPKM) {
        this.rnaSeqFPKM = rnaSeqFPKM;
    }

    public String getRnaSeqTPM() {
        return rnaSeqTPM;
    }

    public void setRnaSeqTPM(String rnaSeqTPM) {
        this.rnaSeqTPM = rnaSeqTPM;
    }

    public String getRnaSeqCount() {
        return rnaSeqCount;
    }

    public void setRnaSeqCount(String rnaSeqCount) {
        this.rnaSeqCount = rnaSeqCount;
    }

    public String getAffyHGEAProbeId() {
        return affyHGEAProbeId;
    }

    public void setAffyHGEAProbeId(String affyHGEAProbeId) {
        this.affyHGEAProbeId = affyHGEAProbeId;
    }

    public String getAffyHGEAExpressionValue() {
        return affyHGEAExpressionValue;
    }

    public void setAffyHGEAExpressionValue(String affyHGEAExpressionValue) {
        this.affyHGEAExpressionValue = affyHGEAExpressionValue;
    }

    public String getIlluminaHGEAProbeId() {
        return illuminaHGEAProbeId;
    }

    public void setIlluminaHGEAProbeId(String illuminaHGEAProbeId) {
        this.illuminaHGEAProbeId = illuminaHGEAProbeId;
    }

    public String getIlluminaHGEAExpressionValue() {
        return illuminaHGEAExpressionValue;
    }

    public void setIlluminaHGEAExpressionValue(String illuminaHGEAExpressionValue) {
        this.illuminaHGEAExpressionValue = illuminaHGEAExpressionValue;
    }

    public String getFold_change() {
        return fold_change;
    }

    public void setFold_change(String fold_change) {
        this.fold_change = fold_change;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public boolean hasMarker() {
        return marker != null;
    }

    public String getBiotype() {
        return biotype;
    }

    public void setBiotype(String biotype) {
        this.biotype = biotype;
    }

    public String getCodingSequenceChange() {
        return codingSequenceChange;
    }

    public void setCodingSequenceChange(String codingSequenceChange) {
        this.codingSequenceChange = codingSequenceChange;
    }

    public String getVariantClass() {
        return variantClass;
    }

    public void setVariantClass(String variantClass) {
        this.variantClass = variantClass;
    }

    public String getCodonChange() {
        return codonChange;
    }

    public void setCodonChange(String codonChange) {
        this.codonChange = codonChange;
    }

    public String getFunctionalPrediction() {
        return functionalPrediction;
    }

    public void setFunctionalPrediction(String functionalPrediction) {
        this.functionalPrediction = functionalPrediction;
    }

    public String getNcbiGeneId() {
        return ncbiGeneId;
    }

    public void setNcbiGeneId(String ncbiGeneId) {
        this.ncbiGeneId = ncbiGeneId;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    @Override public String toString() {
        return String.format("[%s]", this.marker);
    }

    public String getMarkerName(MolecularData molecularData) {
        return this.marker;
    }

    public String getFusionGeneSymbol() {
        return fusionGeneSymbol;
    }

    public void setFusionGeneSymbol(String fusionGeneSymbol) {
        this.fusionGeneSymbol = fusionGeneSymbol;
    }

    public String getFusionGenePartner1() {
        return fusionGenePartner1;
    }

    public void setFusionGenePartner1(String fusionGenePartner1) {
        this.fusionGenePartner1 = fusionGenePartner1;
    }

    public String getFusionGenePartner2() {
        return fusionGenePartner2;
    }

    public void setFusionGenePartner2(String fusionGenePartner2) {
        this.fusionGenePartner2 = fusionGenePartner2;
    }

    public String getTranslocationNomenclature() {
        return translocationNomenclature;
    }

    public void setTranslocationNomenclature(String translocationNomenclature) {
        this.translocationNomenclature = translocationNomenclature;
    }

    public String getpValue() {
        return pValue;
    }

    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    public String getMarkerStatusComment() {
        return markerStatusComment;
    }

    public void setMarkerStatusComment(String markerStatusComment) {
        this.markerStatusComment = markerStatusComment;
    }

    public static List<String> getAminoAcidChangesFromMolecularDataList(List<MolecularData> molecularDataList){
        List<String> markerList = new ArrayList<>();
        molecularDataList.forEach( mData -> markerList.add(mData.getAminoAcidChange()) );
        return markerList;
    }

    public static List<String> getMarkersFromMolecularDataList(List<MolecularData> molecularDataList){
        List<String> markerList = new ArrayList<>();
        molecularDataList.forEach( mData -> markerList.add(mData.getMarker()) );
        return markerList;
    }
}
