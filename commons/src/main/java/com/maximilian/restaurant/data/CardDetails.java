package com.maximilian.restaurant.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CardDetails {

    @NotNull
    @Pattern(regexp = "[0-9]{16}", message = "Invalid card number")
    private String cardNumber;
    @NotNull
    @Pattern(regexp = "[0-9]{3}", message = "Invalid CVV code")
    private String cvv;
    @NotNull
    @Pattern(regexp = "(([0][1-9])|([1][0-2]))/[0-9]{2}", message = "Invalid valid until date")
    private String validUntil;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public String toString() {
        return "CardDetails{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cvv='" + cvv + '\'' +
                ", validUntil='" + validUntil + '\'' +
                '}';
    }
}
