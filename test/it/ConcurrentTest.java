package it;

import controllers.routes;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Logger;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.F;
import play.mvc.Call;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import repositories.AccountRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

public class ConcurrentTest extends WithApplication {

    private Long senderId;
    private Long receiverId;

    private AccountRepository accountRepository;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
            .configure((Map) Helpers.inMemoryDatabase())
            .in(Mode.TEST)
            .build();
    }

    @Before
    public void beforeEachTest() {
        // Create accounts
        this.accountRepository = app.injector().instanceOf(AccountRepository.class);
        this.senderId = accountRepository.createAccount(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
        this.receiverId = accountRepository.createAccount(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
    }

    @Test
    public void testDoubleSpendingIsPrevented() {
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 9999);

        // Send two simultaneous requests
        Call call = routes.Payments.transfer();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Callable<F.Tuple<String,Result>>> callables = Arrays.asList(
            () -> {
                String threadName = Thread.currentThread().getName();
                Logger.info("Started: {}", threadName);
                Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));
                Logger.info("Done: {}", threadName);
                return new F.Tuple<>(threadName, result);
            },
            () -> {
                String threadName = Thread.currentThread().getName();
                Logger.info("Started: {}", threadName);
                Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));
                Logger.info("Done: {}", threadName);
                return new F.Tuple<>(threadName, result);
            }
        );

        try {
            executor.invokeAll(callables).stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(pair -> Logger.info("Status for {}: {}", pair._1, pair._2.status()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check data in the database changed
        assertThat(accountRepository.getBalance(senderId).orElse(null), equalTo(BigDecimal.valueOf(1)));
        assertThat(accountRepository.getBalance(receiverId).orElse(null), equalTo(BigDecimal.valueOf(9999)));
    }

}
