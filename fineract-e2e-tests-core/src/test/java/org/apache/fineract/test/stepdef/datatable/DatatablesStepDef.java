/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.test.stepdef.datatable;

import static java.util.function.Function.identity;
import static org.apache.fineract.client.feign.util.FeignCalls.ok;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.fineract.client.feign.FeignException;
import org.apache.fineract.client.feign.FineractFeignClient;
import org.apache.fineract.client.models.GetDataTablesResponse;
import org.apache.fineract.client.models.PostColumnHeaderData;
import org.apache.fineract.client.models.PostDataTablesAppTableIdResponse;
import org.apache.fineract.client.models.PostDataTablesRequest;
import org.apache.fineract.client.models.PostDataTablesResponse;
import org.apache.fineract.client.models.PostWorkingCapitalLoanProductsResponse;
import org.apache.fineract.client.models.PostWorkingCapitalLoansResponse;
import org.apache.fineract.client.models.ResultsetColumnHeaderData;
import org.apache.fineract.test.data.datatable.DatatableColumnType;
import org.apache.fineract.test.data.datatable.DatatableEntityType;
import org.apache.fineract.test.data.datatable.DatatableNameGenerator;
import org.apache.fineract.test.stepdef.AbstractStepDef;
import org.apache.fineract.test.support.TestContextKey;

@RequiredArgsConstructor
public class DatatablesStepDef extends AbstractStepDef {

    public static final String CREATE_DATATABLE_RESULT_KEY = "CreateDatatableResult";
    public static final String DATATABLE_NAME = "DatatableId";
    public static final String DATATABLE_QUERY_RESPONSE = "DatatableQueryResponse";
    public static final String DATATABLE_ENTRY_ID = "DatatableEntryId";

    private final FineractFeignClient fineractClient;
    private final DatatableNameGenerator datatableNameGenerator;

    @When("A datatable for {string} is created")
    public void whenDatatableCreated(final String entityTypeStr) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final List<PostColumnHeaderData> columns = createRandomDatatableColumnsRequest();
        final PostDataTablesRequest request = createDatatableRequest(entityType, columns);

        final PostDataTablesResponse response = ok(() -> fineractClient.dataTables().createDatatable(request, Map.of()));

        testContext().set(CREATE_DATATABLE_RESULT_KEY, response);
        testContext().set(DATATABLE_NAME, response.getResourceIdentifier());
    }

    @When("A datatable for {string} is created with the following extra columns:")
    public void whenDatatableCreatedWithFollowingExtraColumns(final String entityTypeStr, final DataTable dataTable) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final List<List<String>> rows = dataTable.asLists();
        final List<List<String>> rowsWithoutHeader = rows.subList(1, rows.size());
        final List<PostColumnHeaderData> columns = createDatatableColumnsRequest(rowsWithoutHeader);
        final PostDataTablesRequest request = createDatatableRequest(entityType, columns);

        final PostDataTablesResponse response = ok(() -> fineractClient.dataTables().createDatatable(request, Map.of()));

        testContext().set(CREATE_DATATABLE_RESULT_KEY, response);
        testContext().set(DATATABLE_NAME, response.getResourceIdentifier());
    }

    private List<PostColumnHeaderData> createDatatableColumnsRequest(final List<List<String>> rowsWithoutHeader) {
        return rowsWithoutHeader.stream().map(row -> {
            final String columnName = row.get(0);
            final DatatableColumnType columnType = DatatableColumnType.fromTypeString(row.get(1));
            final long columnLength = Long.parseLong(row.get(2));
            final boolean unique = BooleanUtils.toBoolean(row.get(3));
            final boolean indexed = BooleanUtils.toBoolean(row.get(4));

            final PostColumnHeaderData postColumnHeaderData = new PostColumnHeaderData();
            postColumnHeaderData.setName(columnName);
            postColumnHeaderData.setType(columnType.getTypeString());
            postColumnHeaderData.setLength(columnLength);
            postColumnHeaderData.setUnique(unique);
            postColumnHeaderData.setIndexed(indexed);
            return postColumnHeaderData;
        }).collect(Collectors.toList());
    }

    @When("A multirow datatable for {string} is created")
    public void whenMultirowDatatableCreated(final String entityTypeStr) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final List<PostColumnHeaderData> columns = createRandomDatatableColumnsRequest();
        final PostDataTablesRequest request = createDatatableRequest(entityType, columns, true);

        final PostDataTablesResponse response = ok(() -> fineractClient.dataTables().createDatatable(request, Map.of()));

        testContext().set(CREATE_DATATABLE_RESULT_KEY, response);
        testContext().set(DATATABLE_NAME, response.getResourceIdentifier());
    }

    private List<PostColumnHeaderData> createRandomDatatableColumnsRequest() {
        final PostColumnHeaderData columnDef = new PostColumnHeaderData();
        columnDef.setName("col");
        columnDef.setType(DatatableColumnType.NUMBER.getTypeString());
        columnDef.setMandatory(false);
        columnDef.setLength(10L);
        columnDef.setCode("");
        columnDef.setUnique(false);
        columnDef.setIndexed(false);
        return List.of(columnDef);
    }

    private PostDataTablesRequest createDatatableRequest(final DatatableEntityType entityType, final List<PostColumnHeaderData> columns) {
        return createDatatableRequest(entityType, columns, false);
    }

    private PostDataTablesRequest createDatatableRequest(final DatatableEntityType entityType, final List<PostColumnHeaderData> columns,
            final boolean multiRow) {
        final PostDataTablesRequest request = new PostDataTablesRequest();
        final String datatableName = datatableNameGenerator.generate(entityType);
        request.setDatatableName(datatableName);
        request.setApptableName(entityType.getReferencedTableName());
        request.setMultiRow(multiRow);
        request.setColumns(columns);
        return request;
    }

    @Then("The following column definitions match:")
    public void thenColumnsMatch(final DataTable dataTable) {
        final String datatableName = testContext().get(DATATABLE_NAME);
        final GetDataTablesResponse response = ok(() -> fineractClient.dataTables().getDatatable(datatableName, Map.of()));

        final Map<String, ResultsetColumnHeaderData> columnMap = response.getColumnHeaderData().stream()
                .collect(Collectors.toMap(ResultsetColumnHeaderData::getColumnName, identity()));

        final List<List<String>> rows = dataTable.asLists();
        final List<List<String>> rowsWithoutHeader = rows.subList(1, rows.size());

        for (final List<String> row : rowsWithoutHeader) {
            final String columnName = row.get(0);
            final boolean primaryKey = BooleanUtils.toBoolean(row.get(1));
            final boolean unique = BooleanUtils.toBoolean(row.get(2));
            final boolean indexed = BooleanUtils.toBoolean(row.get(3));

            final ResultsetColumnHeaderData columnMetadata = columnMap.get(columnName);
            assertThat(columnMetadata).withFailMessage("Column [%s] not found on datatable", columnName).isNotNull();

            assertThat(columnMetadata.getIsColumnPrimaryKey())
                    .withFailMessage("Primary key definition for column [%s] does not match", columnName).isEqualTo(primaryKey);
            assertThat(columnMetadata.getIsColumnUnique())
                    .withFailMessage("Unique constraint definition for column [%s] does not match", columnName).isEqualTo(unique);
            assertThat(columnMetadata.getIsColumnIndexed()).withFailMessage("Index definition for column [%s] does not match", columnName)
                    .isEqualTo(indexed);
        }
    }

    @When("The client calls the query endpoint for the created datatable with {string} column filter, and {string} value filter")
    public void whenQueryEndpointCalled(final String columnFilter, final String valueFilter) {
        try {
            fineractClient.dataTables().queryValues(testContext().get(DATATABLE_NAME),
                    Map.of("columnFilter", columnFilter, "valueFilter", valueFilter, "resultColumns", columnFilter));
        } catch (FeignException e) {
            testContext().set(DATATABLE_QUERY_RESPONSE, e);
        }
    }

    @Then("The status of the HTTP response should be {int}")
    public void thenStatusCodeMatch(final int statusCode) {
        final FeignException exception = testContext().get(DATATABLE_QUERY_RESPONSE);
        assertThat(exception.status()).isEqualTo(statusCode);
    }

    @Then("The response body should contain the following message: {string}")
    public void thenResponseBodyContains(final String json) {
        final FeignException exception = testContext().get(DATATABLE_QUERY_RESPONSE);
        final String jsonResponse = exception.responseBodyAsString();
        assertThat(jsonResponse).contains(json);
    }

    @Then("Listing datatables with apptable {string} includes the created datatable")
    public void thenListingByApptableIncludesCreated(final String apptable) {
        final String datatableName = testContext().get(DATATABLE_NAME);
        final List<GetDataTablesResponse> response = ok(() -> fineractClient.dataTables().getDatatables(apptable, Map.of()));
        assertThat(response).extracting(GetDataTablesResponse::getRegisteredTableName)
                .withFailMessage("Expected datatable [%s] to be listed under apptable [%s] but it was not", datatableName, apptable)
                .contains(datatableName);
    }

    @Then("Listing datatables with apptable {string} excludes the created datatable")
    public void thenListingByApptableExcludesCreated(final String apptable) {
        final String datatableName = testContext().get(DATATABLE_NAME);
        final List<GetDataTablesResponse> response = ok(() -> fineractClient.dataTables().getDatatables(apptable, Map.of()));
        assertThat(response).extracting(GetDataTablesResponse::getRegisteredTableName)
                .withFailMessage("Datatable [%s] unexpectedly visible under apptable [%s]", datatableName, apptable)
                .doesNotContain(datatableName);
    }

    @When("A datatable entry is created for {string} with value {string} in column {string}")
    public void whenEntryCreatedForEntity(final String entityTypeStr, final String value, final String columnName) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        final Map<String, Object> body = buildEntryBody(columnName, value);
        final PostDataTablesAppTableIdResponse response = ok(
                () -> fineractClient.dataTables().createDatatableEntry(datatableName, entityId, body, Map.of()));
        testContext().set(DATATABLE_ENTRY_ID, response.getResourceId());
    }

    @Then("A second datatable entry for {string} with value {string} in column {string} is rejected")
    public void thenSecondEntryRejected(final String entityTypeStr, final String value, final String columnName) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        final Map<String, Object> body = buildEntryBody(columnName, value);
        try {
            fineractClient.dataTables().createDatatableEntry(datatableName, entityId, body, Map.of());
            throw new AssertionError(
                    "Expected second entry on single-row datatable [" + datatableName + "] to be rejected, but it succeeded");
        } catch (final FeignException e) {
            assertThat(e.status()).withFailMessage("Expected client/server error for second single-row entry, but got HTTP %d", e.status())
                    .isGreaterThanOrEqualTo(400);
        }
    }

    @Then("Fetching the datatable entry for {string} returns value {string} in column {string}")
    public void thenEntryHasValue(final String entityTypeStr, final String expectedValue, final String columnName) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final List<Map<String, Object>> rows = fetchEntryRows(entityType);
        assertThat(rows).withFailMessage("No datatable rows found for entity %s", entityType).isNotEmpty();
        final Object actual = rows.getFirst().get(columnName);
        final Object expected = parseValue(expectedValue);
        assertThat(valuesMatch(actual, expected)).withFailMessage("Column [%s] expected [%s] but was [%s]", columnName, expected, actual)
                .isTrue();
    }

    @When("The datatable entry for {string} is updated with value {string} in column {string}")
    public void whenEntryUpdated(final String entityTypeStr, final String value, final String columnName) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        final Map<String, Object> body = buildEntryBody(columnName, value);
        ok(() -> fineractClient.dataTables().updateDatatableEntryOnetoOne(datatableName, entityId, body, Map.of()));
    }

    @When("The datatable entry for {string} is deleted")
    public void whenEntryDeleted(final String entityTypeStr) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        ok(() -> fineractClient.dataTables().deleteDatatableEntries(datatableName, entityId, Map.of()));
    }

    @Then("Fetching the datatable entry for {string} returns empty result")
    public void thenEntryEmpty(final String entityTypeStr) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final List<Map<String, Object>> rows = fetchEntryRows(entityType);
        assertThat(rows).isEmpty();
    }

    @When("A multirow datatable entry is created for {string} with value {string} in column {string}")
    public void whenMultirowEntryCreated(final String entityTypeStr, final String value, final String columnName) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        final Map<String, Object> body = buildEntryBody(columnName, value);
        final PostDataTablesAppTableIdResponse response = ok(
                () -> fineractClient.dataTables().createDatatableEntry(datatableName, entityId, body, Map.of()));
        testContext().set(DATATABLE_ENTRY_ID, response.getResourceId());
    }

    @Then("Fetching multirow datatable entries for {string} returns value {string} in column {string}")
    public void thenMultirowEntryHasValue(final String entityTypeStr, final String expectedValue, final String columnName) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final List<Map<String, Object>> rows = fetchEntryRows(entityType);
        assertThat(rows).withFailMessage("No datatable rows found for entity %s", entityType).isNotEmpty();
        final Object expected = parseValue(expectedValue);
        assertThat(rows).anyMatch(row -> valuesMatch(row.get(columnName), expected));
    }

    @When("The multirow datatable entry for {string} is updated with value {string} in column {string} by entry id")
    public void whenMultirowEntryUpdatedById(final String entityTypeStr, final String value, final String columnName) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        final Long entryId = testContext().get(DATATABLE_ENTRY_ID);
        final Map<String, Object> body = buildEntryBody(columnName, value);
        ok(() -> fineractClient.dataTables().updateDatatableEntryOneToMany(datatableName, entityId, entryId, body, Map.of()));
    }

    @When("The multirow datatable entry for {string} is deleted by entry id")
    public void whenMultirowEntryDeletedById(final String entityTypeStr) {
        final DatatableEntityType entityType = DatatableEntityType.fromString(entityTypeStr);
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        final Long entryId = testContext().get(DATATABLE_ENTRY_ID);
        ok(() -> fineractClient.dataTables().deleteDatatableEntry(datatableName, entityId, entryId, Map.of()));
    }

    private Map<String, Object> buildEntryBody(final String columnName, final String value) {
        return Map.of("locale", "en", columnName, parseValue(value));
    }

    private Object parseValue(final String value) {
        if ("true".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        try {
            final long parsed = Long.parseLong(value);
            return parsed >= Integer.MIN_VALUE && parsed <= Integer.MAX_VALUE ? (int) parsed : parsed;
        } catch (final NumberFormatException ignored) {
            // not an integer, fall through
        }
        try {
            return new BigDecimal(value);
        } catch (final NumberFormatException ignored) {
            return value;
        }
    }

    private boolean valuesMatch(final Object actual, final Object expected) {
        if (Objects.equals(actual, expected)) {
            return true;
        }
        if (actual instanceof Number actualNum && expected instanceof Number expectedNum) {
            return new BigDecimal(actualNum.toString()).compareTo(new BigDecimal(expectedNum.toString())) == 0;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchEntryRows(final DatatableEntityType entityType) {
        final String datatableName = testContext().get(DATATABLE_NAME);
        final Long entityId = resolveEntityId(entityType);
        final Object response = ok(() -> fineractClient.dataTables().getDatatableEntries(datatableName, entityId, (String) null, Map.of()));
        if (response instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        if (response instanceof Map<?, ?> map && !map.isEmpty()) {
            return List.of((Map<String, Object>) map);
        }
        return List.of();
    }

    private Long resolveEntityId(final DatatableEntityType entityType) {
        return switch (entityType) {
            case WC_LOAN_PRODUCT -> resolveWcLoanProductId();
            case WC_LOAN -> resolveWcLoanId();
            case LOAN -> throw new UnsupportedOperationException("Entry steps are not implemented for LOAN entity");
        };
    }

    private Long resolveWcLoanProductId() {
        final PostWorkingCapitalLoanProductsResponse response = Optional
                .<PostWorkingCapitalLoanProductsResponse>ofNullable(
                        testContext().get(TestContextKey.WORKING_CAPITAL_LOAN_PRODUCT_CREATE_RESPONSE))
                .orElseGet(() -> testContext().get(TestContextKey.DEFAULT_WORKING_CAPITAL_LOAN_PRODUCT_CREATE_RESPONSE_WCLP));
        assertThat(response)
                .withFailMessage(
                        "No WC loan product found in test context. " + "Use 'Admin creates a new Working Capital Loan Product' step first.")
                .isNotNull();
        return response.getResourceId();
    }

    private Long resolveWcLoanId() {
        final PostWorkingCapitalLoansResponse response = testContext().get(TestContextKey.WORKING_CAPITAL_LOAN_CREATE_RESPONSE);
        assertThat(response)
                .withFailMessage(
                        "No WC loan found in test context. Use 'Admin creates a working capital loan with the following data' step first.")
                .isNotNull();
        return response.getLoanId();
    }
}
