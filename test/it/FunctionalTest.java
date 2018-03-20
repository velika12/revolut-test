package it;

import controllers.routes;
import org.junit.Test;
import repositories.AccountRepository;
import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Call;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class FunctionalTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
            .configure((Map) Helpers.inMemoryDatabase())
            .in(Mode.TEST)
            .build();
    }

    @Test
    public void testTransferReturnsOkWhenSenderHasBalanceGreaterThanSentAmount() {
        // Create accounts
        AccountRepository repository = app.injector().instanceOf(AccountRepository.class);
        Long senderId = repository.createAccount(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
        Long receiverId = repository.createAccount(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot create a new account"));

        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 9999);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body));

        // Check that response is ok
        assertThat(result.status(), equalTo(OK));

        // Check the body of the response
        String expectedResultBody = String.format("{\"id\":%d,\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", 1, senderId, receiverId, 9999);
        assertThat(Helpers.contentAsString(result), equalTo(expectedResultBody));

        // Check data in the database changed
        assertThat(repository.getBalance(senderId).orElse(null), equalTo(BigDecimal.valueOf(1)));
        assertThat(repository.getBalance(receiverId).orElse(null), equalTo(BigDecimal.valueOf(9999)));
    }

    @Test
    public void testTransferReturnsOkWhenSenderHasBalanceEqualToSentAmount() {
        // Create accounts
        AccountRepository repository = app.injector().instanceOf(AccountRepository.class);
        Long senderId = repository.createAccount(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
        Long receiverId = repository.createAccount(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot create a new account"));

        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 10000);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body));

        // Check that response is ok
        assertThat(result.status(), equalTo(OK));

        // Check data in the database changed
        assertThat(repository.getBalance(senderId).orElse(null), equalTo(BigDecimal.ZERO));
        assertThat(repository.getBalance(receiverId).orElse(null), equalTo(BigDecimal.valueOf(10000)));
    }

    @Test
    public void testTransferReturnsBadRequestWhenRequestBodyIsInvalid() {
        // Request body
        String body = "{\"senderId\":null,\"receiverId\":null,\"amount\":null}";

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenUsersDonNotExist() {
        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", 1, 2, 4000);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenSenderHasBalanceLessThanSentAmount() {
        // Create accounts
        AccountRepository repository = app.injector().instanceOf(AccountRepository.class);
        Long senderId = repository.createAccount(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot createTransaction a new account"));
        Long receiverId = repository.createAccount(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot createTransaction a new account"));

        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 10001);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenRequestBodyJsonIsInvalid() {
        // Request body
        String body = "{:: Invalid json! }}}";

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

}
