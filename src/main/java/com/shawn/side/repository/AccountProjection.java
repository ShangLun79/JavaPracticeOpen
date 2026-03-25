package com.shawn.side.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shawn.side.entity.Account;

public interface AccountProjection extends JpaRepository<Account, Long>{

    public interface AccountSummary {
        Long getId();
        String getAccountNumber();
        BigDecimal getBalance();        
    }

    List<AccountSummary> findByUserId(Long userId);


    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.balance >= :minBalance")
    List<AccountSummary> findSummaryByUserIdAndMinBalance(
        @Param("userId") Long userId,
        @Param("minBalance") BigDecimal minBalance
    );

}
