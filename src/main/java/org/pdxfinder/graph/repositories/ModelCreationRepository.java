package org.pdxfinder.graph.repositories;

import org.pdxfinder.graph.dao.ModelCreation;
import org.pdxfinder.graph.dao.MolecularCharacterization;
import org.pdxfinder.graph.dao.Sample;
import org.pdxfinder.graph.dao.TreatmentSummary;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface describing operations for adding/finding/deleting PDX strain records
 *
 * @author csaba
 */
@Repository
public interface ModelCreationRepository extends Neo4jRepository<ModelCreation, Long> {


    ModelCreation findBySourcePdxId(@Param("modelId") String modelId);

    @Query("MATCH (model:ModelCreation) WHERE toLower(model.dataSource) = toLower({dataSource}) AND toLower(model.sourcePdxId) = toLower({modelId}) " +
            "WITH model " +
            "OPTIONAL MATCH (model)-[spr:SPECIMENS]-(sp:Specimen)-[hsr:HOST_STRAIN]-(hs:HostStrain) " +
            "OPTIONAL MATCH (model)-[qar:QUALITY_ASSURED_BY]-(qa:QualityAssurance) "+
            "OPTIONAL MATCH (model)-[url:EXTERNAL_URL]-(ext_url:ExternalUrl) "+
            "OPTIONAL MATCH (model)-[grp:GROUP]-(group:Group) "+
            "WITH model, spr, sp, hsr, hs, qar, qa, url, ext_url, grp, group " +
            "OPTIONAL MATCH (sp)-[itr:ENGRAFTMENT_TYPE]-(it:EngraftmentType) "+
            "OPTIONAL MATCH (sp)-[isr:ENGRAFTMENT_SITE]-(is:EngraftmentSite) "+
            "OPTIONAL MATCH (sp)-[ism:ENGRAFTMENT_MATERIAL]-(im:EngraftmentMaterial) "+

            "RETURN model, spr, sp, hsr, hs, itr, isr, it, is, qar, qa, url, ext_url, grp, group, ism, im")
    ModelCreation findByDataSourceAndSourcePdxId(@Param("dataSource") String dataSource, @Param("modelId") String modelId);

    @Query("MATCH (model:ModelCreation) WHERE model.dataSource = {ds} " +
            "WITH model ORDER by model.sourcePdxId" +
            "OPTIONAL MATCH (model)-[spr:SPECIMENS]-(sp:Specimen)-[hsr:HOST_STRAIN]-(hs:HostStrain) " +
            "OPTIONAL MATCH (model)-[qar:QUALITY_ASSURED_BY]-(qa:QualityAssurance) "+
            "OPTIONAL MATCH (model)-[url:EXTERNAL_URL]-(ext_url:ExternalUrl) "+
            "OPTIONAL MATCH (model)-[gr:GROUP]-(g:Group) "+
            "WITH model, spr, sp, hsr, hs, qar, qa, url, ext_url, gr, g " +
            "OPTIONAL MATCH (sp)-[itr:ENGRAFTMENT_TYPE]-(it:EngraftmentType) "+
            "OPTIONAL MATCH (sp)-[isr:ENGRAFTMENT_SITE]-(is:EngraftmentSite) "+
            "OPTIONAL MATCH (sp)-[ism:ENGRAFTMENT_MATERIAL]-(im:EngraftmentMaterial) "+

            "RETURN model, spr, sp, hsr, hs, itr, isr, it, is, qar, qa, url, ext_url, ism, im, gr, g ")
    List<ModelCreation> findModelsWithSpecimensAndQAByDS(@Param("ds") String ds);


    @Query("MATCH (model:ModelCreation) WHERE model.dataSource = {ds} " +
            "WITH model " +

            "OPTIONAL MATCH (model)-[url:EXTERNAL_URL]-(ext_url:ExternalUrl) "+
            "OPTIONAL MATCH (model)-[gr:GROUP]-(g:Group) "+

            "RETURN model, url, ext_url, gr, g ORDER by model.sourcePdxId")
    List<ModelCreation> findModelsWithSharingAndContactByDS(@Param("ds") String ds);

    @Query("MATCH (model:ModelCreation) WHERE model.sourcePdxId = {modelId} AND model.dataSource = {dataSource} RETURN model ")
    ModelCreation findBySourcePdxIdAndDataSource(@Param("modelId") String modelId, @Param("dataSource") String dataSource);

    @Query("MATCH (model:ModelCreation) return count(model) ")
    int countAllModels();


    @Query("MATCH (mod:ModelCreation) where toLower(mod.dataSource) = toLower({datasource}) RETURN count(mod)")
    int getModelCountByDataSource(@Param("datasource") String dataSource);

    @Query("MATCH (s:Sample)-[i:IMPLANTED_IN]-(mod:ModelCreation) " +
            "WHERE id(s) = {sample} " +
            "RETURN mod")
    ModelCreation findBySample(@Param("sample") Sample sample);


    //disable filtering on markers
    @Query("MATCH (humSample:Sample)-[i:IMPLANTED_IN]-(mod:ModelCreation)" +
            "WHERE (toLower(humSample.diagnosis) CONTAINS toLower({diag}) OR {diag}='') " +
            "AND (humSample.dataSource IN {dataSource} OR {dataSource}=[] )"+
            "WITH mod, humSample, i " +

            "MATCH (humSample)-[o:ORIGIN_TISSUE]-(t:Tissue) " +
            "MATCH (humSample)-[ot:OF_TYPE]-(tt:TumorType) " +
            "WHERE (tt.name IN {tumorType} OR {tumorType}=[])  " +
            "OPTIONAL MATCH (humSample)-[mto:MAPPED_TO]-(oterm:OntologyTerm) " +
            "RETURN humSample, i, mod, o, t, ot, tt, mto, oterm ")
    Collection<ModelCreation> findByMultipleFilters(@Param("diag") String diag, @Param("markers") String[] markers,
                                                    @Param("dataSource") String[] dataSource, @Param("tumorType") String[] tumorType);




    //Ontology powered search: returns less data to improve performance
    @Query("MATCH (term:OntologyTerm)<-[*0..]-(child:OntologyTerm)-[mapp:MAPPED_TO]-(humSample:Sample)-[i:IMPLANTED_IN]-(mod:ModelCreation) " +
            "        WHERE term.label = {query} " +
            "        AND (humSample.dataSource IN {dataSource} OR {dataSource}=[]) " +
            "        WITH humSample,i,mod,mapp,child " +

            "        MATCH (humSample)-[o:ORIGIN_TISSUE]-(t:Tissue) " +
            "        MATCH (humSample)-[ot:OF_TYPE]-(tt:TumorType) " +
            "        WHERE (tt.name IN {tumorType} OR {tumorType}=[])  " +
            "        return humSample, i, mod, o, t, ot, tt,mapp,child ")
    Collection<ModelCreation> findByOntology(@Param("query") String query, @Param("markers") String[] markers,
                                             @Param("dataSource") String[] dataSource, @Param("tumorType") String[] tumorType);

    @Query("MATCH (n:ModelCreation) RETURN n")
    Collection<ModelCreation> findAllModels();

    @Query("MATCH (mc:ModelCreation)<-[ir:IMPLANTED_IN]-(s:Sample)-[sfr:SAMPLED_FROM]-(ps:PatientSnapshot)-[pr:COLLECTION_EVENT]-(p:Patient) " +
            "WITH mc, ir, s, sfr, ps, pr, p " +
            "MATCH (c:Tissue)-[cr:SAMPLE_SITE]-(s)-[ttr:OF_TYPE]-(tt:TumorType) " +
            "WITH mc, ir, s, sfr, ps, pr, p, cr, c, ttr, tt " +
            "MATCH (t:Tissue)-[tr:ORIGIN_TISSUE]-(s)-[otm:MAPPED_TO]-(ot:OntologyTerm)-[ottm:SUBCLASS_OF *0..]->(term:OntologyTerm) " +
            "OPTIONAL MATCH (mc)-[gr:GROUP]-(g:Group) " +
            "RETURN mc, ir, s, sfr, ps, pr, p, cr, c, ttr, tt, tr, t, otm, ot, ottm, term, gr, g")
    Collection<ModelCreation> findModelsWithPatientData();

    @Query("MATCH " +
            "(mc:ModelCreation)-[msr:MODEL_SAMPLE_RELATION]-(s:Sample)" +
            "-[cbr:CHARACTERIZED_BY]-(molChar:MolecularCharacterization)" +
            "-[pur:PLATFORM_USED]-(p:Platform) " +
            "RETURN mc, msr, s, cbr, molChar, pur, p")
    Collection<ModelCreation> findAllModelsPlatforms();


    @Query("MATCH (model:ModelCreation)--(s:Sample)--(molch:MolecularCharacterization) " +
            "WHERE id(molch) = {mc} " +
            "RETURN model")
    ModelCreation findByMolChar(@Param("mc") MolecularCharacterization mc);

    @Query("MATCH (model:ModelCreation)-[sr:MODEL_SAMPLE_RELATION]-(s:Sample)--(molch:MolecularCharacterization) " +
            "WHERE id(molch) = {mc} " +
            "RETURN model, sr, s")
    ModelCreation findModelWithSampleByMolChar(@Param("mc") MolecularCharacterization mc);

    @Query("MATCH (mod:ModelCreation) WHERE toLower(mod.dataSource) = toLower({dataSource}) " +
            "WITH mod " +
            "MATCH (mod)-[msr:MODEL_SAMPLE_RELATION]-(s:Sample)-[cbr:CHARACTERIZED_BY]-(mc:MolecularCharacterization)--(ma:MarkerAssociation) " +
            "WITH mod, msr, s, cbr, mc " +
            "MATCH (mc)-[pur:PLATFORM_USED]-(pl:Platform) " +
            "RETURN mod, msr, s, cbr, mc, pur, pl")
    Collection<ModelCreation> getModelsWithMolCharBySource(@Param("dataSource") String dataSource);

    @Query("MATCH (mod:ModelCreation) WHERE toLower(mod.dataSource) = toLower({dataSource}) " +
            "WITH mod ORDER BY mod.sourcePdxId " +
            "OPTIONAL MATCH (mod)-[iir:IMPLANTED_IN]-(psamp:Sample)-[cbr2:CHARACTERIZED_BY]-(mc2:MolecularCharacterization) " +
            "OPTIONAL MATCH (mod)-[spr:SPECIMENS]-(sp:Specimen)-[hsr:HOST_STRAIN]-(hs:HostStrain) " +
            "WITH mod, iir, psamp, spr, sp, cbr2, mc2, hsr, hs " +
            "OPTIONAL MATCH (sp)-[sfr:SAMPLED_FROM]-(s:Sample)-[cbr:CHARACTERIZED_BY]-(mc:MolecularCharacterization) " +
            "OPTIONAL MATCH (mc)-[pur:PLATFORM_USED]-(pl:Platform) " +
            "OPTIONAL MATCH (mc2)-[pur2:PLATFORM_USED]-(pl2:Platform) " +
            "RETURN mod, iir, psamp, spr, sp, sfr, s, cbr, mc, mc2, cbr2, pur, pl, pur2, pl2, hs, hsr")
    List<ModelCreation> findModelPlatformSampleByDS(@Param("dataSource") String dataSource);


    @Query("MATCH (mod:ModelCreation) WHERE toLower(mod.dataSource) = toLower({dataSource}) " +
            "WITH mod " +
            "MATCH (mod)--(samp:Sample)-[cbr:CHARACTERIZED_BY]-(mc:MolecularCharacterization)-[assoc:ASSOCIATED_WITH]->(mAss:MarkerAssociation) " +
            "WHERE mc.type = {type}  "+
            "WITH mod, samp, cbr, mc, assoc, mAss " +
            "OPTIONAL MATCH (mod)-[iir:IMPLANTED_IN]-(psamp:Sample) " +
            "WITH iir, psamp, mod, samp, cbr, mc, assoc, mAss " +
            "OPTIONAL MATCH (mod)-[spr:SPECIMENS]-(sp:Specimen)-[sfr:SAMPLED_FROM]-(s:Sample)-[cbr2:CHARACTERIZED_BY]-(mc2:MolecularCharacterization)-[assoc2:ASSOCIATED_WITH]->(mAss2:MarkerAssociation) " +
            "WHERE mc2.type = {type} "+
            "WITH iir, psamp, mod, samp, spr, sp, sfr, s, cbr, mc, mc2, cbr2, assoc, mAss, assoc2, mAss2 " +
            "OPTIONAL MATCH (sp)-[hsr:HOST_STRAIN]-(hs:HostStrain) " +
            "OPTIONAL MATCH (mc)-[pur:PLATFORM_USED]-(pl:Platform) " +
            "OPTIONAL MATCH (mc2)-[pur2:PLATFORM_USED]-(pl2:Platform) " +
            "RETURN mod, iir, psamp, samp, spr, sp, sfr, s, cbr, mc, mc2, cbr2, pur, pl, pur2, pl2, assoc, mAss, assoc2, mAss2, hsr, hs ")
    List<ModelCreation> findModelsWithMolecularDataByDSAndMolcharType(@Param("dataSource") String dataSource, @Param("type") String type);

    @Query("MATCH (mod:ModelCreation) WHERE toLower(mod.dataSource) = toLower({dataSource})  " +
            "WITH mod SKIP {from} LIMIT {to}" +
            "MATCH (mod)-[msr:MODEL_SAMPLE_RELATION]-(s:Sample)-[cbr:CHARACTERIZED_BY]-(mc:MolecularCharacterization)--(ma:MarkerAssociation) " +
            "WITH mod, msr, s, cbr, mc " +
            "MATCH (mc)-[pur:PLATFORM_USED]-(pl:Platform) " +
            "RETURN mod, msr, s, cbr, mc, pur, pl")
    Collection<ModelCreation> getModelsWithMolCharBySourceFromTo(@Param("dataSource") String dataSource, @Param("from") int from, @Param("to") int to);

    @Query("MATCH (model:ModelCreation)--(ts:TreatmentSummary) " +
            "WHERE id(ts) = {ts} " +
            "RETURN model")
    ModelCreation findByTreatmentSummary(@Param("ts") TreatmentSummary ts);

    @Query("MATCH (model:ModelCreation)--(s:Sample)--(ps:PatientSnapshot)--(ts:TreatmentSummary) " +
            "WHERE id(ts) = {ts} " +
            "RETURN model")
    Collection<ModelCreation> findByPatientTreatmentSummary(@Param("ts") TreatmentSummary ts);


    @Query("MATCH (model:ModelCreation) WHERE model.sourcePdxId = {modelId} AND model.dataSource = {dataSource} " +
            "WITH model " +
            "OPTIONAL MATCH (model)-[spr:SPECIMENS]-(sp:Specimen)-[hsr:HOST_STRAIN]-(hs:HostStrain) " +
            "WITH model, spr, sp, hsr, hs " +
            "OPTIONAL MATCH (sp)-[sfr:SAMPLED_FROM]-(s:Sample) " +
            "RETURN model, spr, sp, hsr, hs, sfr, s")
    ModelCreation findBySourcePdxIdAndDataSourceWithSpecimensAndHostStrain(@Param("modelId") String modelId, @Param("dataSource") String dataSource);

    @Query("MATCH (psamp:Sample)-[ir:IMPLANTED_IN]-(model:ModelCreation) WHERE model.sourcePdxId = {modelId} AND model.dataSource = {dataSource} " +
            "WITH model, psamp, ir " +
            "OPTIONAL MATCH (model)-[spr:SPECIMENS]-(sp:Specimen)-[hsr:HOST_STRAIN]-(hs:HostStrain) " +
            "WITH model, spr, sp, hsr, hs, psamp, ir " +
            "OPTIONAL MATCH (sp)-[sfr:SAMPLED_FROM]-(s:Sample) " +
            "OPTIONAL MATCH (s)-[cbr:CHARACTERIZED_BY]-(mc:MolecularCharacterization)-[pur:PLATFORM_USED]-(pl:Platform) " +
            "OPTIONAL MATCH (psamp)-[cbr2:CHARACTERIZED_BY]-(mc2:MolecularCharacterization)-[pur2:PLATFORM_USED]-(pl2:Platform) " +
            "RETURN model, spr, sp, hsr, hs, sfr, s, psamp, ir, cbr, mc, pur, pl, cbr2, mc2, pur2, pl2")
    ModelCreation findBySourcePdxIdAndDataSourceWithSamplesAndSpecimensAndHostStrain(@Param("modelId") String modelId, @Param("dataSource") String dataSource);

    @Query("MATCH (model:ModelCreation)-[msr:MODEL_SAMPLE_RELATION]-(samp:Sample)-[cby:CHARACTERIZED_BY]-(molchar:MolecularCharacterization)-[asw:ASSOCIATED_WITH]-(massoc:MarkerAssociation)-[mark:MARKER]-(marker:Marker) WHERE molchar.type={molcharType} " +
            "RETURN model, msr, samp, cby, molchar, asw, massoc, mark, marker")
    List<ModelCreation> findByMolcharType(@Param("molcharType") String molcharType);

    @Query("MATCH (mod:ModelCreation)-[tsr:SUMMARY_OF_TREATMENT]-(ts:TreatmentSummary)-[tpr:TREATMENT_PROTOCOL]-(tp:TreatmentProtocol)-[tcr:TREATMENT_COMPONENT]-(tc:TreatmentComponent)-[dr:TREATMENT]-(d:Treatment)-[mt:MAPPED_TO]-(ot:OntologyTerm) " +
            "WHERE tc.type = {type} " +
            "RETURN mod, tsr, ts, tpr, tp, tcr, tc, dr, d, mt, ot")
    Set<ModelCreation> getModelsTreatmentsAndDrugs(@Param("type") String type);

    @Query("MATCH (model:ModelCreation)-[ii:IMPLANTED_IN]-(samp:Sample)--(ps:PatientSnapshot)-[tsr:SUMMARY_OF_TREATMENT]-(ts:TreatmentSummary)-[tpr:TREATMENT_PROTOCOL]-(tp:TreatmentProtocol)-[tcr:TREATMENT_COMPONENT]-(tc:TreatmentComponent)-[dr:TREATMENT]-(d:Treatment) " +
            "WHERE model.dataSource = {dataSource} " +
            "WITH model, ii, samp, ps, tsr, ts, tpr, tp, tcr, tc, dr, d " +
            "OPTIONAL MATCH (tp)-[resp:RESPONSE]-(res:Response) " +
            "RETURN model, ii, samp, ps, tsr, ts, tpr, tp, tcr, tc, dr, d,resp, res")
    List<ModelCreation> findModelFromPatienSnapshotWithTreatmentSummaryByDataSource(@Param("dataSource")String dataSource);

    @Query("MATCH(model:ModelCreation)-[tsr:SUMMARY_OF_TREATMENT]-(ts:TreatmentSummary)-[tpr:TREATMENT_PROTOCOL]-(tp:TreatmentProtocol)-[tcr:TREATMENT_COMPONENT]-(tc:TreatmentComponent)-[dr:TREATMENT]-(d:Treatment) " +
            "WHERE toLower(model.dataSource) = toLower({dataSource}) " +
            "WITH model,tsr, ts, tpr, tp, tcr, tc, dr, d " +
            "OPTIONAL MATCH (tp)-[resp:RESPONSE]-(res:Response) " +
            "RETURN model,tsr, ts, tpr, tp, tcr, tc, dr, d, res, resp" )
    List<ModelCreation> findModelsWithTreatmentSummaryByDataSource(@Param("dataSource") String dataSource);
}














