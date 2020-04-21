package org.pdxfinder.dataloaders.updog.tablevalidation;

import org.apache.commons.collections4.CollectionUtils;
import org.pdxfinder.dataloaders.updog.tablevalidation.error.BrokenRelationErrorCreator;
import org.pdxfinder.dataloaders.updog.tablevalidation.error.DuplicateValueErrorCreator;
import org.pdxfinder.dataloaders.updog.tablevalidation.error.EmptyValueErrorCreator;
import org.pdxfinder.dataloaders.updog.tablevalidation.error.MissingColumnErrorCreator;
import org.pdxfinder.dataloaders.updog.tablevalidation.error.MissingTableErrorCreator;
import org.pdxfinder.dataloaders.updog.tablevalidation.error.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class Validator {

    private static final Logger log = LoggerFactory.getLogger(Validator.class);
    private List<ValidationError> validationErrors;
    private BrokenRelationErrorCreator brokenRelationError = new BrokenRelationErrorCreator();

    public Validator() {
        this.validationErrors = new ArrayList<>();
    }

    public List<ValidationError> validate(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    ) {
        checkRequiredTablesPresent(tableSet, tableSetSpecification);
        if (CollectionUtils.isNotEmpty(validationErrors)) {
            log.error(
                "Not all required tables where present for {}. Aborting further validation",
                tableSetSpecification.getProvider());
            return validationErrors;
        }
        checkRequiredColumnsPresent(tableSet, tableSetSpecification);
        checkAllNonEmptyValuesPresent(tableSet, tableSetSpecification);
        checkAllUniqueColumnsForDuplicates(tableSet, tableSetSpecification);
        checkRelationsValid(tableSet, tableSetSpecification);

        return validationErrors;
    }

    private void checkRequiredTablesPresent(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    ) {
        if (tableSetSpecification.hasRequiredColumns())
            validationErrors.addAll(new MissingTableErrorCreator().create(tableSet, tableSetSpecification));
    }

    private void checkRequiredColumnsPresent(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    ) {
        if (tableSetSpecification.hasRequiredColumns())
            validationErrors.addAll(new MissingColumnErrorCreator().create(tableSet, tableSetSpecification));
    }

    private void checkAllNonEmptyValuesPresent(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    ) {
        validationErrors.addAll(new EmptyValueErrorCreator().create(tableSet, tableSetSpecification));
    }

    private void checkAllUniqueColumnsForDuplicates(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    ) {
        validationErrors.addAll(new DuplicateValueErrorCreator().create(tableSet, tableSetSpecification));
    }

    private void checkRelationsValid(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    ) {
        validationErrors.addAll(brokenRelationError.create(tableSet, tableSetSpecification));
    }

    boolean passesValidation(
        Map<String, Table> tableSet,
        TableSetSpecification tableSetSpecification
    ) {
        return validate(tableSet, tableSetSpecification).isEmpty();
    }

    List<ValidationError> getValidationErrors() {
        return this.validationErrors;
    }

}
