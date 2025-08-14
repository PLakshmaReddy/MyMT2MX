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
import java.util.Date;

public class Pacs009Parser {

    public Pacs009 parse(File pacs009File) throws JDOMException, IOException, ParseException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(pacs009File);
        Element rootElement = document.getRootElement();

        Pacs009 pacs009 = new Pacs009();

        // Group Header
        Element grpHdr = rootElement.getChild("GrpHdr", rootElement.getNamespace());
        if (grpHdr != null) {
            Pacs009.GroupHeader groupHeader = new Pacs009.GroupHeader();
            groupHeader.setMessageIdentification(grpHdr.getChildText("MsgId", grpHdr.getNamespace()));
            String creDtTm = grpHdr.getChildText("CreDtTm", grpHdr.getNamespace());
            if (creDtTm != null) {
                groupHeader.setCreationDateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(creDtTm));
            }
            String nbOfTxs = grpHdr.getChildText("NbOfTxs", grpHdr.getNamespace());
            if (nbOfTxs != null) {
                groupHeader.setNumberOfTransactions(Integer.parseInt(nbOfTxs));
            }

            Element sttlmInf = grpHdr.getChild("SttlmInf", grpHdr.getNamespace());
            if (sttlmInf != null) {
                Pacs009.SettlementInformation settlementInformation = new Pacs009.SettlementInformation();
                settlementInformation.setSettlementMethod(sttlmInf.getChildText("SttlmMtd", sttlmInf.getNamespace()));
                groupHeader.setSettlementInformation(settlementInformation);
            }
            pacs009.setGroupHeader(groupHeader);
        }

        // Financial Institution Credit Transfer
        Element finInstnCdtTrf = rootElement.getChild("FinInstnCdtTrf", rootElement.getNamespace());
        if (finInstnCdtTrf != null) {
            Pacs009.FinancialInstitutionCreditTransfer creditTransfer = new Pacs009.FinancialInstitutionCreditTransfer();

            Element pmtId = finInstnCdtTrf.getChild("PmtId", finInstnCdtTrf.getNamespace());
            if (pmtId != null) {
                Pacs009.PaymentIdentification paymentIdentification = new Pacs009.PaymentIdentification();
                paymentIdentification.setInstructionIdentification(pmtId.getChildText("InstrId", pmtId.getNamespace()));
                paymentIdentification.setEndToEndIdentification(pmtId.getChildText("EndToEndId", pmtId.getNamespace()));
                creditTransfer.setPaymentIdentification(paymentIdentification);
            }

            Element intrBkSttlmAmt = finInstnCdtTrf.getChild("IntrBkSttlmAmt", finInstnCdtTrf.getNamespace());
            if (intrBkSttlmAmt != null) {
                Pacs009.Amount amount = new Pacs009.Amount();
                amount.setCurrency(intrBkSttlmAmt.getAttributeValue("Ccy"));
                amount.setValue(new BigDecimal(intrBkSttlmAmt.getText()));
                creditTransfer.setInterbankSettlementAmount(amount);
            }

            String interbankSettlementDate = finInstnCdtTrf.getChildText("IntrBkSttlmDt", finInstnCdtTrf.getNamespace());
            if (interbankSettlementDate != null) {
                creditTransfer.setInterbankSettlementDate(new SimpleDateFormat("yyyy-MM-dd").parse(interbankSettlementDate));
            }

            creditTransfer.setInstructingAgent(parseParty(finInstnCdtTrf.getChild("InstgAgt", finInstnCdtTrf.getNamespace())));
            creditTransfer.setIntermediaryAgent1(parseParty(finInstnCdtTrf.getChild("IntrmyAgt1", finInstnCdtTrf.getNamespace())));
            creditTransfer.setIntermediaryAgent2(parseParty(finInstnCdtTrf.getChild("IntrmyAgt2", finInstnCdtTrf.getNamespace())));
            creditTransfer.setIntermediaryAgent3(parseParty(finInstnCdtTrf.getChild("IntrmyAgt3", finInstnCdtTrf.getNamespace())));
            creditTransfer.setInstructedAgent(parseParty(finInstnCdtTrf.getChild("InstdAgt", finInstnCdtTrf.getNamespace())));

            Element ustrd = finInstnCdtTrf.getChild("Ustrd", finInstnCdtTrf.getNamespace());
            if (ustrd != null) {
                creditTransfer.setSenderToReceiverInformation(ustrd.getText());
            }

            pacs009.setFinancialInstitutionCreditTransfer(creditTransfer);
        }

        return pacs009;
    }

    private Pacs009.Party parseParty(Element partyElement) {
        if (partyElement == null) {
            return null;
        }
        Pacs009.Party party = new Pacs009.Party();
        Element finInstnId = partyElement.getChild("FinInstnId", partyElement.getNamespace());
        if (finInstnId != null) {
            party.setBic(finInstnId.getChildText("BICFI", finInstnId.getNamespace()));
        }
        party.setName(party.getBic()); // for now, we assume name and bic are the same
        return party;
    }
}
