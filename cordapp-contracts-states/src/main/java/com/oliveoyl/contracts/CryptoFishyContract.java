package com.oliveoyl.contracts;

import com.google.common.collect.ImmutableList;
import com.oliveoyl.states.CryptoFishy;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CryptoFishyContract implements Contract {
    public static final String ID = "com.oliveoyl.contracts.CryptoFishyContract";

    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        isFishy(tx);
    }

    private void isFishy(LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<CryptoFishyCommands> command = requireSingleCommand(tx.getCommands(), CryptoFishyCommands.class);
        CryptoFishyCommands commandType = command.getValue();

        if (commandType instanceof CryptoFishyCommands.Issue) verifyIssue(tx, command);
        else if (commandType instanceof CryptoFishyCommands.Fish) verifyFish(tx, command);
        else if (commandType instanceof CryptoFishyCommands.Transfer) verifyTransfer(tx, command);
    }

    private void verifyTransfer(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A CryptoFishyTransfer transaction should only consume one input state.", tx.getInputs().size() == 1);
            require.using("A CryptoFishyTransfer transaction should only create one output state.", tx.getOutputs().size() == 1);

            final CryptoFishy in = tx.inputsOfType(CryptoFishy.class).get(0);
            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("Only the owner property may change in a CryptoFishyTransfer transaction.", out.equals(in.transfer(out.getOwner())));
            require.using("The owner property must change in a CryptoFishyTransfer transaction.", !(in.getOwner().equals(out.getOwner())));

            require.using("There must only be one signer (the current owner) in a CryptoFishyTransfer transaction.", command.getSigners().size() == 1);
            final Party currentOwner = in.getOwner();
            require.using("The current owner must be a signer in a CryptoFishyTransfer transaction.",
                    command.getSigners().containsAll(ImmutableList.of(currentOwner.getOwningKey())));

            return null;
        });
    }

    private void verifyFish(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException  {
        requireThat(require -> {
            require.using("A CryptoFishyFish transaction should only consume one input state.", tx.getInputs().size() == 1);
            require.using("A CryptoFishyFish transaction should only create one output state.", tx.getOutputs().size() == 1);

            final CryptoFishy in = tx.inputsOfType(CryptoFishy.class).get(0);
            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("Only the isFished property may change in a CryptoFishyFish transaction.", out.equals(in.fish()));
            require.using("The isFished property must change in a CryptoFishyFish transaction.", (in.isFished() != out.isFished()));

            require.using("There must only be one signer (the current owner) in a CryptoFishyFish transaction.", command.getSigners().size() == 1);
            final Party owner = in.getOwner();
            require.using("The current owner must be a signer in a CryptoFishyFish transaction.",
                    command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));

            return null;
        });
    }

    private void verifyIssue(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A CryptoFishyIssue transaction should consume no input states.", tx.getInputs().isEmpty());
            require.using("A CryptoFishyIssue transaction should only create one output state.", tx.getOutputs().size() == 1);

            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("The year property must be non-negative in a CryptoFishyIssue transaction.", out.getYear() > 0);
            require.using("The type property must not be blank in a CryptoFishyIssue transaction.", !out.getType().isEmpty());
            require.using("The location property must not be blank in a CryptoFishyIssue transaction.", !out.getLocation().isEmpty());
            require.using("The isFished property must not be false in a CryptoFishyIssue transaction.", !out.isFished());

            require.using("There must only be one signer (the regulatory body) in a CryptoFishyIssue transaction.", command.getSigners().size() == 1);
            final Party regulatoryBody = out.getRegulatoryBody();
            require.using("The regulatory body must be a signer in a CryptoFishyIssue transaction.",
                    command.getSigners().containsAll(ImmutableList.of(regulatoryBody.getOwningKey())));

            return null;
        });
    }
}
