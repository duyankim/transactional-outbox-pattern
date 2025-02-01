package com.github.account.domain;

import java.math.BigDecimal;

import lombok.NonNull;
import lombok.Value;

@Value
public class Money {

    public static Money ZERO = Money.of(BigDecimal.ZERO);

    @NonNull
    private final BigDecimal amount;

    public boolean isPositiveOrZero(){
        return this.amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isNegative(){
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isPositive(){
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Money money){
        return this.amount.compareTo(money.amount) >= 0;
    }

    public boolean isGreaterThan(Money money){
        return this.amount.compareTo(money.amount) >= 1;
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    public static Money add(Money a, Money b) {
        return new Money(a.amount.add(b.amount));
    }

    public Money minus(Money money){
        return new Money(this.amount.subtract(money.amount));
    }

    public Money plus(Money money){
        return new Money(this.amount.add(money.amount));
    }

    public static Money subtract(Money a, Money b) {
        return new Money(a.amount.subtract(b.amount));
    }

    public Money negate(){
        return new Money(this.amount.negate());
    }

}
