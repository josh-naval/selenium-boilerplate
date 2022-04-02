package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {

    @FindBy(how = How.XPATH, using = "//input[@name='email']")
    public WebElement txtEmail;

    @FindBy(how = How.NAME, using = "password")
    public WebElement txtPassword;

    @FindBy(how = How.XPATH, using = "//button[.='Login']")
    public WebElement btnLogin;

    public LoginPage(WebDriver webDriver) {
        super.webDriver = webDriver;
        PageFactory.initElements(webDriver, this);
    }

}
