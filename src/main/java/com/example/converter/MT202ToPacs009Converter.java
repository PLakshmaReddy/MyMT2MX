package com.example.converter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

public class MT202ToPacs009Converter implements Converter {

    private final Configuration configuration;
    private final MT202Parser mt202Parser;

    public MT202ToPacs009Converter(Configuration configuration) {
        this.configuration = configuration;
        this.mt202Parser = new MT202Parser();
    }

    @Override
    public String convert(String mt202Message) {
        MT202 mt202 = mt202Parser.parse(mt202Message);
        Pacs009 pacs009 = mapToPacs009(mt202);
        return generatePacs009(pacs009);
    }


    private Pacs009 mapToPacs009(com.example.converter.MT202 mt202) {
        Pacs009 pacs009 = new Pacs009();

        // Group Header
        Pacs009.GroupHeader groupHeader = new Pacs009.GroupHeader();
        groupHeader.setMessageIdentification(java.util.UUID.randomUUID().toString());
        groupHeader.setCreationDateTime(new java.util.Date());
        groupHeader.setNumberOfTransactions(1);
        Pacs009.SettlementInformation settlementInformation = new Pacs009.SettlementInformation();
        settlementInformation.setSettlementMethod(configuration.getProperty("settlement.method"));
        groupHeader.setSettlementInformation(settlementInformation);
        pacs009.setGroupHeader(groupHeader);

        // Financial Institution Credit Transfer
        Pacs009.FinancialInstitutionCreditTransfer financialInstitutionCreditTransfer = new Pacs009.FinancialInstitutionCreditTransfer();

        Pacs009.PaymentIdentification paymentIdentification = new Pacs009.PaymentIdentification();
        paymentIdentification.setInstructionIdentification(mt202.getTransactionReferenceNumber());
        paymentIdentification.setEndToEndIdentification(mt202.getRelatedReference());
        financialInstitutionCreditTransfer.setPaymentIdentification(paymentIdentification);

        Pacs009.Amount amount = new Pacs009.Amount();
        amount.setCurrency(mt202.getCurrency());
        amount.setValue(mt202.getAmount());
        financialInstitutionCreditTransfer.setInterbankSettlementAmount(amount);

        financialInstitutionCreditTransfer.setInterbankSettlementDate(mt202.getValueDate());

        financialInstitutionCreditTransfer.setInstructingAgent(mapParty(mt202.getOrderingInstitution()));
        financialInstitutionCreditTransfer.setInstructedAgent(mapParty(mt202.getBeneficiaryInstitution()));

        pacs009.setFinancialInstitutionCreditTransfer(financialInstitutionCreditTransfer);

        return pacs009;
    }

    private Pacs009.Party mapParty(com.example.converter.MT202.Party mtParty) {
        if (mtParty == null) {
            return null;
        }
        Pacs009.Party pacsParty = new Pacs009.Party();
        pacsParty.setBic(mtParty.getBic());
        pacsParty.setAccountNumber(mtParty.getAccountNumber());
        if (mtParty.getNameAndAddress() != null) {
            pacsParty.setName(mtParty.getNameAndAddress());
        } else {
            pacsParty.setName(mtParty.getBic());
        }
        return pacsParty;
    }

    private String generatePacs009(Pacs009 pacs009) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Document");
            doc.appendChild(rootElement);

            Element grpHdr = doc.createElement("GrpHdr");
            rootElement.appendChild(grpHdr);

            Element msgId = doc.createElement("MsgId");
            msgId.appendChild(doc.createTextNode(pacs009.getGroupHeader().getMessageIdentification()));
            grpHdr.appendChild(msgId);

            Element creDtTm = doc.createElement("CreDtTm");
            creDtTm.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(pacs009.getGroupHeader().getCreationDateTime())));
            grpHdr.appendChild(creDtTm);

            Element nbOfTxs = doc.createElement("NbOfTxs");
            nbOfTxs.appendChild(doc.createTextNode(String.valueOf(pacs009.getGroupHeader().getNumberOfTransactions())));
            grpHdr.appendChild(nbOfTxs);

            Element sttlmInf = doc.createElement("SttlmInf");
            grpHdr.appendChild(sttlmInf);

            Element sttlmMtd = doc.createElement("SttlmMtd");
            sttlmMtd.appendChild(doc.createTextNode(pacs009.getGroupHeader().getSettlementInformation().getSettlementMethod()));
            sttlmInf.appendChild(sttlmMtd);

            Element finInstnCdtTrf = doc.createElement("FinInstnCdtTrf");
            rootElement.appendChild(finInstnCdtTrf);

            Element pmtId = doc.createElement("PmtId");
            finInstnCdtTrf.appendChild(pmtId);

            Element instrId = doc.createElement("InstrId");
            instrId.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getPaymentIdentification().getInstructionIdentification()));
            pmtId.appendChild(instrId);

            Element endToEndId = doc.createElement("EndToEndId");
            endToEndId.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getPaymentIdentification().getEndToEndIdentification()));
            pmtId.appendChild(endToEndId);

            Element intrBkSttlmAmt = doc.createElement("IntrBkSttlmAmt");
            intrBkSttlmAmt.setAttribute("Ccy", pacs009.getFinancialInstitutionCreditTransfer().getInterbankSettlementAmount().getCurrency());
            intrBkSttlmAmt.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getInterbankSettlementAmount().getValue().toPlainString()));
            finInstnCdtTrf.appendChild(intrBkSttlmAmt);

            Element intrBkSttlmDt = doc.createElement("IntrBkSttlmDt");
            intrBkSttlmDt.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd").format(pacs009.getFinancialInstitutionCreditTransfer().getInterbankSettlementDate())));
            finInstnCdtTrf.appendChild(intrBkSttlmDt);

            if (pacs009.getFinancialInstitutionCreditTransfer().getInstructingAgent() != null) {
                Element instgAgt = doc.createElement("InstgAgt");
                finInstnCdtTrf.appendChild(instgAgt);
                Element nm = doc.createElement("Nm");
                nm.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getInstructingAgent().getName()));
                instgAgt.appendChild(nm);
            }

            if (pacs009.getFinancialInstitutionCreditTransfer().getInstructedAgent() != null) {
                Element instdAgt = doc.createElement("InstdAgt");
                finInstnCdtTrf.appendChild(instdAgt);
                Element nm = doc.createElement("Nm");
                nm.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getInstructedAgent().getName()));
                instdAgt.appendChild(nm);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
