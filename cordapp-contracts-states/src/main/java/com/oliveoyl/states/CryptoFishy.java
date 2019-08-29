package com.oliveoyl.states;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CryptoFishy implements LinearState {
    private final int year;
    private final Party owner;
    private final String type;
    private final String location;
    private final Integer quantity;
    private final boolean isFished;
    private final Party regulatoryBody;

    private final UniqueIdentifier linearId;

    public CryptoFishy(int year, Party owner, String type, String location, Integer quantity, boolean isFished, Party regulatoryBody) {
        this.year = year;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.quantity = quantity;
        this.isFished = isFished;
        this.regulatoryBody = regulatoryBody;
        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public CryptoFishy(int year, Party owner, String type, String location, Integer quantity, boolean isFished, Party regulatoryBody, UniqueIdentifier linearId) {
        this.year = year;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.quantity = quantity;
        this.isFished = isFished;
        this.regulatoryBody = regulatoryBody;
        this.linearId = linearId;
    }

    @NotNull
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(owner);
    }

    public CryptoFishy fish() {
        return new CryptoFishy(year, owner, type, location, quantity, true, regulatoryBody, linearId);
    }

    public CryptoFishy transfer(Party newOwner) {
        return new CryptoFishy(year, newOwner, type, location, quantity, isFished, regulatoryBody, linearId);
    }

    public int getYear() {
        return year;
    }
    public Party getOwner() {
        return owner;
    }
    public String getType() {
        return type;
    }
    public String getLocation() {
        return location;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public boolean isFished() {
        return isFished;
    }
    public Party getRegulatoryBody() {
        return regulatoryBody;
    }

    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoFishy that = (CryptoFishy) o;
        return year == that.year &&
                isFished == that.isFished &&
                owner.equals(that.owner) &&
                type.equals(that.type) &&
                location.equals(that.location) &&
                regulatoryBody.equals(that.regulatoryBody) &&
                linearId.equals(that.linearId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, owner, type, location, isFished, regulatoryBody, linearId);
    }
}
