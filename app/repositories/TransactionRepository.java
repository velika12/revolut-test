package repositories;

import payments.TransactionData;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/** Transaction repository interface */
public interface TransactionRepository {

    public CompletionStage<Optional<TransactionData>> createTransaction(TransactionData data);

}
