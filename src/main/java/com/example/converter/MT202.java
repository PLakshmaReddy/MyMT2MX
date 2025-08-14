package com.example.converter;

import java.math.BigDecimal;
import java.util.Date;

public class MT202 {

    private String transactionReferenceNumber;
    private String relatedReference;
    private Date valueDate;
    private String currency;
    private BigDecimal amount;
    private Party orderingInstitution;
    private Party sendersCorrespondent;
    private Party receiversCorrespondent;
    private Party intermediary;
    private Party accountWithInstitution;
    private Party beneficiaryInstitution;
    private String senderToReceiverInformation;
    private Party intermediaryInstitution1; // 53
    private Party intermediaryInstitution2; // 54
    private Party receivingAgent; // 56
    private Party beneficiarysCorrespondent; // 57

    public static class Party {
        private String bic;
        private String accountNumber;
        private String nameAndAddress;
        private String dataSourceScheme;

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

        public String getNameAndAddress() {
            return nameAndAddress;
        }

        public void setNameAndAddress(String nameAndAddress) {
            this.nameAndAddress = nameAndAddress;
        }

        public String getDataSourceScheme() {
            return dataSourceScheme;
        }

        public void setDataSourceScheme(String dataSourceScheme) {
            this.dataSourceScheme = dataSourceScheme;
        }
    }

    // Getters and setters

    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public String getRelatedReference() {
        return relatedReference;
    }

    public void setRelatedReference(String relatedReference) {
        this.relatedReference = relatedReference;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Party getOrderingInstitution() {
        return orderingInstitution;
    }

    public void setOrderingInstitution(Party orderingInstitution) {
        this.orderingInstitution = orderingInstitution;
    }

    public Party getSendersCorrespondent() {
        return sendersCorrespondent;
    }

    public void setSendersCorrespondent(Party sendersCorrespondent) {
        this.sendersCorrespondent = sendersCorrespondent;
    }

    public Party getReceiversCorrespondent() {
        return receiversCorrespondent;
    }

    public void setReceiversCorrespondent(Party receiversCorrespondent) {
        this.receiversCorrespondent = receiversCorrespondent;
    }

    public Party getIntermediary() {
        return intermediary;
    }

    public void setIntermediary(Party intermediary) {
        this.intermediary = intermediary;
    }

    public Party getAccountWithInstitution() {
        return accountWithInstitution;
    }

    public void setAccountWithInstitution(Party accountWithInstitution) {
        this.accountWithInstitution = accountWithInstitution;
    }

    public Party getBeneficiaryInstitution() {
        return beneficiaryInstitution;
    }

    public void setBeneficiaryInstitution(Party beneficiaryInstitution) {
        this.beneficiaryInstitution = beneficiaryInstitution;
    }

    public String getSenderToReceiverInformation() {
        return senderToReceiverInformation;
    }

    public void setSenderToReceiverInformation(String senderToReceiverInformation) {
        this.senderToReceiverInformation = senderToReceiverInformation;
    }

    public Party getIntermediaryInstitution1() {
        return intermediaryInstitution1;
    }

    public void setIntermediaryInstitution1(Party intermediaryInstitution1) {
        this.intermediaryInstitution1 = intermediaryInstitution1;
    }

    public Party getIntermediaryInstitution2() {
        return intermediaryInstitution2;
    }

    public void setIntermediaryInstitution2(Party intermediaryInstitution2) {
        this.intermediaryInstitution2 = intermediaryInstitution2;
    }

    public Party getReceivingAgent() {
        return receivingAgent;
    }

    public void setReceivingAgent(Party receivingAgent) {
        this.receivingAgent = receivingAgent;
    }

    public Party getBeneficiarysCorrespondent() {
        return beneficiarysCorrespondent;
    }

    public void setBeneficiarysCorrespondent(Party beneficiarysCorrespondent) {
        this.beneficiarysCorrespondent = beneficiarysCorrespondent;
    }
}
