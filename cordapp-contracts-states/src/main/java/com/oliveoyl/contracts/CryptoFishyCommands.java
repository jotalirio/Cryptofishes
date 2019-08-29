package com.oliveoyl.contracts;

import net.corda.core.contracts.CommandData;

public interface CryptoFishyCommands extends CommandData {
    class Issue implements CryptoFishyCommands {}
    class Transfer implements CryptoFishyCommands {}
    class Fish implements CryptoFishyCommands {}
}