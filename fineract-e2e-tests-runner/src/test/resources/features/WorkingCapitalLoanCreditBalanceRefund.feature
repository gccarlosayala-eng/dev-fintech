@WorkingCapitalLoanCreditBalanceRefundFeature
Feature: Working Capital Loan Credit Balance Refund

  Scenario: Verify working capital loan credit balance refund - partial refund keeps OVERPAID
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And Admin successfully approves the working capital loan on "01 January 2026" with "9000" amount and expected disbursement date on "01 January 2026"
    And Admin successfully disburse the Working Capital loan on "01 January 2026" with "9000" EUR transaction amount
    When Admin sets the business date to "02 January 2026"
    And Customer makes repayment on "02 January 2026" with 9100.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "OVERPAID"
    When Admin sets the business date to "03 January 2026"
    When Customer makes credit balance refund on "03 January 2026" with 50.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "OVERPAID"
    And Customer makes credit balance refund on "03 January 2026" with 50.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "CLOSED_OBLIGATIONS_MET"

  Scenario: Verify working capital loan credit balance refund - full refund closes loan
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And Admin successfully approves the working capital loan on "01 January 2026" with "9000" amount and expected disbursement date on "01 January 2026"
    And Admin successfully disburse the Working Capital loan on "01 January 2026" with "9000" EUR transaction amount
    When Admin sets the business date to "02 January 2026"
    And Customer makes repayment on "02 January 2026" with 9050.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "OVERPAID"
    When Admin sets the business date to "03 January 2026"
    When Customer makes credit balance refund on "03 January 2026" with 50.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "CLOSED_OBLIGATIONS_MET"

  Scenario: Verify working capital loan credit balance refund - non-overpaid loan returns 400
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And Admin successfully approves the working capital loan on "01 January 2026" with "9000" amount and expected disbursement date on "01 January 2026"
    And Admin successfully disburse the Working Capital loan on "01 January 2026" with "9000" EUR transaction amount
    Then Working Capital loan status will be "ACTIVE"
    When Admin sets the business date to "02 January 2026"
    And Initiating a credit balance refund on "02 January 2026" with 10.0 transaction amount on Working Capital loan results an error with the following data:
      | HTTP response code | Error message                                                  |
      | 400                | Credit balance refund is allowed only for overpaid loans       |
    And Customer makes repayment on "02 January 2026" with 9000.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "CLOSED_OBLIGATIONS_MET"

  Scenario: Verify working capital loan credit balance refund - payment details are accepted
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And Admin successfully approves the working capital loan on "01 January 2026" with "9000" amount and expected disbursement date on "01 January 2026"
    And Admin successfully disburse the Working Capital loan on "01 January 2026" with "9000" EUR transaction amount
    When Admin sets the business date to "02 January 2026"
    And Customer makes repayment on "02 January 2026" with 9050.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "OVERPAID"
    When Admin sets the business date to "03 January 2026"
    When Customer makes credit balance refund on "03 January 2026" with 25.0 transaction amount on Working Capital loan with the following payment details:
      | paymentType   | accountNumber | checkNumber | routingCode | receiptNumber | bankNumber |
      | CHECK_PAYMENT | 12345         | 321         | 456         | 789           | 654        |
    Then Working Capital loan status will be "OVERPAID"
    And Customer makes credit balance refund on "03 January 2026" with 25.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "CLOSED_OBLIGATIONS_MET"

  Scenario: Verify working capital loan credit balance refund - refund amount greater than overpayment returns 400
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And Admin successfully approves the working capital loan on "01 January 2026" with "9000" amount and expected disbursement date on "01 January 2026"
    And Admin successfully disburse the Working Capital loan on "01 January 2026" with "9000" EUR transaction amount
    When Admin sets the business date to "02 January 2026"
    And Customer makes repayment on "02 January 2026" with 9100.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "OVERPAID"
    When Admin sets the business date to "03 January 2026"
    And Initiating a credit balance refund on "03 January 2026" with 200.0 transaction amount on Working Capital loan results an error with the following data:
      | HTTP response code | Error message                                                        |
      | 400                | Credit balance refund amount cannot exceed overpayment amount        |
    And Customer makes credit balance refund on "03 January 2026" with 100.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "CLOSED_OBLIGATIONS_MET"

  Scenario: Verify working capital loan credit balance refund - future transaction date returns 400
    When Admin sets the business date to "01 January 2026"
    And Admin creates a client with random data
    And Admin creates a working capital loan with the following data:
      | LoanProduct | submittedOnDate | expectedDisbursementDate | principalAmount | totalPayment | periodPaymentRate | discount |
      | WCLP        | 01 January 2026 | 01 January 2026          | 9000            | 100000       | 18                | 0        |
    And Admin successfully approves the working capital loan on "01 January 2026" with "9000" amount and expected disbursement date on "01 January 2026"
    And Admin successfully disburse the Working Capital loan on "01 January 2026" with "9000" EUR transaction amount
    When Admin sets the business date to "02 January 2026"
    And Customer makes repayment on "02 January 2026" with 9100.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "OVERPAID"
    And Initiating a credit balance refund on "15 January 2026" with 50.0 transaction amount on Working Capital loan results an error with the following data:
      | HTTP response code | Error message           |
      | 400                | cannot.be.a.future.date |
    And Customer makes credit balance refund on "02 January 2026" with 100.0 transaction amount on Working Capital loan
    Then Working Capital loan status will be "CLOSED_OBLIGATIONS_MET"

