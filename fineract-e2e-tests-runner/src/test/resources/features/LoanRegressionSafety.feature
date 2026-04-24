@RegressionSafety
Feature: Loan Lifecycle Regression Safety
  Exercises the complete loan lifecycle through all major state transitions.
  Designed to be run against multiple Fineract instances with different DB states
  to catch regressions introduced by database schema changes.
  Based on proven patterns from 0_COB.feature (C2501, C2681).

  @TestRailId:C3500 @RegressionSafety
  Scenario: Full loan lifecycle - create, disburse, partial repay, COB with delinquency, full payoff
#   -- Setup and disbursement --
    When Admin sets the business date to "01 January 2022"
    When Admin creates a client with random data
    When Admin creates a new default Loan with date: "01 January 2022"
    And Admin successfully approves the loan on "01 January 2022" with "1000" amount and expected disbursement date on "01 January 2022"
    When Admin successfully disburse the loan on "01 January 2022" with "1000" EUR transaction amount
    Then Loan status will be "ACTIVE"
    Then Loan has 1000 outstanding amount
#   -- Partial repayment before due date (monetary action) --
    When Admin sets the business date to "15 January 2022"
    And Customer makes "AUTOPAY" repayment on "15 January 2022" with 500 EUR transaction amount
    Then Repayment transaction is created with 500 amount and "AUTOPAY" type
    Then Loan has 500 outstanding amount
    Then Loan status will be "ACTIVE"
#   -- Advance past due date (Feb 1), run COB, verify delinquency --
#   -- Arrears setting is 3 on default product -> delinquent from Feb 3 --
    When Admin sets the business date to "04 February 2022"
    When Admin runs COB job
    Then Admin checks that delinquency range is: "RANGE_1" and has delinquentDate "2022-02-03"
#   -- Full payoff --
    And Customer makes "AUTOPAY" repayment on "04 February 2022" with 500 EUR transaction amount
    Then Loan has 0 outstanding amount
    Then Loan status will be "CLOSED_OBLIGATIONS_MET"
