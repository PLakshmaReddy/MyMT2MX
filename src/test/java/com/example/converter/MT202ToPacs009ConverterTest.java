package com.example.converter;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MT202ToPacs009ConverterTest {

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

        Converter converter = new MT202ToPacs009Converter(new Configuration());
        String pacs009Message = converter.convert(mt202Message);

        assertNotNull(pacs009Message);
        System.out.println("Generated XML:\n" + pacs009Message);

        // Assertions for generated XML
        assertTrue(pacs009Message.contains("<MsgId>"));
        assertTrue(pacs009Message.contains("<CreDtTm>"));
        assertTrue(pacs009Message.contains("<NbOfTxs>1</NbOfTxs>"));
        assertTrue(pacs009Message.contains("<SttlmMtd>INDA</SttlmMtd>"));
        assertTrue(pacs009Message.contains("<InstrId>REFERENCE</InstrId>"));
        assertTrue(pacs009Message.contains("<EndToEndId>RELATEDREF</EndToEndId>"));
        assertTrue(pacs009Message.contains("<IntrBkSttlmAmt Ccy=\"USD\">1234.56</IntrBkSttlmAmt>"));
        assertTrue(pacs009Message.contains("<IntrBkSttlmDt>2024-08-13</IntrBkSttlmDt>"));
        assertTrue(pacs009Message.contains("<InstgAgt>"));
        assertTrue(pacs009Message.contains("<InstdAgt>"));
        assertTrue(pacs009Message.contains("<Nm>ORDERINGBANK</Nm>"));
        assertTrue("The pacs.009 message should contain the beneficiary name.", pacs009Message.contains("<Nm>BENEFICIARYBANK</Nm>"));
    }
}
