@WorkingCapitalLoanProductAdvancedAccountingFeature
Feature: WorkingCapitalLoanProductAdvancedAccounting

  @TestRailId:C76759
  Scenario: Verify WC Loan Product template includes advanced accounting options
    When Admin retrieves the Working Capital Loan Product template
    Then Working Capital Loan Product template has advanced accounting options

  @TestRailId:C76760
  Scenario: Verify WC Loan Product create persists advanced accounting mappings
    When Admin creates a new Working Capital Loan Product with Cash based accounting and advanced mappings
    Then Working Capital Loan Product has advanced accounting mappings

  @TestRailId:C76761
  Scenario: Verify WC Loan Product update persists advanced accounting mappings
    When Admin creates a new Working Capital Loan Product with accounting rule "CASH_BASED"
    And Admin updates Working Capital Loan Product with advanced mappings
    Then Working Capital Loan Product has advanced accounting mappings

  @TestRailId:C76762
  Scenario: Verify WC Loan Product second update overrides existing advanced mappings
    When Admin creates a new Working Capital Loan Product with accounting rule "CASH_BASED"
    And Admin updates Working Capital Loan Product with advanced mappings twice
    Then Working Capital Loan Product has latest advanced accounting mappings after second update

