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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MT202ToPacs008Converter implements Converter {

    private final Configuration configuration;

    public MT202ToPacs008Converter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String convert(String mt202Message) {
        com.example.converter.MT202 mt202 = parseMt202(mt202Message);
        Pacs008 pacs008 = mapToPacs008(mt202);
        return generatePacs008(pacs008);
    }

    private com.example.converter.MT202 parseMt202(String mt202Message) {
        com.example.converter.MT202 mt202 = new com.example.converter.MT202();

        mt202.setTransactionReferenceNumber(getTagValue(mt202Message, "20"));
        mt202.setRelatedReference(getTagValue(mt202Message, "21"));

        String field32AValue = getTagValue(mt202Message, "32A");
        if (field32AValue != null) {
            try {
                Date date = new SimpleDateFormat("yyMMdd").parse(field32AValue.substring(0, 6));
                mt202.setValueDate(date);
                mt202.setCurrency(field32AValue.substring(6, 9));
                mt202.setAmount(new BigDecimal(field32AValue.substring(9).replace(",", ".")));
            } catch (ParseException e) {
                // Handle exception
            }
        }

        mt202.setOrderingInstitution(parseParty(mt202Message, "52"));
        mt202.setBeneficiaryInstitution(parseParty(mt202Message, "58"));

        mt202.setSenderToReceiverInformation(getTagValue(mt202Message, "72"));

        return mt202;
    }

    private com.example.converter.MT202.Party parseParty(String mt202Message, String tag) {
        String tagAValue = getTagValue(mt202Message, tag + "A");
        if (tagAValue != null) {
            com.example.converter.MT202.Party party = new com.example.converter.MT202.Party();
            String[] parts = tagAValue.split("/");
            party.setBic(parts[0]);
            if (parts.length > 1) {
                party.setAccountNumber(parts[1]);
            }
            return party;
        }

        String tagDValue = getTagValue(mt202Message, tag + "D");
        if (tagDValue != null) {
            com.example.converter.MT202.Party party = new com.example.converter.MT202.Party();
            String[] lines = tagDValue.split("\\n");
            party.setNameAndAddress(lines[0]);
            if (lines.length > 1) {
                party.setAccountNumber(lines[1]);
            }
            return party;
        }

        return null;
    }

    private String getTagValue(String message, String tag) {
        // DOTALL flag (?s) allows . to match newline characters.
        Pattern pattern = Pattern.compile("(?s):" + tag + ":(.*?)\\n:");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        pattern = Pattern.compile("(?s):" + tag + ":(.*?)\\n-}");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }


    private Pacs008 mapToPacs008(com.example.converter.MT202 mt202) {
        Pacs008 pacs008 = new Pacs008();

        // Group Header
        Pacs008.GroupHeader groupHeader = new Pacs008.GroupHeader();
        groupHeader.setMessageIdentification(java.util.UUID.randomUUID().toString());
        groupHeader.setCreationDateTime(new java.util.Date());
        groupHeader.setNumberOfTransactions(1);
        Pacs008.SettlementInformation settlementInformation = new Pacs008.SettlementInformation();
        settlementInformation.setSettlementMethod(configuration.getProperty("settlement.method"));
        groupHeader.setSettlementInformation(settlementInformation);
        pacs008.setGroupHeader(groupHeader);

        // Credit Transfer Transaction Information
        Pacs008.CreditTransferTransactionInformation creditTransferTransactionInformation = new Pacs008.CreditTransferTransactionInformation();

        Pacs008.PaymentIdentification paymentIdentification = new Pacs008.PaymentIdentification();
        paymentIdentification.setInstructionIdentification(mt202.getTransactionReferenceNumber());
        paymentIdentification.setEndToEndIdentification(mt202.getRelatedReference());
        creditTransferTransactionInformation.setPaymentIdentification(paymentIdentification);

        Pacs008.Amount amount = new Pacs008.Amount();
        amount.setCurrency(mt202.getCurrency());
        amount.setValue(mt202.getAmount());
        creditTransferTransactionInformation.setInterbankSettlementAmount(amount);

        creditTransferTransactionInformation.setInterbankSettlementDate(mt202.getValueDate());

        creditTransferTransactionInformation.setDebtor(mapParty(mt202.getOrderingInstitution()));
        creditTransferTransactionInformation.setCreditor(mapParty(mt202.getBeneficiaryInstitution()));

        creditTransferTransactionInformation.setRemittanceInformation(mt202.getSenderToReceiverInformation());

        pacs008.setCreditTransferTransactionInformation(creditTransferTransactionInformation);

        return pacs008;
    }

    private Pacs008.Party mapParty(com.example.converter.MT202.Party mtParty) {
        if (mtParty == null) {
            return null;
        }
        Pacs008.Party pacsParty = new Pacs008.Party();
        pacsParty.setBic(mtParty.getBic());
        pacsParty.setAccountNumber(mtParty.getAccountNumber());
        if (mtParty.getNameAndAddress() != null) {
            pacsParty.setName(mtParty.getNameAndAddress());
        } else {
            pacsParty.setName(mtParty.getBic());
        }
        return pacsParty;
    }

    private String generatePacs008(Pacs008 pacs008) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Document");
            doc.appendChild(rootElement);

            Element grpHdr = doc.createElement("GrpHdr");
            rootElement.appendChild(grpHdr);

            Element msgId = doc.createElement("MsgId");
            msgId.appendChild(doc.createTextNode(pacs008.getGroupHeader().getMessageIdentification()));
            grpHdr.appendChild(msgId);

            Element creDtTm = doc.createElement("CreDtTm");
            creDtTm.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(pacs008.getGroupHeader().getCreationDateTime())));
            grpHdr.appendChild(creDtTm);

            Element nbOfTxs = doc.createElement("NbOfTxs");
            nbOfTxs.appendChild(doc.createTextNode(String.valueOf(pacs008.getGroupHeader().getNumberOfTransactions())));
            grpHdr.appendChild(nbOfTxs);

            Element sttlmInf = doc.createElement("SttlmInf");
            grpHdr.appendChild(sttlmInf);

            Element sttlmMtd = doc.createElement("SttlmMtd");
            sttlmMtd.appendChild(doc.createTextNode(pacs008.getGroupHeader().getSettlementInformation().getSettlementMethod()));
            sttlmInf.appendChild(sttlmMtd);

            Element cdtTrfTxInf = doc.createElement("CdtTrfTxInf");
            rootElement.appendChild(cdtTrfTxInf);

            Element pmtId = doc.createElement("PmtId");
            cdtTrfTxInf.appendChild(pmtId);

            Element instrId = doc.createElement("InstrId");
            instrId.appendChild(doc.createTextNode(pacs008.getCreditTransferTransactionInformation().getPaymentIdentification().getInstructionIdentification()));
            pmtId.appendChild(instrId);

            Element endToEndId = doc.createElement("EndToEndId");
            endToEndId.appendChild(doc.createTextNode(pacs008.getCreditTransferTransactionInformation().getPaymentIdentification().getEndToEndIdentification()));
            pmtId.appendChild(endToEndId);

            Element intrBkSttlmAmt = doc.createElement("IntrBkSttlmAmt");
            intrBkSttlmAmt.setAttribute("Ccy", pacs008.getCreditTransferTransactionInformation().getInterbankSettlementAmount().getCurrency());
            intrBkSttlmAmt.appendChild(doc.createTextNode(pacs008.getCreditTransferTransactionInformation().getInterbankSettlementAmount().getValue().toPlainString()));
            cdtTrfTxInf.appendChild(intrBkSttlmAmt);

            Element intrBkSttlmDt = doc.createElement("IntrBkSttlmDt");
            intrBkSttlmDt.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd").format(pacs008.getCreditTransferTransactionInformation().getInterbankSettlementDate())));
            cdtTrfTxInf.appendChild(intrBkSttlmDt);

            if (pacs008.getCreditTransferTransactionInformation().getDebtor() != null) {
                Element dbtr = doc.createElement("Dbtr");
                cdtTrfTxInf.appendChild(dbtr);
                Element nm = doc.createElement("Nm");
                nm.appendChild(doc.createTextNode(pacs008.getCreditTransferTransactionInformation().getDebtor().getName()));
                dbtr.appendChild(nm);
            }

            if (pacs008.getCreditTransferTransactionInformation().getCreditor() != null) {
                Element cdtr = doc.createElement("Cdtr");
                cdtTrfTxInf.appendChild(cdtr);
                Element nm = doc.createElement("Nm");
                nm.appendChild(doc.createTextNode(pacs008.getCreditTransferTransactionInformation().getCreditor().getName()));
                cdtr.appendChild(nm);
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
