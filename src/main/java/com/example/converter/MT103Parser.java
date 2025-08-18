package com.example.converter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MT103Parser {

    public MT103 parse(String mt103Message) {
        MT103 mt103 = new MT103();

        mt103.setSendersReference(getTagValue(mt103Message, "20"));
        mt103.setBankOperationCode(getTagValue(mt103Message, "23B"));

        String field32AValue = getTagValue(mt103Message, "32A");
        if (field32AValue != null) {
            try {
                Date date = new SimpleDateFormat("yyMMdd").parse(field32AValue.substring(0, 6));
                mt103.setValueDate(date);
                mt103.setCurrency(field32AValue.substring(6, 9));
                mt103.setAmount(new BigDecimal(field32AValue.substring(9).replace(",", ".")));
            } catch (ParseException e) {
                // Handle exception
            }
        }

        mt103.setOrderingCustomer(parseParty(mt103Message, "50"));
        mt103.setOrderingInstitution(parseParty(mt103Message, "52"));
        mt103.setSendersCorrespondent(parseParty(mt103Message, "53"));
        mt103.setReceiversCorrespondent(parseParty(mt103Message, "54"));
        mt103.setIntermediaryInstitution(parseParty(mt103Message, "56"));
        mt103.setAccountWithInstitution(parseParty(mt103Message, "57"));
        mt103.setBeneficiaryCustomer(parseParty(mt103Message, "59"));
        mt103.setRemittanceInformation(getTagValue(mt103Message, "70"));
        mt103.setDetailsOfCharges(getTagValue(mt103Message, "71A"));
        mt103.setSenderToReceiverInformation(getTagValue(mt103Message, "72"));

        return mt103;
    }

    private MT103.Party parseParty(String mt103Message, String tag) {
        String tagAValue = getTagValue(mt103Message, tag + "A");
        if (tagAValue != null) {
            MT103.Party party = new MT103.Party();
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

        String tagDValue = getTagValue(mt103Message, tag + "D");
        if (tagDValue != null) {
            MT103.Party party = new MT103.Party();
            String[] lines = tagDValue.split("\\n");
            int nameAndAddressStartIndex = 0;
            if (lines.length > 0 && lines[0].startsWith("/")) {
                if (lines[0].startsWith("/DSS/")) {
                    String[] dssParts = lines[0].split("/");
                    if (dssParts.length > 2) {
                        party.setDataSourceScheme(dssParts[2]);
                    }
                } else {
                    party.setAccountNumber(lines[0].substring(1));
                }
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

        // Handle option F for tag 50 and 59
        String tagFValue = getTagValue(mt103Message, tag + "F");
        if (tagFValue != null) {
            MT103.Party party = new MT103.Party();
            party.setNameAndAddress(tagFValue);
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
