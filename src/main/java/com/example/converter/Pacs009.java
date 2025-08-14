package com.example.converter;

import java.math.BigDecimal;
import java.util.Date;

public class Pacs009 {

    private GroupHeader groupHeader;
    private FinancialInstitutionCreditTransfer financialInstitutionCreditTransfer;

    public GroupHeader getGroupHeader() {
        return groupHeader;
    }

    public void setGroupHeader(GroupHeader groupHeader) {
        this.groupHeader = groupHeader;
    }

    public FinancialInstitutionCreditTransfer getFinancialInstitutionCreditTransfer() {
        return financialInstitutionCreditTransfer;
    }

    public void setFinancialInstitutionCreditTransfer(FinancialInstitutionCreditTransfer financialInstitutionCreditTransfer) {
        this.financialInstitutionCreditTransfer = financialInstitutionCreditTransfer;
    }

    public static class GroupHeader {
        private String messageIdentification;
        private Date creationDateTime;
        private int numberOfTransactions;
        private SettlementInformation settlementInformation;

        public String getMessageIdentification() {
            return messageIdentification;
        }

        public void setMessageIdentification(String messageIdentification) {
            this.messageIdentification = messageIdentification;
        }

        public Date getCreationDateTime() {
            return creationDateTime;
        }

        public void setCreationDateTime(Date creationDateTime) {
            this.creationDateTime = creationDateTime;
        }

        public int getNumberOfTransactions() {
            return numberOfTransactions;
        }

        public void setNumberOfTransactions(int numberOfTransactions) {
            this.numberOfTransactions = numberOfTransactions;
        }

        public SettlementInformation getSettlementInformation() {
            return settlementInformation;
        }

        public void setSettlementInformation(SettlementInformation settlementInformation) {
            this.settlementInformation = settlementInformation;
        }
    }

    public static class SettlementInformation {
        private String settlementMethod;

        public String getSettlementMethod() {
            return settlementMethod;
        }

        public void setSettlementMethod(String settlementMethod) {
            this.settlementMethod = settlementMethod;
        }
    }

    public static class FinancialInstitutionCreditTransfer {
        private PaymentIdentification paymentIdentification;
        private Amount interbankSettlementAmount;
        private Date interbankSettlementDate;
        private Party instructingAgent;
        private Party instructedAgent;
        private String senderToReceiverInformation;
        private Party intermediaryAgent1;
        private Party intermediaryAgent2;
        private Party intermediaryAgent3;

        public PaymentIdentification getPaymentIdentification() {
            return paymentIdentification;
        }

        public void setPaymentIdentification(PaymentIdentification paymentIdentification) {
            this.paymentIdentification = paymentIdentification;
        }

        public Amount getInterbankSettlementAmount() {
            return interbankSettlementAmount;
        }

        public void setInterbankSettlementAmount(Amount interbankSettlementAmount) {
            this.interbankSettlementAmount = interbankSettlementAmount;
        }

        public Date getInterbankSettlementDate() {
            return interbankSettlementDate;
        }

        public void setInterbankSettlementDate(Date interbankSettlementDate) {
            this.interbankSettlementDate = interbankSettlementDate;
        }

        public Party getInstructingAgent() {
            return instructingAgent;
        }

        public void setInstructingAgent(Party instructingAgent) {
            this.instructingAgent = instructingAgent;
        }

        public Party getInstructedAgent() {
            return instructedAgent;
        }

        public void setInstructedAgent(Party instructedAgent) {
            this.instructedAgent = instructedAgent;
        }

        public String getSenderToReceiverInformation() {
            return senderToReceiverInformation;
        }

        public void setSenderToReceiverInformation(String senderToReceiverInformation) {
            this.senderToReceiverInformation = senderToReceiverInformation;
        }

        public Party getIntermediaryAgent1() {
            return intermediaryAgent1;
        }

        public void setIntermediaryAgent1(Party intermediaryAgent1) {
            this.intermediaryAgent1 = intermediaryAgent1;
        }

        public Party getIntermediaryAgent2() {
            return intermediaryAgent2;
        }

        public void setIntermediaryAgent2(Party intermediaryAgent2) {
            this.intermediaryAgent2 = intermediaryAgent2;
        }

        public Party getIntermediaryAgent3() {
            return intermediaryAgent3;
        }

        public void setIntermediaryAgent3(Party intermediaryAgent3) {
            this.intermediaryAgent3 = intermediaryAgent3;
        }
    }

    public static class PaymentIdentification {
        private String instructionIdentification;
        private String endToEndIdentification;

        public String getInstructionIdentification() {
            return instructionIdentification;
        }

        public void setInstructionIdentification(String instructionIdentification) {
            this.instructionIdentification = instructionIdentification;
        }

        public String getEndToEndIdentification() {
            return endToEndIdentification;
        }

        public void setEndToEndIdentification(String endToEndIdentification) {
            this.endToEndIdentification = endToEndIdentification;
        }
    }

    public static class Amount {
        private String currency;
        private BigDecimal value;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }

    public static class Party {
        private String name;
        private String address;
        private String bic;
        private String accountNumber;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBic() {
            return bic;
        }

        public void setBic(String bic) {
            this.bic = bic;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }
    }
}
