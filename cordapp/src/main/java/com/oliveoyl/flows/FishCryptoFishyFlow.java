package com.oliveoyl.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.oliveoyl.contracts.CryptoFishyCommands;
import com.oliveoyl.contracts.CryptoFishyContract;
import com.oliveoyl.flows.VerifySignAndFinaliseFlow;
import com.oliveoyl.states.CryptoFishy;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

@InitiatingFlow
@StartableByRPC
public class FishCryptoFishyFlow extends FlowLogic<SignedTransaction> {
    private final UniqueIdentifier linearId;

    public FishCryptoFishyFlow(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        StateAndRef<CryptoFishy> inputStateAndRef = getServiceHub().getVaultService().queryBy(CryptoFishy.class, queryCriteria).getStates().get(0);

        CryptoFishy outputFishy = inputStateAndRef.getState().getData().fish();

        TransactionBuilder builder = new TransactionBuilder(inputStateAndRef.getState().getNotary())
                .addInputState(inputStateAndRef)
                .addOutputState(outputFishy, CryptoFishyContract.ID)
                .addCommand(new CryptoFishyCommands.Fish(), getOurIdentity().getOwningKey());

        return subFlow(new VerifySignAndFinaliseFlow(builder));
    }
}
