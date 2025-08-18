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

    @Test
    void testCompare_complexCase() throws IOException, JDOMException, ParseException {
        File mtFile = tempDir.resolve("transaction2.mt202").toFile();
        Files.write(mtFile.toPath(), (
                ":20:TXREF-COMPLEX\n" +
                ":21:RELREF-COMPLEX\n" +
                ":32A:240817CAD54321.98\n" +
                ":52A:BANKCATT\n" +
                ":56A:BANKDEFF\n" +
                ":57A:BANKNL2A\n" +
                ":58A:BANKGB2L\n" +
                ":72:/INFO/complex case\n"
        ).getBytes());

        File pacsFile = tempDir.resolve("transaction2.pacs009").toFile();
        Files.write(pacsFile.toPath(), (
                "<Document>\n" +
                "    <GrpHdr>\n" +
                "        <MsgId>MSGID-COMPLEX</MsgId>\n" +
                "        <CreDtTm>2024-08-17T12:00:00</CreDtTm>\n" +
                "        <NbOfTxs>1</NbOfTxs>\n" +
                "        <SttlmInf>\n" +
                "            <SttlmMtd>INDA</SttlmMtd>\n" +
                "        </SttlmInf>\n" +
                "    </GrpHdr>\n" +
                "    <FinInstnCdtTrf>\n" +
                "        <PmtId>\n" +
                "            <InstrId>TXREF-COMPLEX</InstrId>\n" +
                "            <EndToEndId>RELREF-COMPLEX</EndToEndId>\n" +
                "        </PmtId>\n" +
                "        <IntrBkSttlmAmt Ccy=\"CAD\">54321.98</IntrBkSttlmAmt>\n" +
                "        <IntrBkSttlmDt>2024-08-17</IntrBkSttlmDt>\n" +
                "        <InstgAgt><FinInstnId><BICFI>BANKCATT</BICFI></FinInstnId></InstgAgt>\n" +
                "        <IntrmyAgt3><FinInstnId><BICFI>BANKDEFF</BICFI></FinInstnId></IntrmyAgt3>\n" +
                "        <InstdAgt><FinInstnId><BICFI>BANKGB2L</BICFI></FinInstnId></InstdAgt>\n" +
                "        <Ustrd>/INFO/complex case</Ustrd>\n" +
                "    </FinInstnCdtTrf>\n" +
                "</Document>"
        ).getBytes());

        MessageComparator comparator = new MessageComparator();
        ComparisonResult result = comparator.compare(mtFile, pacsFile);

        assertFalse(result.hasDifferences());
    }

    @Test
    void testCompare_withD_option() throws IOException, JDOMException, ParseException {
        File mtFile = tempDir.resolve("test_d_option.mt202").toFile();
        Files.write(mtFile.toPath(), (
                ":20:TXREF-D-OPT\n" +
                ":21:RELREF-D-OPT\n" +
                ":32A:240818JPY100000\n" +
                ":53D:/12345\n" +
                "TEST BANK\n" +
                "TEST ADDRESS\n" +
                ":58A:BANKJPJT\n"
        ).getBytes());

        File pacsFile = tempDir.resolve("test_d_option.pacs009").toFile();
        Files.write(pacsFile.toPath(), (
                "<Document>\n" +
                "    <GrpHdr>\n" +
                "        <MsgId>MSGID-D-OPT</MsgId>\n" +
                "        <CreDtTm>2024-08-18T13:00:00</CreDtTm>\n" +
                "        <NbOfTxs>1</NbOfTxs>\n" +
                "    </GrpHdr>\n" +
                "    <FinInstnCdtTrf>\n" +
                "        <PmtId>\n" +
                "            <InstrId>TXREF-D-OPT</InstrId>\n" +
                "            <EndToEndId>RELREF-D-OPT</EndToEndId>\n" +
                "        </PmtId>\n" +
                "        <IntrBkSttlmAmt Ccy=\"JPY\">100000</IntrBkSttlmAmt>\n" +
                "        <IntrBkSttlmDt>2024-08-18</IntrBkSttlmDt>\n" +
                "        <IntrmyAgt1>\n" +
                "            <FinInstnId>\n" +
                "                <Nm>TEST BANK</Nm>\n" +
                "                <PstlAdr>\n" +
                "                    <AdrLine>TEST ADDRESS</AdrLine>\n" +
                "                </PstlAdr>\n" +
                "            </FinInstnId>\n" +
                "        </IntrmyAgt1>\n" +
                "        <InstdAgt><FinInstnId><BICFI>BANKJPJT</BICFI></FinInstnId></InstdAgt>\n" +
                "    </FinInstnCdtTrf>\n" +
                "</Document>"
        ).getBytes());

        MessageComparator comparator = new MessageComparator();
        ComparisonResult result = comparator.compare(mtFile, pacsFile);

        assertFalse(result.hasDifferences());
    }

    @Test
    void testCompare_withA_option_accountAndBic() throws IOException, JDOMException, ParseException {
        File mtFile = tempDir.resolve("test_a_option.mt202").toFile();
        Files.write(mtFile.toPath(), (
                ":20:TXREF-A-OPT\n" +
                ":21:RELREF-A-OPT\n" +
                ":32A:240819USD5000\n" +
                ":52A:/987654\n" +
                "BANKUS33\n" +
                ":58A:BANKGB2L\n"
        ).getBytes());

        File pacsFile = tempDir.resolve("test_a_option.pacs009").toFile();
        Files.write(pacsFile.toPath(), (
                "<Document>\n" +
                "    <GrpHdr>\n" +
                "        <MsgId>MSGID-A-OPT</MsgId>\n" +
                "        <CreDtTm>2024-08-19T14:00:00</CreDtTm>\n" +
                "        <NbOfTxs>1</NbOfTxs>\n" +
                "    </GrpHdr>\n" +
                "    <FinInstnCdtTrf>\n" +
                "        <PmtId>\n" +
                "            <InstrId>TXREF-A-OPT</InstrId>\n" +
                "            <EndToEndId>RELREF-A-OPT</EndToEndId>\n" +
                "        </PmtId>\n" +
                "        <IntrBkSttlmAmt Ccy=\"USD\">5000</IntrBkSttlmAmt>\n" +
                "        <IntrBkSttlmDt>2024-08-19</IntrBkSttlmDt>\n" +
                "        <InstgAgt>\n" +
                "           <FinInstnId><BICFI>BANKUS33</BICFI></FinInstnId>\n" +
                "        </InstgAgt>\n" +
                "        <InstdAgt><FinInstnId><BICFI>BANKGB2L</BICFI></FinInstnId></InstdAgt>\n" +
                "    </FinInstnCdtTrf>\n" +
                "</Document>"
        ).getBytes());

        MessageComparator comparator = new MessageComparator();
        ComparisonResult result = comparator.compare(mtFile, pacsFile);

        assertFalse(result.hasDifferences());
    }

    @Test
    void testCompare_withDSS_difference() throws IOException, JDOMException, ParseException {
        File mtFile = tempDir.resolve("test_dss_diff.mt202").toFile();
        Files.write(mtFile.toPath(), (
                ":20:TXREF-DSS-DIFF\n" +
                ":32A:240819EUR9999\n" +
                ":53D:/DSS/NAT/1234\n" +
                "TEST BANK DSS\n" +
                "TEST ADDRESS DSS\n" +
                ":58A:BANKDEFF\n"
        ).getBytes());

        File pacsFile = tempDir.resolve("test_dss_diff.pacs009").toFile();
        Files.write(pacsFile.toPath(), (
                "<Document>\n" +
                "    <GrpHdr>\n" +
                "        <MsgId>MSGID-DSS-DIFF</MsgId>\n" +
                "        <CreDtTm>2024-08-19T15:00:00</CreDtTm>\n" +
                "        <NbOfTxs>1</NbOfTxs>\n" +
                "    </GrpHdr>\n" +
                "    <FinInstnCdtTrf>\n" +
                "        <PmtId>\n" +
                "            <InstrId>TXREF-DSS-DIFF</InstrId>\n" +
                "        </PmtId>\n" +
                "        <IntrBkSttlmAmt Ccy=\"EUR\">9999</IntrBkSttlmAmt>\n" +
                "        <IntrBkSttlmDt>2024-08-19</IntrBkSttlmDt>\n" +
                "        <IntrmyAgt1>\n" +
                "            <FinInstnId>\n" +
                "                <Nm>TEST BANK DSS</Nm>\n" +
                "                <PstlAdr>\n" +
                "                    <AdrLine>TEST ADDRESS DSS</AdrLine>\n" +
                "                </PstlAdr>\n" +
                "                <Othr>\n" +
                "                    <SchmeNm><Cd>CUID</Cd></SchmeNm>\n" +
                "                </Othr>\n" +
                "            </FinInstnId>\n" +
                "        </IntrmyAgt1>\n" +
                "        <InstdAgt><FinInstnId><BICFI>BANKDEFF</BICFI></FinInstnId></InstdAgt>\n" +
                "    </FinInstnCdtTrf>\n" +
                "</Document>"
        ).getBytes());

        MessageComparator comparator = new MessageComparator();
        ComparisonResult result = comparator.compare(mtFile, pacsFile);

        assertTrue(result.hasDifferences());
        assertEquals(1, result.getDifferences().size());
        assertEquals("Intermediary Agent 1 Data Source Scheme", result.getDifferences().get(0).getFieldName());
    }
}
