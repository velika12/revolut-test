package repositories;

import payments.TransactionData;

import java.util.Optional;

/** Transaction repository interface */
public interface TransactionRepository {

    public Optional<TransactionData> createTransaction(TransactionData data);

}
