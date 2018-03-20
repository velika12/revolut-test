import com.google.inject.AbstractModule;
import payments.*;
import repositories.AccountRepository;
import repositories.AccountRepositoryEbean;
import repositories.TransactionRepository;
import repositories.TransactionRepositoryEbean;

/** Guice module */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(AccountRepository.class).to(AccountRepositoryEbean.class).asEagerSingleton();
        bind(TransactionRepository.class).to(TransactionRepositoryEbean.class).asEagerSingleton();
        bind(PaymentService.class).to(PaymentServiceImpl.class);
    }

}
