package com.example.converter;

import java.math.BigDecimal;
import java.util.Date;

public class MT103 {

    private String sendersReference;
    private String bankOperationCode;
    private Date valueDate;
    private String currency;
    private BigDecimal amount;
    private Party orderingCustomer;
    private Party orderingInstitution;
    private Party sendersCorrespondent;
    private Party receiversCorrespondent;
    private Party intermediaryInstitution;
    private Party accountWithInstitution;
    private Party beneficiaryCustomer;
    private String remittanceInformation;
    private String detailsOfCharges;
    private String senderToReceiverInformation;

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

    public String getSendersReference() {
        return sendersReference;
    }

    public void setSendersReference(String sendersReference) {
        this.sendersReference = sendersReference;
    }

    public String getBankOperationCode() {
        return bankOperationCode;
    }

    public void setBankOperationCode(String bankOperationCode) {
        this.bankOperationCode = bankOperationCode;
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

    public Party getOrderingCustomer() {
        return orderingCustomer;
    }

    public void setOrderingCustomer(Party orderingCustomer) {
        this.orderingCustomer = orderingCustomer;
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

    public Party getIntermediaryInstitution() {
        return intermediaryInstitution;
    }

    public void setIntermediaryInstitution(Party intermediaryInstitution) {
        this.intermediaryInstitution = intermediaryInstitution;
    }

    public Party getAccountWithInstitution() {
        return accountWithInstitution;
    }

    public void setAccountWithInstitution(Party accountWithInstitution) {
        this.accountWithInstitution = accountWithInstitution;
    }

    public Party getBeneficiaryCustomer() {
        return beneficiaryCustomer;
    }

    public void setBeneficiaryCustomer(Party beneficiaryCustomer) {
        this.beneficiaryCustomer = beneficiaryCustomer;
    }

    public String getRemittanceInformation() {
        return remittanceInformation;
    }

    public void setRemittanceInformation(String remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }

    public String getDetailsOfCharges() {
        return detailsOfCharges;
    }

    public void setDetailsOfCharges(String detailsOfCharges) {
        this.detailsOfCharges = detailsOfCharges;
    }

    public String getSenderToReceiverInformation() {
        return senderToReceiverInformation;
    }

    public void setSenderToReceiverInformation(String senderToReceiverInformation) {
        this.senderToReceiverInformation = senderToReceiverInformation;
    }
}
