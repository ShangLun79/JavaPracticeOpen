package com.shawn.side.repository;



import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shawn.side.entity.Account;
import com.shawn.side.entity.Account.AccountStatus;
import com.shawn.side.entity.Account.AccountType;

import jakarta.transaction.Transactional;

public interface AccountQueryRepository extends JpaRepository<Account, Long> {

    List<Account> findByAccountTypeAndStatus(AccountType accountType, AccountStatus status);

    @Query("SELECT a FROM Account a WHERE a.accountType = :type AND a.status = :status")
    List<Account> findByTypeAndStatusJpql(
        @Param("type") AccountType type,
        @Param("status") AccountStatus status
    );

    // ------------
    List<Account> findByUserId(long userId);

    @Query("SELECT account FROM Account account WHERE account.user.id = :userId")
    List<Account> findByUserIdJpql(
        @Param("userId") Long userId
    );


    // -------------
    @Query("SELECT account.balance FROM Account account WHERE account.id = :id")
    Optional<BigDecimal> findBalanceById(
        @Param("id") Long accountId
    );

    // --------------
    long countByUserId(Long userId);

    @Query("SELECT SUM(account.balance) FROM Account account WHERE account.user.id = :userId")
    Optional<BigDecimal> sumBalanceByUserId(
        @Param("userId") Long userId
    );

    @Query(
        value = "SELECT * FROM accounts WHERE user_id = :userId AND status = 'ACTIVE'", 
        nativeQuery = true
    )
    List<Account> findActiveAccountsByUserIdNative(
        @Param("userId") Long userId
    );

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Account account SET account.status = :status WHERE account.user.id = :userId")
    int updateStatusByUserId(
        @Param("userId") Long userId,
        @Param("status") AccountStatus status
    );



}
