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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MT202ToPacs009Converter implements Converter {

    private final Configuration configuration;

    public MT202ToPacs009Converter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String convert(String mt202Message) {
        MT202 mt202 = parseMt202(mt202Message);
        Pacs009 pacs009 = mapToPacs009(mt202);
        return generatePacs009(pacs009);
    }

    private MT202 parseMt202(String mt202Message) {
        MT202 mt202 = new MT202();

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
        mt202.setSendersCorrespondent(parseParty(mt202Message, "53"));
        mt202.setReceiversCorrespondent(parseParty(mt202Message, "54"));
        mt202.setIntermediary(parseParty(mt202Message, "56"));
        mt202.setAccountWithInstitution(parseParty(mt202Message, "57"));
        mt202.setBeneficiaryInstitution(parseParty(mt202Message, "58"));

        mt202.setSenderToReceiverInformation(getTagValue(mt202Message, "72"));

        return mt202;
    }

    private MT202.Party parseParty(String mt202Message, String tag) {
        String tagAValue = getTagValue(mt202Message, tag + "A");
        if (tagAValue != null) {
            MT202.Party party = new MT202.Party();
            party.setBic(tagAValue);
            return party;
        }

        String tagDValue = getTagValue(mt202Message, tag + "D");
        if (tagDValue != null) {
            MT202.Party party = new MT202.Party();
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

    private Pacs009 mapToPacs009(MT202 mt202) {
        Pacs009 pacs009 = new Pacs009();

        // Group Header
        Pacs009.GroupHeader groupHeader = new Pacs009.GroupHeader();
        groupHeader.setMessageIdentification(UUID.randomUUID().toString());
        groupHeader.setCreationDateTime(new Date());
        groupHeader.setNumberOfTransactions(1);
        Pacs009.SettlementInformation settlementInformation = new Pacs009.SettlementInformation();
        settlementInformation.setSettlementMethod(configuration.getProperty("settlement.method"));
        groupHeader.setSettlementInformation(settlementInformation);
        pacs009.setGroupHeader(groupHeader);

        // Financial Institution Credit Transfer
        Pacs009.FinancialInstitutionCreditTransfer fiCdtTrf = new Pacs009.FinancialInstitutionCreditTransfer();

        Pacs009.PaymentIdentification pmtId = new Pacs009.PaymentIdentification();
        pmtId.setInstructionIdentification(mt202.getTransactionReferenceNumber());
        pmtId.setEndToEndIdentification(mt202.getRelatedReference());
        fiCdtTrf.setPaymentIdentification(pmtId);

        Pacs009.Amount amount = new Pacs009.Amount();
        amount.setCurrency(mt202.getCurrency());
        amount.setValue(mt202.getAmount());
        fiCdtTrf.setInterbankSettlementAmount(amount);

        fiCdtTrf.setInterbankSettlementDate(mt202.getValueDate());

        fiCdtTrf.setInstructingAgent(mapParty(mt202.getOrderingInstitution()));
        fiCdtTrf.setInstructedAgent(mapParty(mt202.getBeneficiaryInstitution()));
        fiCdtTrf.setSendersCorrespondent(mapParty(mt202.getSendersCorrespondent()));
        fiCdtTrf.setReceiversCorrespondent(mapParty(mt202.getReceiversCorrespondent()));
        fiCdtTrf.setIntermediary(mapParty(mt202.getIntermediary()));
        if (mt202.getAccountWithInstitution() != null) {
            if (mt202.getAccountWithInstitution().getBic() != null) {
                fiCdtTrf.setInstructionForNextAgent(mt202.getAccountWithInstitution().getBic());
            } else {
                fiCdtTrf.setInstructionForNextAgent(mt202.getAccountWithInstitution().getNameAndAddress());
            }
        }
        fiCdtTrf.setRemittanceInformation(mt202.getSenderToReceiverInformation());

        pacs009.setFinancialInstitutionCreditTransfer(fiCdtTrf);

        return pacs009;
    }

    private Pacs009.Party mapParty(MT202.Party mtParty) {
        if (mtParty == null) {
            return null;
        }
        Pacs009.Party pacsParty = new Pacs009.Party();
        pacsParty.setBic(mtParty.getBic());
        pacsParty.setAccountNumber(mtParty.getAccountNumber());
        pacsParty.setName(mtParty.getNameAndAddress());
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
                Element finInstnId = doc.createElement("FinInstnId");
                instgAgt.appendChild(finInstnId);
                Element bicfi = doc.createElement("BICFI");
                bicfi.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getInstructingAgent().getBic()));
                finInstnId.appendChild(bicfi);
            }

            if (pacs009.getFinancialInstitutionCreditTransfer().getInstructedAgent() != null) {
                Element instdAgt = doc.createElement("InstdAgt");
                finInstnCdtTrf.appendChild(instdAgt);
                Element finInstnId = doc.createElement("FinInstnId");
                instdAgt.appendChild(finInstnId);
                Element bicfi = doc.createElement("BICFI");
                bicfi.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getInstructedAgent().getBic()));
                finInstnId.appendChild(bicfi);
            }

            if (pacs009.getFinancialInstitutionCreditTransfer().getSendersCorrespondent() != null) {
                Element sndrsCorr = doc.createElement("SndrsCorr");
                finInstnCdtTrf.appendChild(sndrsCorr);
                Element finInstnId = doc.createElement("FinInstnId");
                sndrsCorr.appendChild(finInstnId);
                Element bicfi = doc.createElement("BICFI");
                bicfi.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getSendersCorrespondent().getBic()));
                finInstnId.appendChild(bicfi);
            }

            if (pacs009.getFinancialInstitutionCreditTransfer().getReceiversCorrespondent() != null) {
                Element rcvrsCorr = doc.createElement("RcvrsCorr");
                finInstnCdtTrf.appendChild(rcvrsCorr);
                Element finInstnId = doc.createElement("FinInstnId");
                rcvrsCorr.appendChild(finInstnId);
                Element bicfi = doc.createElement("BICFI");
                bicfi.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getReceiversCorrespondent().getBic()));
                finInstnId.appendChild(bicfi);
            }

            if (pacs009.getFinancialInstitutionCreditTransfer().getIntermediary() != null) {
                Element intrmyAgt1 = doc.createElement("IntrmyAgt1");
                finInstnCdtTrf.appendChild(intrmyAgt1);
                Element finInstnId = doc.createElement("FinInstnId");
                intrmyAgt1.appendChild(finInstnId);
                Element bicfi = doc.createElement("BICFI");
                bicfi.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getIntermediary().getBic()));
                finInstnId.appendChild(bicfi);
            }

            if (pacs009.getFinancialInstitutionCreditTransfer().getInstructionForNextAgent() != null) {
                Element instrForNxtAgt = doc.createElement("InstrForNxtAgt");
                finInstnCdtTrf.appendChild(instrForNxtAgt);
                Element instrInf = doc.createElement("InstrInf");
                instrInf.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getInstructionForNextAgent()));
                instrForNxtAgt.appendChild(instrInf);
            }

            if (pacs009.getFinancialInstitutionCreditTransfer().getRemittanceInformation() != null) {
                Element rmtInf = doc.createElement("RmtInf");
                finInstnCdtTrf.appendChild(rmtInf);
                Element ustrd = doc.createElement("Ustrd");
                ustrd.appendChild(doc.createTextNode(pacs009.getFinancialInstitutionCreditTransfer().getRemittanceInformation()));
                rmtInf.appendChild(ustrd);
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
