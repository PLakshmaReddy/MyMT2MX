package com.example.comparator;

import com.example.converter.MT103;
import com.example.converter.MT103Parser;
import com.example.converter.Pacs008;
import com.example.converter.Pacs008Parser;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class MessageComparator103 {

    private final MT103Parser mt103Parser = new MT103Parser();
    private final Pacs008Parser pacs008Parser = new Pacs008Parser();

    public ComparisonResult compare(File mtFile, File pacsFile) throws IOException, JDOMException, ParseException {
        String mtContent = new String(Files.readAllBytes(mtFile.toPath()));

        MT103 mt103 = mt103Parser.parse(mtContent);
        Pacs008 pacs008 = pacs008Parser.parse(pacsFile);

        String transactionReference = mt103.getSendersReference();
        ComparisonResult result = new ComparisonResult(transactionReference);

        compareAndAddDifference(result, "Transaction Reference", transactionReference, pacs008.getCreditTransferTransactionInformation().getPaymentIdentification().getInstructionIdentification());
        compareAndAddDifference(result, "Value Date", new SimpleDateFormat("yyyy-MM-dd").format(mt103.getValueDate()), new SimpleDateFormat("yyyy-MM-dd").format(pacs008.getCreditTransferTransactionInformation().getInterbankSettlementDate()));
        compareAndAddDifference(result, "Amount", mt103.getAmount().toPlainString(), pacs008.getCreditTransferTransactionInformation().getInterbankSettlementAmount().getValue().toPlainString());
        compareAndAddDifference(result, "Currency", mt103.getCurrency(), pacs008.getCreditTransferTransactionInformation().getInterbankSettlementAmount().getCurrency());
        compareParty(result, "Ordering Customer", mt103.getOrderingCustomer(), pacs008.getCreditTransferTransactionInformation().getDebtor());
        compareParty(result, "Beneficiary Customer", mt103.getBeneficiaryCustomer(), pacs008.getCreditTransferTransactionInformation().getCreditor());

        String mtCharges = mt103.getDetailsOfCharges();
        String pacsCharges = pacs008.getCreditTransferTransactionInformation().getChargeBearer();
        if (mtCharges != null && pacsCharges != null && mtCharges.length() >= 3 && pacsCharges.length() >= 3) {
            compareAndAddDifference(result, "Details of Charges", mtCharges.substring(0, 3), pacsCharges.substring(0, 3));
        } else {
            compareAndAddDifference(result, "Details of Charges", mtCharges, pacsCharges);
        }

        return result;
    }

    private void compareParty(ComparisonResult result, String partyName, MT103.Party mtParty, Pacs008.Party pacsParty) {
        if (mtParty == null || pacsParty == null) {
            return;
        }

        if (mtParty.getBic() != null) {
            compareAndAddDifference(result, partyName + " BIC", mtParty.getBic(), pacsParty.getBic());
        } else if (mtParty.getNameAndAddress() != null) {
            String pacsNameAndAddress = pacsParty.getName();
            if (pacsParty.getAddress() != null) {
                pacsNameAndAddress += "\n" + pacsParty.getAddress();
            }
            compareAndAddDifference(result, partyName + " Name & Address", mtParty.getNameAndAddress(), pacsNameAndAddress);
        }
    }

    private void compareAndAddDifference(ComparisonResult result, String fieldName, String mtValue, String pacsValue) {
        if (!Objects.equals(mtValue, pacsValue)) {
            result.addDifference(fieldName, mtValue, pacsValue);
        }
    }
}
