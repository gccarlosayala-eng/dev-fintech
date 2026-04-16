@WorkingCapitalDatatablesFeature
Feature: WorkingCapitalDatatables

  # Datatable schema - foreign key column per entity and strategy
  Scenario: Single-row datatable for WC Loan exposes wc_loan_id as unique primary key
    When A datatable for "WC_Loan" is created
    Then The following column definitions match:
      | Name       | Primary key | Unique | Indexed |
      | wc_loan_id | true        | true   | true    |

  Scenario: Multi-row datatable for WC Loan exposes wc_loan_id as indexed foreign key
    When A multirow datatable for "WC_Loan" is created
    Then The following column definitions match:
      | Name       | Primary key | Unique | Indexed |
      | wc_loan_id | false       | false  | true    |

  Scenario: Single-row datatable for WC Loan Product exposes wc_product_loan_id as unique primary key
    When A datatable for "WC_Loan_Product" is created
    Then The following column definitions match:
      | Name               | Primary key | Unique | Indexed |
      | wc_product_loan_id | true        | true   | true    |

  Scenario: Multi-row datatable for WC Loan Product exposes wc_product_loan_id as indexed foreign key
    When A multirow datatable for "WC_Loan_Product" is created
    Then The following column definitions match:
      | Name               | Primary key | Unique | Indexed |
      | wc_product_loan_id | false       | false  | true    |

  # Datatable visibility is scoped to its registered entity
  Scenario: Datatable registered for WC Loan is listed under m_wc_loan but not under m_loan
    When A datatable for "WC_Loan" is created
    Then Listing datatables with apptable "m_wc_loan" includes the created datatable
    And Listing datatables with apptable "m_loan" excludes the created datatable

  Scenario: Datatable registered for WC Loan Product is listed under m_wc_loan_product but not under m_product_loan
    When A datatable for "WC_Loan_Product" is created
    Then Listing datatables with apptable "m_wc_loan_product" includes the created datatable
    And Listing datatables with apptable "m_product_loan" excludes the created datatable

  # Entry CRUD - WC Loan Product
  Scenario: Single-row datatable entry for WC Loan Product supports create, read, update, delete
    When Admin creates a new Working Capital Loan Product
    And A datatable for "WC_Loan_Product" is created with the following extra columns:
      | Name    | Type   | Length | Unique | Indexed |
      | test_nr | number | 10     | false  | false   |
    And A datatable entry is created for "WC_Loan_Product" with value "42" in column "test_nr"
    Then Fetching the datatable entry for "WC_Loan_Product" returns value "42" in column "test_nr"
    When The datatable entry for "WC_Loan_Product" is updated with value "99" in column "test_nr"
    Then Fetching the datatable entry for "WC_Loan_Product" returns value "99" in column "test_nr"
    When The datatable entry for "WC_Loan_Product" is deleted
    Then Fetching the datatable entry for "WC_Loan_Product" returns empty result

  Scenario: Multi-row datatable entries for WC Loan Product support create, read, update, delete
    When Admin creates a new Working Capital Loan Product
    And A multirow datatable for "WC_Loan_Product" is created
    And A multirow datatable entry is created for "WC_Loan_Product" with value "10" in column "col"
    Then Fetching multirow datatable entries for "WC_Loan_Product" returns value "10" in column "col"
    When The multirow datatable entry for "WC_Loan_Product" is updated with value "77" in column "col" by entry id
    Then Fetching multirow datatable entries for "WC_Loan_Product" returns value "77" in column "col"
    When The multirow datatable entry for "WC_Loan_Product" is deleted by entry id
    Then Fetching the datatable entry for "WC_Loan_Product" returns empty result

  # Entry CRUD - WC Loan
  Scenario: Single-row datatable entry for WC Loan supports create, read, update, delete
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And A datatable for "WC_Loan" is created with the following extra columns:
      | Name    | Type   | Length | Unique | Indexed |
      | test_nr | number | 10     | false  | false   |
    And A datatable entry is created for "WC_Loan" with value "42" in column "test_nr"
    Then Fetching the datatable entry for "WC_Loan" returns value "42" in column "test_nr"
    When The datatable entry for "WC_Loan" is updated with value "99" in column "test_nr"
    Then Fetching the datatable entry for "WC_Loan" returns value "99" in column "test_nr"
    When The datatable entry for "WC_Loan" is deleted
    Then Fetching the datatable entry for "WC_Loan" returns empty result

  Scenario: Multi-row datatable entries for WC Loan support create, read, update, delete
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And A multirow datatable for "WC_Loan" is created
    And A multirow datatable entry is created for "WC_Loan" with value "10" in column "col"
    Then Fetching multirow datatable entries for "WC_Loan" returns value "10" in column "col"
    When The multirow datatable entry for "WC_Loan" is updated with value "77" in column "col" by entry id
    Then Fetching multirow datatable entries for "WC_Loan" returns value "77" in column "col"
    When The multirow datatable entry for "WC_Loan" is deleted by entry id
    Then Fetching the datatable entry for "WC_Loan" returns empty result

  # Single-row strategy - only one entry per parent row
  Scenario: Single-row datatable for WC Loan rejects a second entry for the same loan
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And A datatable for "WC_Loan" is created with the following extra columns:
      | Name    | Type   | Length | Unique | Indexed |
      | test_nr | number | 10     | false  | false   |
    And A datatable entry is created for "WC_Loan" with value "1" in column "test_nr"
    Then A second datatable entry for "WC_Loan" with value "2" in column "test_nr" is rejected
