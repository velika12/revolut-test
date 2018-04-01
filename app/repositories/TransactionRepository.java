package repositories;

import payments.TransactionData;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TransactionRepository {

    public CompletionStage<Optional<TransactionData>> createTransaction(TransactionData data);

}
