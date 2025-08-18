package com.example.converter;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Pacs008Parser {

    public Pacs008 parse(File pacs008File) throws JDOMException, IOException, ParseException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(pacs008File);
        Element rootElement = document.getRootElement();

        Pacs008 pacs008 = new Pacs008();

        // Group Header
        Element grpHdr = rootElement.getChild("GrpHdr", rootElement.getNamespace());
        if (grpHdr != null) {
            Pacs008.GroupHeader groupHeader = new Pacs008.GroupHeader();
            groupHeader.setMessageIdentification(grpHdr.getChildText("MsgId", grpHdr.getNamespace()));
            String creDtTm = grpHdr.getChildText("CreDtTm", grpHdr.getNamespace());
            if (creDtTm != null) {
                groupHeader.setCreationDateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(creDtTm));
            }
            pacs008.setGroupHeader(groupHeader);
        }

        // Credit Transfer Transaction Information
        Element cdtTrfTxInf = rootElement.getChild("CdtTrfTxInf", rootElement.getNamespace());
        if (cdtTrfTxInf != null) {
            Pacs008.CreditTransferTransactionInformation txInfo = new Pacs008.CreditTransferTransactionInformation();

            Element pmtId = cdtTrfTxInf.getChild("PmtId", cdtTrfTxInf.getNamespace());
            if (pmtId != null) {
                Pacs008.PaymentIdentification paymentIdentification = new Pacs008.PaymentIdentification();
                paymentIdentification.setInstructionIdentification(pmtId.getChildText("InstrId", pmtId.getNamespace()));
                paymentIdentification.setEndToEndIdentification(pmtId.getChildText("EndToEndId", pmtId.getNamespace()));
                txInfo.setPaymentIdentification(paymentIdentification);
            }

            Element intrBkSttlmAmt = cdtTrfTxInf.getChild("IntrBkSttlmAmt", cdtTrfTxInf.getNamespace());
            if (intrBkSttlmAmt != null) {
                Pacs008.Amount amount = new Pacs008.Amount();
                amount.setCurrency(intrBkSttlmAmt.getAttributeValue("Ccy"));
                amount.setValue(new BigDecimal(intrBkSttlmAmt.getText()));
                txInfo.setInterbankSettlementAmount(amount);
            }

            String interbankSettlementDate = cdtTrfTxInf.getChildText("IntrBkSttlmDt", cdtTrfTxInf.getNamespace());
            if (interbankSettlementDate != null) {
                txInfo.setInterbankSettlementDate(new SimpleDateFormat("yyyy-MM-dd").parse(interbankSettlementDate));
            }

            txInfo.setDebtor(parseParty(cdtTrfTxInf.getChild("Dbtr", cdtTrfTxInf.getNamespace())));
            txInfo.setDebtorAgent(parseParty(cdtTrfTxInf.getChild("DbtrAgt", cdtTrfTxInf.getNamespace())));
            txInfo.setCreditor(parseParty(cdtTrfTxInf.getChild("Cdtr", cdtTrfTxInf.getNamespace())));
            txInfo.setCreditorAgent(parseParty(cdtTrfTxInf.getChild("CdtrAgt", cdtTrfTxInf.getNamespace())));

            Element chrgBr = cdtTrfTxInf.getChild("ChrgBr", cdtTrfTxInf.getNamespace());
            if (chrgBr != null) {
                txInfo.setChargeBearer(chrgBr.getText());
            }

            Element rmtInf = cdtTrfTxInf.getChild("RmtInf", cdtTrfTxInf.getNamespace());
            if (rmtInf != null) {
                Pacs008.RemittanceInformation remittanceInformation = new Pacs008.RemittanceInformation();
                List<Element> ustrdElements = rmtInf.getChildren("Ustrd", rmtInf.getNamespace());
                if (ustrdElements != null) {
                    remittanceInformation.setUnstructured(ustrdElements.stream().map(Element::getText).collect(Collectors.toList()));
                }
                txInfo.setRemittanceInformation(remittanceInformation);
            }

            pacs008.setCreditTransferTransactionInformation(txInfo);
        }

        return pacs008;
    }

    private Pacs008.Party parseParty(Element partyElement) {
        if (partyElement == null) {
            return null;
        }
        Pacs008.Party party = new Pacs008.Party();
        Element finInstnId = partyElement.getChild("FinInstnId", partyElement.getNamespace());
        if (finInstnId != null) {
            party.setBic(finInstnId.getChildText("BICFI", finInstnId.getNamespace()));
        }
        party.setName(partyElement.getChildText("Nm", partyElement.getNamespace()));
        Element pstlAdr = partyElement.getChild("PstlAdr", partyElement.getNamespace());
        if (pstlAdr != null) {
            party.setAddress(pstlAdr.getChildText("AdrLine", pstlAdr.getNamespace()));
        }
        return party;
    }
}
