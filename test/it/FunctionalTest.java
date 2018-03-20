package it;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.routes;
import org.junit.Test;
import payments.AccountRepository;
import play.Application;
import play.Logger;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
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
        Long senderId = repository.create(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
        Long receiverId = repository.create(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot create a new account"));

        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 9999);
        JsonNode json = Json.parse(body);

        // Make a request for the transfer action
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyJson(json));

        // Check the response is ok
        assertThat(result.status(), equalTo(OK));

        // Check data in the database changed
        assertThat(repository.getBalance(senderId).orElse(null), equalTo(BigDecimal.valueOf(1)));
        assertThat(repository.getBalance(receiverId).orElse(null), equalTo(BigDecimal.valueOf(9999)));
    }

    @Test
    public void testTransferReturnsOkWhenSenderHasBalanceEqualToSentAmount() {
        // Create accounts
        AccountRepository repository = app.injector().instanceOf(AccountRepository.class);
        Long senderId = repository.create(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
        Long receiverId = repository.create(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot create a new account"));

        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 10000);
        JsonNode json = Json.parse(body);

        // Make a request for the transfer action
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyJson(json));

        // Check the response is ok
        assertThat(result.status(), equalTo(OK));

        // Check data in the database changed
        assertThat(repository.getBalance(senderId).orElse(null), equalTo(BigDecimal.ZERO));
        assertThat(repository.getBalance(receiverId).orElse(null), equalTo(BigDecimal.valueOf(10000)));
    }

    @Test
    public void testTransferReturnsBadRequestWhenRequestBodyIsInvalid() {
        // Request body
        String body = "{\"senderId\":null,\"receiverId\":null,\"amount\":null}";
        JsonNode json = Json.parse(body);

        // Make a request for transfer action
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyJson(json));

        // Check the response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenUsersDonNotExist() {
        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", 1, 2, 4000);
        JsonNode json = Json.parse(body);

        // Make a request for the transfer action
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyJson(json));

        // Check the response is bad
        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

    @Test
    public void testTransferReturnsBadRequestWhenSenderHasBalanceLessThanSentAmount() {
        // Create accounts
        AccountRepository repository = app.injector().instanceOf(AccountRepository.class);
        Long senderId = repository.create(BigDecimal.valueOf(10000)).orElseThrow(() -> new RuntimeException("Cannot create a new account"));
        Long receiverId = repository.create(BigDecimal.ZERO).orElseThrow(() -> new RuntimeException("Cannot create a new account"));

        // Request body
        String body = String.format("{\"senderId\":%d,\"receiverId\":%d,\"amount\":%d}", senderId, receiverId, 10001);
        JsonNode json = Json.parse(body);

        // Make a request for the transfer action
        Call call = routes.Payments.transfer();
        Result result = route(app, fakeRequest(call).bodyJson(json));

        assertThat(result.status(), equalTo(BAD_REQUEST));
    }

}
