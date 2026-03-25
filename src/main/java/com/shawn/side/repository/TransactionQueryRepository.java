package com.shawn.side.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shawn.side.entity.Transaction;

public interface TransactionQueryRepository 
    extends JpaRepository<Transaction, Long>
{

    @Query("SELECT t FROM Transaction t JOIN FETCH t.toAccount")
    List<Transaction> findAllWithToAccountJoinFetch();


    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.toAccount LEFT JOIN FETCH t.fromAccount")
    List<Transaction> findAllWithBothAccountsJoinFetch();


    @EntityGraph(attributePaths = {"toAccount"})
    List<Transaction> findAll();

    @EntityGraph(attributePaths = {"toAccount", "fromAccount"})
    List<Transaction> findByToAccount_Id(Long accountId);
}
