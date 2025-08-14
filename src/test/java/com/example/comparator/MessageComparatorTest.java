package com.example.comparator;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class MessageComparatorTest {

    @TempDir
    Path tempDir;

    @Test
    void testCompare_noDifferences() throws IOException, JDOMException, ParseException {
        File mtFile = tempDir.resolve("test_no_diff.mt202").toFile();
        Files.write(mtFile.toPath(), (
                ":20:TXREF123\n" +
                ":21:RELREF456\n" +
                ":32A:240815USD1000.00\n" +
                ":52A:BANKUS33\n" +
                ":53A:BANKUS34\n" +
                ":58A:BANKGB2L\n" +
                ":72:/INFO/some info\n"
        ).getBytes());

        File pacsFile = tempDir.resolve("test_no_diff.pacs009").toFile();
        Files.write(pacsFile.toPath(), (
                "<Document>\n" +
                "    <GrpHdr>\n" +
                "        <MsgId>MSGID123</MsgId>\n" +
                "        <CreDtTm>2024-08-15T10:00:00</CreDtTm>\n" +
                "        <NbOfTxs>1</NbOfTxs>\n" +
                "        <SttlmInf>\n" +
                "            <SttlmMtd>INDA</SttlmMtd>\n" +
                "        </SttlmInf>\n" +
                "    </GrpHdr>\n" +
                "    <FinInstnCdtTrf>\n" +
                "        <PmtId>\n" +
                "            <InstrId>TXREF123</InstrId>\n" +
                "            <EndToEndId>RELREF456</EndToEndId>\n" +
                "        </PmtId>\n" +
                "        <IntrBkSttlmAmt Ccy=\"USD\">1000.00</IntrBkSttlmAmt>\n" +
                "        <IntrBkSttlmDt>2024-08-15</IntrBkSttlmDt>\n" +
                "        <InstgAgt><FinInstnId><BICFI>BANKUS33</BICFI></FinInstnId></InstgAgt>\n" +
                "        <IntrmyAgt1><FinInstnId><BICFI>BANKUS34</BICFI></FinInstnId></IntrmyAgt1>\n" +
                "        <InstdAgt><FinInstnId><BICFI>BANKGB2L</BICFI></FinInstnId></InstdAgt>\n" +
                "        <Ustrd>/INFO/some info</Ustrd>\n" +
                "    </FinInstnCdtTrf>\n" +
                "</Document>"
        ).getBytes());

        MessageComparator comparator = new MessageComparator();
        ComparisonResult result = comparator.compare(mtFile, pacsFile);

        assertFalse(result.hasDifferences());
    }

    @Test
    void testCompare_withDifferences() throws IOException, JDOMException, ParseException {
        File mtFile = tempDir.resolve("test_with_diff.mt202").toFile();
        Files.write(mtFile.toPath(), (
                ":20:TXREF789\n" +
                ":21:RELREF012\n" +
                ":32A:240816EUR2000.50\n" +
                ":52A:BANKDEFF\n" +
                ":53A:BANKDEGG\n" +
                ":58A:BANKFRPP\n" +
                ":72:/INFO/some other info\n"
        ).getBytes());

        File pacsFile = tempDir.resolve("test_with_diff.pacs009").toFile();
        Files.write(pacsFile.toPath(), (
                "<Document>\n" +
                "    <GrpHdr>\n" +
                "        <MsgId>MSGID789</MsgId>\n" +
                "        <CreDtTm>2024-08-16T11:00:00</CreDtTm>\n" +
                "        <NbOfTxs>1</NbOfTxs>\n" +
                "        <SttlmInf>\n" +
                "            <SttlmMtd>INDA</SttlmMtd>\n" +
                "        </SttlmInf>\n" +
                "    </GrpHdr>\n" +
                "    <FinInstnCdtTrf>\n" +
                "        <PmtId>\n" +
                "            <InstrId>TXREF789-DIFFERENT</InstrId>\n" +
                "            <EndToEndId>RELREF012-DIFFERENT</EndToEndId>\n" +
                "        </PmtId>\n" +
                "        <IntrBkSttlmAmt Ccy=\"EUR\">2000.51</IntrBkSttlmAmt>\n" +
                "        <IntrBkSttlmDt>2024-08-17</IntrBkSttlmDt>\n" +
                "        <InstgAgt><FinInstnId><BICFI>BANKDEFF</BICFI></FinInstnId></InstgAgt>\n" +
                "        <IntrmyAgt1><FinInstnId><BICFI>BANKDEHH</BICFI></FinInstnId></IntrmyAgt1>\n" +
                "        <InstdAgt><FinInstnId><BICFI>BANKNL2A</BICFI></FinInstnId></InstdAgt>\n" +
                "        <Ustrd>/INFO/some other info DIFFERENT</Ustrd>\n" +
                "    </FinInstnCdtTrf>\n" +
                "</Document>"
        ).getBytes());

        MessageComparator comparator = new MessageComparator();
        ComparisonResult result = comparator.compare(mtFile, pacsFile);

        assertTrue(result.hasDifferences());
        assertEquals(7, result.getDifferences().size());

        // Check for specific differences
        assertTrue(result.getDifferences().stream().anyMatch(d -> d.getFieldName().equals("Transaction Reference")));
        assertTrue(result.getDifferences().stream().anyMatch(d -> d.getFieldName().equals("Related Reference")));
        assertTrue(result.getDifferences().stream().anyMatch(d -> d.getFieldName().equals("Amount")));
        assertTrue(result.getDifferences().stream().anyMatch(d -> d.getFieldName().equals("Value Date")));
        assertTrue(result.getDifferences().stream().anyMatch(d -> d.getFieldName().equals("Beneficiary Institution BIC")));
        assertTrue(result.getDifferences().stream().anyMatch(d -> d.getFieldName().equals("Sender to Receiver Information")));
        assertTrue(result.getDifferences().stream().anyMatch(d -> d.getFieldName().equals("Intermediary Agent 1 BIC")));
    }
}
