package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import com.oliveoyl.flows.FishCryptoFishyFlow;
import com.oliveoyl.flows.IssueCryptoFishyFlow;
import com.oliveoyl.flows.TransferCryptoFishyFlow;
import com.oliveoyl.states.CryptoFishy;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.StartedMockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FlowTests {
    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;
    private Party partyA;
    private Party partyB;
    private final String TYPE = "albacore";
    private final String LOCATION = "manilla";
    private final Integer QUANTITY = 100;

    @Before
    public void setup() {
        network = new MockNetwork(ImmutableList.of("com.oliveoyl"));
        a = network.createNode();
        b = network.createNode();
        partyA = a.getInfo().getLegalIdentities().get(0);
        partyB = b.getInfo().getLegalIdentities().get(0);
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void issue() throws Exception {
        LedgerTransaction tx = issue(a, TYPE, LOCATION);

        assertEquals(0, tx.getInputs().size());
        assertEquals(1, tx.getOutputs().size());

        List<CryptoFishy> fishies = tx.outputsOfType(CryptoFishy.class);
        assertEquals(1, fishies.size());

        CryptoFishy fishy = fishies.get(0);
        CryptoFishy expectedFishy = new CryptoFishy(
                Calendar.getInstance().get(Calendar.YEAR),
                partyA, TYPE, LOCATION, QUANTITY, false, partyA,
                fishy.getLinearId());
        assertEquals(expectedFishy, fishy);
    }

    @Test
    public void fish() throws Exception {
        LedgerTransaction issueTx = issue(a, TYPE, LOCATION);
        UniqueIdentifier linearId = issueTx.outputsOfType(CryptoFishy.class).get(0).getLinearId();
        LedgerTransaction fishTx = fish(a, linearId);

        assertEquals(1, fishTx.getInputs().size());
        assertEquals(1, fishTx.getOutputs().size());

        List<CryptoFishy> inputFishies = fishTx.inputsOfType(CryptoFishy.class);
        assertEquals(1, inputFishies.size());
        List<CryptoFishy> outputFishies = fishTx.outputsOfType(CryptoFishy.class);
        assertEquals(1, outputFishies.size());

        CryptoFishy inputFishy = inputFishies.get(0);
        CryptoFishy outputFishy = outputFishies.get(0);
        CryptoFishy expectedInputFishy = new CryptoFishy(
                Calendar.getInstance().get(Calendar.YEAR),
                partyA, TYPE, LOCATION, QUANTITY, false, partyA,
                inputFishy.getLinearId());
        assertEquals(expectedInputFishy, inputFishy);
        assertEquals(expectedInputFishy.fish(), outputFishy);
    }

    @Test
    public void transfer() throws Exception {
        LedgerTransaction issueTx = issue(a, TYPE, LOCATION);
        UniqueIdentifier linearId = issueTx.outputsOfType(CryptoFishy.class).get(0).getLinearId();
        fish(a, linearId);
        LedgerTransaction transferTx = transfer(a, linearId, partyB);

        assertEquals(1, transferTx.getInputs().size());
        assertEquals(1, transferTx.getOutputs().size());

        List<CryptoFishy> inputFishies = transferTx.inputsOfType(CryptoFishy.class);
        assertEquals(1, inputFishies.size());
        List<CryptoFishy> outputFishies = transferTx.outputsOfType(CryptoFishy.class);
        assertEquals(1, outputFishies.size());

        CryptoFishy inputFishy = inputFishies.get(0);
        CryptoFishy outputFishy = outputFishies.get(0);
        CryptoFishy expectedInputFishy = new CryptoFishy(
                Calendar.getInstance().get(Calendar.YEAR),
                partyA, TYPE, LOCATION, QUANTITY, true, partyA,
                inputFishy.getLinearId());
        assertEquals(expectedInputFishy, inputFishy);
        assertEquals(expectedInputFishy.transfer(partyB), outputFishy);
    }

    private LedgerTransaction issue(StartedMockNode node, String type, String location) throws Exception {
        IssueCryptoFishyFlow flow = new IssueCryptoFishyFlow(partyA, type, location, QUANTITY);
        CordaFuture<SignedTransaction> future = node.startFlow(flow);
        network.runNetwork();
        SignedTransaction stx = future.get();
        return stx.toLedgerTransaction(node.getServices());
    }

    private LedgerTransaction fish(StartedMockNode node, UniqueIdentifier linearId) throws Exception {
        FishCryptoFishyFlow flow = new FishCryptoFishyFlow(linearId);
        CordaFuture<SignedTransaction> future = node.startFlow(flow);
        network.runNetwork();
        SignedTransaction stx = future.get();
        return stx.toLedgerTransaction(node.getServices());
    }

    private LedgerTransaction transfer(StartedMockNode node, UniqueIdentifier linearId, Party newOwner) throws Exception {
        TransferCryptoFishyFlow flow = new TransferCryptoFishyFlow(linearId, newOwner);
        CordaFuture<SignedTransaction> future = node.startFlow(flow);
        network.runNetwork();
        SignedTransaction stx = future.get();
        return stx.toLedgerTransaction(node.getServices());
    }
}
