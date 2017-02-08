package org.pdxfinder.dao;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Represent the PDX model
 * The model will have at least one PdxPassage to capture the model creation event
 */
@NodeEntity
public class PdxStrain {

    @GraphId
    private Long id;

    private String sourcePdxId;
    private ImplantationSite implantationSite;
    private ImplantationType implantationType;
    private BackgroundStrain backgroundStrain;
    private String passage;

    @Relationship(type = "IMPLANTED_IN", direction = Relationship.INCOMING)
    private Tumor tumor;

    public PdxStrain(String sourcePdxId, ImplantationSite implantationSite, ImplantationType implantationType, Tumor tumor, BackgroundStrain backgroundStrain, String passage) {
        this.sourcePdxId = sourcePdxId;
        this.implantationSite = implantationSite;
        this.implantationType = implantationType;
        this.tumor = tumor;
        this.backgroundStrain = backgroundStrain;
        this.passage = passage;
    }

    public PdxStrain() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    public String getSourcePdxId() {
        return sourcePdxId;
    }

    public void setSourcePdxId(String sourcePdxId) {
        this.sourcePdxId = sourcePdxId;
    }

    public ImplantationSite getImplantationSite() {
        return implantationSite;
    }

    public void setImplantationSite(ImplantationSite implantationSite) {
        this.implantationSite = implantationSite;
    }

    public ImplantationType getImplantationType() {
        return implantationType;
    }

    public void setImplantationType(ImplantationType implantationType) {
        this.implantationType = implantationType;
    }

    public Tumor getTumor() {
        return tumor;
    }

    public void setTumor(Tumor tumor) {
        this.tumor = tumor;
    }

    public BackgroundStrain getBackgroundStrain() {
        return backgroundStrain;
    }

    public void setBackgroundStrain(BackgroundStrain backgroundStrain) {
        this.backgroundStrain = backgroundStrain;
    }

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }
}
