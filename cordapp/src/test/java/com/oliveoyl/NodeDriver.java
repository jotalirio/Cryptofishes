package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import net.corda.testing.node.User;

import java.util.List;

import static net.corda.testing.driver.Driver.driver;

public class NodeDriver {
    public static void main(String[] args) {
        final User user = new User("user1", "test", ImmutableSet.of("ALL"));
        driver(new DriverParameters().withStartNodesInProcess(true).withWaitForAllNodesToFinish(true), dsl -> {
            List<String> names = ImmutableList.of("RegulatoryBody", "FishermanOne", "FishermanTwo", "Buyer");

            // We start the nodes in a for-loop to ensure they start in the same order and obtain the same webports
            // each time.
            for (String name : names) {
                CordaFuture<NodeHandle> handleFuture = dsl.startNode(new NodeParameters()
                        .withProvidedName(new CordaX500Name(name, "London", "GB"))
                        .withRpcUsers(ImmutableList.of(user)));
                try {
                    dsl.startWebserver(handleFuture.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        });
    }
}
