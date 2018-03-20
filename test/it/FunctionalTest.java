package it;

import controllers.routes;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
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
    public void testTransferReturnsOkWhenSenderHasBalanceGreaterThanSentAmount() {
        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 9999);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));

        // Check that response is ok
        assertThat(result.status(), equalTo(OK));

        // Check the body of the response
        String expectedResultBody = String.format("{\"id\":%d,\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", 1, senderId, receiverId, 9999);
        assertThat(Helpers.contentAsString(result), equalTo(expectedResultBody));

        // Check data in the database changed
        assertThat(accountRepository.getBalance(senderId).orElse(null), equalTo(BigDecimal.valueOf(1)));
        assertThat(accountRepository.getBalance(receiverId).orElse(null), equalTo(BigDecimal.valueOf(9999)));
    }

    @Test
    public void testTransferReturnsOkWhenSenderHasBalanceEqualToSentAmount() {
        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 10000);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));

        // Check that response is ok
        assertThat(result.status(), equalTo(OK));

        // Check data in the database changed
        assertThat(accountRepository.getBalance(senderId).orElse(null), equalTo(BigDecimal.ZERO));
        assertThat(accountRepository.getBalance(receiverId).orElse(null), equalTo(BigDecimal.valueOf(10000)));
    }

    @Test
    public void testTransferReturnsBadRequestWhenRequestBodyIsInvalid() {
        // Request body
        String body = "{\"senderId\":null,\"receiverId\":null,\"amount\":null}";

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenRequestBodyContainsNegativeAmount() {
        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, -1000);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenUsersDoNotExist() {
        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", 111, 222, 4000);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenSenderHasBalanceLessThanSentAmount() {
        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 10001);

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenRequestBodyJsonIsInvalid() {
        // Request body
        String body = "{:: Invalid json! }}}";

        // Send request
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyText(body).header("Content-Type", "application/json"));

        // Check that response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

}
