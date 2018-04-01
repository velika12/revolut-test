package repositories;

import com.avaje.ebean.Ebean;
import models.Account;
import models.Transaction;
import payments.TransactionData;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/** Ebean implementation of transaction repository */
@Singleton
public class TransactionRepositoryEbean implements TransactionRepository {

    @Inject
    public TransactionRepositoryEbean(EbeanConfig ebeanConfig) {}

    @Override
    public CompletionStage<Optional<TransactionData>> createTransaction(TransactionData data) {
        return supplyAsync(() -> {

            if (data.getSenderId().equals(data.getReceiverId())) {
                return Optional.empty();
            }

            Transaction transaction;

            Ebean.beginTransaction();

            try {
                // Find and lock records
                Account sender = Account.find.setForUpdate(true).where().eq("id", data.getSenderId()).findUnique();
                Account receiver = Account.find.setForUpdate(true).where().eq("id", data.getReceiverId()).findUnique();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (null == sender || null == receiver) {
                    Ebean.rollbackTransaction();
                    return Optional.empty();
                }

                if (sender.balance.compareTo(data.getAmount()) < 0) {
                    Ebean.rollbackTransaction();
                    return Optional.empty();
                }

                // Transfer money between accounts
                sender.balance = sender.balance.subtract(data.getAmount());
                receiver.balance = receiver.balance.add(data.getAmount());

                transaction = new Transaction(data.getAmount(), sender, receiver);

                transaction.save();
                sender.save();
                receiver.save();

                Ebean.commitTransaction();
            } finally {
                Ebean.endTransaction();
            }

            return Optional.of(new TransactionData(transaction));
        });
    }

}
