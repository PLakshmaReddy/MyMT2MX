package com.example.converter;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MT202ToPacs009ConverterTest {

    @Test
    public void testConvert() {
        String mt202Message = "{1:F01BANKDEFMAXXX2034567890}{2:I202BANKUS33XXXXN}{3:{108:MT202}}{4:\n" +
                ":20:REFERENCE\n" +
                ":21:RELATEDREF\n" +
                ":32A:240813USD1234,56\n" +
                ":52A:/12345\nORDERINGBANK\n" +
                ":53A:SENDERSCORR\n" +
                ":54A:RECEIVERSCORR\n" +
                ":56A:INTERMEDIARY\n" +
                ":57A:ACCOUNTWITHBANK\n" +
                ":58A:BENEFICIARYBANK\n" +
                ":72:REMITTANCE INFO\n" +
                "-}{5:{CHK:0123456789AB}}";

        MT202ToPacs009Converter converter = new MT202ToPacs009Converter(new Configuration());
        MT202 mt202 = converter.parseMt202(mt202Message);
        String pacs009Message = converter.convert(mt202Message);

        assertNotNull(pacs009Message);
        System.out.println("Generated XML:\n" + pacs009Message);

        // Assertions for generated XML
        assertEquals("12345", mt202.getOrderingInstitution().getAccountNumber());
        assertTrue(pacs009Message.contains("<MsgId>"));
        assertTrue(pacs009Message.contains("<CreDtTm>"));
        assertTrue(pacs009Message.contains("<NbOfTxs>1</NbOfTxs>"));
        assertTrue(pacs009Message.contains("<SttlmMtd>INDA</SttlmMtd>"));
        assertTrue(pacs009Message.contains("<InstrId>REFERENCE</InstrId>"));
        assertTrue(pacs009Message.contains("<EndToEndId>RELATEDREF</EndToEndId>"));
        assertTrue(pacs009Message.contains("<IntrBkSttlmAmt Ccy=\"USD\">1234.56</IntrBkSttlmAmt>"));

        Pattern pattern = Pattern.compile("<IntrBkSttlmDt>(.*?)</IntrBkSttlmDt>");
        Matcher matcher = pattern.matcher(pacs009Message);
        assertTrue(matcher.find());
        assertEquals("2024-08-13", matcher.group(1));

        assertTag(pacs009Message, "InstgAgt", "ORDERINGBANK");
        assertTag(pacs009Message, "InstdAgt", "BENEFICIARYBANK");
        assertTag(pacs009Message, "SndrsCorr", "SENDERSCORR");
        assertTag(pacs009Message, "RcvrsCorr", "RECEIVERSCORR");
        assertTag(pacs009Message, "IntrmyAgt1", "INTERMEDIARY");

        Pattern rmtInfPattern = Pattern.compile("<RmtInf><Ustrd>(.*?)</Ustrd></RmtInf>");
        Matcher rmtInfMatcher = rmtInfPattern.matcher(pacs009Message.replaceAll("\\s", ""));
        assertTrue("Remittance Information not found", rmtInfMatcher.find());
        assertEquals("REMITTANCEINFO", rmtInfMatcher.group(1));

        Pattern instrForNxtAgtPattern = Pattern.compile("<InstrForNxtAgt><InstrInf>(.*?)</InstrInf></InstrForNxtAgt>");
        Matcher instrForNxtAgtMatcher = instrForNxtAgtPattern.matcher(pacs009Message.replaceAll("\\s", ""));
        assertTrue("Instruction For Next Agent not found", instrForNxtAgtMatcher.find());
        assertEquals("ACCOUNTWITHBANK", instrForNxtAgtMatcher.group(1));
    }

    private void assertTag(String xml, String tagName, String expectedValue) {
        Pattern pattern = Pattern.compile("<" + tagName + ">.*?<Nm>(.*?)</Nm>.*?</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml.replaceAll("\\s", ""));
        if (matcher.find()) {
            assertEquals(expectedValue, matcher.group(1));
            return;
        }

        pattern = Pattern.compile("<" + tagName + ">.*?<BICFI>(.*?)</BICFI>.*?</" + tagName + ">");
        matcher = pattern.matcher(xml.replaceAll("\\s", ""));
        assertTrue("Tag " + tagName + " not found", matcher.find());
        assertEquals(expectedValue, matcher.group(1));
    }

    @Test
    public void testConvertWithOptionD() {
        String mt202Message = "{1:F01BANKDEFMAXXX2034567890}{2:I202BANKUS33XXXXN}{3:{108:MT202}}{4:\n" +
                ":20:REFERENCE\n" +
                ":21:RELATEDREF\n" +
                ":32A:240813USD1234,56\n" +
                ":52D:ORDERINGBANK\nACCOUNT 12345\n" +
                ":58D:BENEFICIARYBANK\nACCOUNT 67890\n" +
                "-}{5:{CHK:0123456789AB}}";

        Converter converter = new MT202ToPacs009Converter(new Configuration());
        String pacs009Message = converter.convert(mt202Message);

        assertNotNull(pacs009Message);
        System.out.println("Generated XML:\n" + pacs009Message);

        assertTag(pacs009Message, "InstgAgt", "ORDERINGBANK");
        assertTag(pacs009Message, "InstdAgt", "BENEFICIARYBANK");
    }
}
