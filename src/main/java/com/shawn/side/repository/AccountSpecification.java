package com.shawn.side.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.shawn.side.entity.Account;
import com.shawn.side.entity.Account.AccountStatus;
import com.shawn.side.entity.Account.AccountType;

public class AccountSpecification {

    public static Specification<Account> hasAccountType(AccountType type) {
        return (root, query, cb) -> {
            if (type == null) {
                return null;
            }
            return cb.equal(root.get("accountType"), type);
        };
    }

    public static Specification<Account> hasStatus(AccountStatus status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);

    }

    public static Specification<Account> hasMinBalance(BigDecimal minBalance) {
        return (root, query, cb) -> 
            minBalance == null ? null : cb.greaterThanOrEqualTo(root.get("balance"), minBalance);
    }

    public static Specification<Account> belongUser(Long userId) {
        return (root, query, cb) -> 
            userId == null ? null : cb.equal(
                root.get("user").get("id"), 
                userId
            );
    }
}
