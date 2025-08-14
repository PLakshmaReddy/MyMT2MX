package com.example.converter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MT202Parser {

    public MT202 parse(String mt202Message) {
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
        mt202.setIntermediaryInstitution1(parseParty(mt202Message, "53"));
        mt202.setIntermediaryInstitution2(parseParty(mt202Message, "54"));
        mt202.setReceivingAgent(parseParty(mt202Message, "56"));
        mt202.setBeneficiarysCorrespondent(parseParty(mt202Message, "57"));
        mt202.setBeneficiaryInstitution(parseParty(mt202Message, "58"));

        mt202.setSenderToReceiverInformation(getTagValue(mt202Message, "72"));

        return mt202;
    }

    private MT202.Party parseParty(String mt202Message, String tag) {
        String tagAValue = getTagValue(mt202Message, tag + "A");
        if (tagAValue != null) {
            MT202.Party party = new MT202.Party();
            String[] lines = tagAValue.split("\\n");
            if (lines.length > 0) {
                if (lines[0].startsWith("/")) {
                    party.setAccountNumber(lines[0].substring(1));
                    if (lines.length > 1) {
                        party.setBic(lines[1]);
                    }
                } else {
                    party.setBic(lines[0]);
                }
            }
            return party;
        }

        String tagDValue = getTagValue(mt202Message, tag + "D");
        if (tagDValue != null) {
            MT202.Party party = new MT202.Party();
            String[] lines = tagDValue.split("\\n");
            int nameAndAddressStartIndex = 0;
            if (lines.length > 0 && lines[0].startsWith("/")) {
                party.setAccountNumber(lines[0].substring(1));
                nameAndAddressStartIndex = 1;
            }
            StringBuilder nameAndAddress = new StringBuilder();
            for (int i = nameAndAddressStartIndex; i < lines.length; i++) {
                nameAndAddress.append(lines[i]);
                if (i < lines.length - 1) {
                    nameAndAddress.append("\n");
                }
            }
            party.setNameAndAddress(nameAndAddress.toString());
            return party;
        }

        return null;
    }

    private String getTagValue(String message, String tag) {
        // DOTALL flag (?s) allows . to match newline characters.
        Pattern pattern = Pattern.compile("(?s):" + tag + ":(.*?)(?:\\n:|\\n-}|$)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
