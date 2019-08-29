package com.oliveoyl.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.oliveoyl.contracts.CryptoFishyCommands.Issue;
import com.oliveoyl.contracts.CryptoFishyContract;
import com.oliveoyl.flows.VerifySignAndFinaliseFlow;
import com.oliveoyl.states.CryptoFishy;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.LocalDate;

@InitiatingFlow
@StartableByRPC
public class IssueCryptoFishyFlow extends FlowLogic<SignedTransaction> {
    private final Party owner;
    private final String type;
    private final String location;
    private final Integer quantity;

    public IssueCryptoFishyFlow(Party owner, String type, String location, Integer quantity) {
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.quantity = quantity;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        LocalDate date = LocalDate.now();
        int year = date.getYear();
        CryptoFishy cryptoFishy = new CryptoFishy(year, owner, type, location, quantity, false, getOurIdentity());

        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(cryptoFishy, CryptoFishyContract.ID)
                .addCommand(new Issue(), getOurIdentity().getOwningKey());

        return subFlow(new VerifySignAndFinaliseFlow(builder));
    }
}
