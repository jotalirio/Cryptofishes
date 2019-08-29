package com.oliveoyl.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.oliveoyl.api.CryptoFishyApi;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.webserver.services.WebServerPluginRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CryptoFishyPlugin implements WebServerPluginRegistry {
    private final List<Function<CordaRPCOps, ?>> webApis = ImmutableList.of(CryptoFishyApi::new);

    private final Map<String, String> staticServeDirs = ImmutableMap.of(
            "regulatoryBody", getClass().getClassLoader().getResource("regulatoryBodyWeb").toExternalForm(),
            "fishermanOne", getClass().getClassLoader().getResource("fishermanOneWeb").toExternalForm(),
            "fishermanTwo", getClass().getClassLoader().getResource("fishermanTwoWeb").toExternalForm(),
            "buyer", getClass().getClassLoader().getResource("buyerWeb").toExternalForm()
    );

    @Override @NotNull public List<Function<CordaRPCOps, ?>> getWebApis() { return webApis; }
    @Override @NotNull public Map<String, String> getStaticServeDirs() { return staticServeDirs; }
    @Override public void customizeJSONSerialization(ObjectMapper om) { }
}