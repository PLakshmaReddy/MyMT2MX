package com.example.comparator;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MessageComparator103Test {

    @TempDir
    Path tempDir;

    @Test
    void testCompare_noDifferences() throws IOException, JDOMException, ParseException {
        File mtFile = tempDir.resolve("test.mt103").toFile();
        Files.write(mtFile.toPath(), (
                ":20:TXREF-103-1\n" +
                ":23B:CRED\n" +
                ":32A:240820USD123.45\n" +
                ":50K:/12345678\n" +
                "ORDERING CUSTOMER\n" +
                "123 MAIN ST\n" +
                "ANYTOWN\n" +
                ":59:/98765432\n" +
                "BENEFICIARY CUSTOMER\n" +
                "456 OAK AVE\n" +
                "ANYCITY\n" +
                ":70:INVOICE 123\n" +
                ":71A:SHA\n" +
                ":72:/ACC/This is a test\n"
        ).getBytes());

        File pacsFile = tempDir.resolve("test.pacs008").toFile();
        Files.write(pacsFile.toPath(), (
                "<Document>\n" +
                "    <GrpHdr>\n" +
                "        <MsgId>MSGID-103-1</MsgId>\n" +
                "        <CreDtTm>2024-08-20T10:00:00</CreDtTm>\n" +
                "        <NbOfTxs>1</NbOfTxs>\n" +
                "    </GrpHdr>\n" +
                "    <CdtTrfTxInf>\n" +
                "        <PmtId>\n" +
                "            <InstrId>TXREF-103-1</InstrId>\n" +
                "            <EndToEndId>E2EREF-103-1</EndToEndId>\n" +
                "        </PmtId>\n" +
                "        <IntrBkSttlmAmt Ccy=\"USD\">123.45</IntrBkSttlmAmt>\n" +
                "        <IntrBkSttlmDt>2024-08-20</IntrBkSttlmDt>\n" +
                "        <ChrgBr>SHAR</ChrgBr>\n" +
                "        <Dbtr>\n" +
                "            <Nm>ORDERING CUSTOMER</Nm>\n" +
                "            <PstlAdr>\n" +
                "                <AdrLine>123 MAIN ST</AdrLine>\n" +
                "                <AdrLine>ANYTOWN</AdrLine>\n" +
                "            </PstlAdr>\n" +
                "        </Dbtr>\n" +
                "        <Cdtr>\n" +
                "            <Nm>BENEFICIARY CUSTOMER</Nm>\n" +
                "            <PstlAdr>\n" +
                "                <AdrLine>456 OAK AVE</AdrLine>\n" +
                "                <AdrLine>ANYCITY</AdrLine>\n" +
                "            </PstlAdr>\n" +
                "        </Cdtr>\n" +
                "        <RmtInf>\n" +
                "            <Ustrd>INVOICE 123</Ustrd>\n" +
                "        </RmtInf>\n" +
                "    </CdtTrfTxInf>\n" +
                "</Document>"
        ).getBytes());

        MessageComparator103 comparator = new MessageComparator103();
        ComparisonResult result = comparator.compare(mtFile, pacsFile);

        assertFalse(result.hasDifferences());
    }
}
