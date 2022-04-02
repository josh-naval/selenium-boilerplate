import org.junit.jupiter.api.Test;
import pagesteps.LoginPageSteps;

public class SampleTestScript extends BaseTest {

    @Test
    public void sample_test_method() {
        LoginPageSteps user = new LoginPageSteps(webDriver);
        user.open_php_travels_page();
        user.login();
    }
}
