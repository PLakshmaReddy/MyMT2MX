package com.example.comparator;

import com.example.converter.MT202;
import com.example.converter.MT202Parser;
import com.example.converter.Pacs009;
import com.example.converter.Pacs009Parser;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class MessageComparator {

    private final MT202Parser mt202Parser = new MT202Parser();
    private final Pacs009Parser pacs009Parser = new Pacs009Parser();

    public ComparisonResult compare(File mtFile, File pacsFile) throws IOException, JDOMException, ParseException {
        String mtContent = new String(Files.readAllBytes(mtFile.toPath()));

        MT202 mt202 = mt202Parser.parse(mtContent);
        Pacs009 pacs009 = pacs009Parser.parse(pacsFile);

        String transactionReference = mt202.getTransactionReferenceNumber();
        ComparisonResult result = new ComparisonResult(transactionReference);

        // Compare Transaction Reference
        compareAndAddDifference(result, "Transaction Reference", transactionReference, pacs009.getFinancialInstitutionCreditTransfer().getPaymentIdentification().getInstructionIdentification());

        // Compare Related Reference
        compareAndAddDifference(result, "Related Reference", mt202.getRelatedReference(), pacs009.getFinancialInstitutionCreditTransfer().getPaymentIdentification().getEndToEndIdentification());

        // Compare Value Date
        String mtDate = new SimpleDateFormat("yyyy-MM-dd").format(mt202.getValueDate());
        String pacsDate = new SimpleDateFormat("yyyy-MM-dd").format(pacs009.getFinancialInstitutionCreditTransfer().getInterbankSettlementDate());
        compareAndAddDifference(result, "Value Date", mtDate, pacsDate);

        // Compare Amount
        String mtAmount = mt202.getAmount().toPlainString();
        String pacsAmount = pacs009.getFinancialInstitutionCreditTransfer().getInterbankSettlementAmount().getValue().toPlainString();
        compareAndAddDifference(result, "Amount", mtAmount, pacsAmount);

        // Compare Currency
        compareAndAddDifference(result, "Currency", mt202.getCurrency(), pacs009.getFinancialInstitutionCreditTransfer().getInterbankSettlementAmount().getCurrency());

        // Compare Ordering Institution
        if(mt202.getOrderingInstitution() != null && pacs009.getFinancialInstitutionCreditTransfer().getInstructingAgent() != null)
        {
            compareAndAddDifference(result, "Ordering Institution BIC", mt202.getOrderingInstitution().getBic(), pacs009.getFinancialInstitutionCreditTransfer().getInstructingAgent().getBic());
        }


        // Compare Beneficiary Institution
        if(mt202.getBeneficiaryInstitution() != null && pacs009.getFinancialInstitutionCreditTransfer().getInstructedAgent() != null)
        {
            compareAndAddDifference(result, "Beneficiary Institution BIC", mt202.getBeneficiaryInstitution().getBic(), pacs009.getFinancialInstitutionCreditTransfer().getInstructedAgent().getBic());
        }

        // Compare Sender to Receiver Information
        compareAndAddDifference(result, "Sender to Receiver Information", mt202.getSenderToReceiverInformation(), pacs009.getFinancialInstitutionCreditTransfer().getSenderToReceiverInformation());

        return result;
    }

    private void compareAndAddDifference(ComparisonResult result, String fieldName, String mtValue, String pacsValue) {
        if (!Objects.equals(mtValue, pacsValue)) {
            result.addDifference(fieldName, mtValue, pacsValue);
        }
    }
}
