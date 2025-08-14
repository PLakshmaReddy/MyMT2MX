package com.example.converter;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MT202ToPacs008ConverterTest {

    @Test
    public void testConvert() {
        String mt202Message = "{1:F01BANKDEFMAXXX2034567890}{2:I202BANKUS33XXXXN}{3:{108:MT202}}{4:\n" +
                ":20:REFERENCE\n" +
                ":21:RELATEDREF\n" +
                ":32A:240813USD1234,56\n" +
                ":52D:ORDERINGBANK\n" +
                "ACCOUNT 12345\n" +
                "NEW YORK US\n" +
                ":58A:BENEFICIARYBANK\n" +
                "-}{5:{CHK:0123456789AB}}";

        Converter converter = new MT202ToPacs008Converter(new Configuration());
        String pacs008Message = converter.convert(mt202Message);

        assertNotNull(pacs008Message);
        System.out.println("Generated XML:\n" + pacs008Message);

        // Assertions for generated XML
        assertTrue(pacs008Message.contains("<MsgId>"));
        assertTrue(pacs008Message.contains("<CreDtTm>"));
        assertTrue(pacs008Message.contains("<NbOfTxs>1</NbOfTxs>"));
        assertTrue(pacs008Message.contains("<SttlmMtd>INDA</SttlmMtd>"));
        assertTrue(pacs008Message.contains("<InstrId>REFERENCE</InstrId>"));
        assertTrue(pacs008Message.contains("<EndToEndId>RELATEDREF</EndToEndId>"));
        assertTrue(pacs008Message.contains("<IntrBkSttlmAmt Ccy=\"USD\">1234.56</IntrBkSttlmAmt>"));
        assertTrue(pacs008Message.contains("<IntrBkSttlmDt>2024-08-13</IntrBkSttlmDt>"));
        assertTrue(pacs008Message.contains("<Nm>ORDERINGBANK</Nm>"));
        assertTrue("The pacs.008 message should contain the beneficiary name.", pacs008Message.contains("<Nm>BENEFICIARYBANK</Nm>"));
    }
}
