package org.pdxfinder.dataloaders.updog.tablevalidation;

import org.junit.Test;
import org.mockito.Mock;
import org.pdxfinder.dataloaders.updog.tablevalidation.error.*;
import org.slf4j.Logger;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ErrorReporterTest {

    private EmptyValueErrorCreator emptyValueErrorCreator = new EmptyValueErrorCreator();

    @Test
    public void truncate_givenTwoErrorsAndLimitToOne_returnsOneError() {
        int limit = 1;
        List<ValidationError> errors = new ArrayList<>();
        EmptyValueError error = emptyValueErrorCreator.create(ColumnReference.of("table", "column"), Table.create(), "Provider");
        errors.add(error);
        errors.add(error);

        ErrorReporter errorReporter = new ErrorReporter(errors).truncate(limit);
        assertEquals(limit, errorReporter.count());
    }

    @Test
    public void truncate_givenErrorsLowerThanLimit_doNotTruncate() {
        int limit = 5;
        List<ValidationError> errors = new ArrayList<>();
        EmptyValueError error = emptyValueErrorCreator.create(ColumnReference.of("table", "column"), Table.create(), "Provider");
        errors.add(error);

        ErrorReporter errorReporter = new ErrorReporter(errors).truncate(limit);
        assertEquals(errors.size(), errorReporter.count());
    }

}