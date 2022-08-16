package utilities.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;

public final class SeleniumActions {
    public static WebDriver webDriver;

    public static Actions initializeActions() {
        return new Actions(webDriver);
    }

    public static Wait<WebDriver> initializeFluentWait() {
        return new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);
    }

    public static Wait<WebDriver> initializeWait() {
        return new WebDriverWait(webDriver, Duration.ofSeconds(20));
    }

    public static WebElement findElement(By locator) {
        return initializeWait().until(webDriver -> webDriver.findElement(locator));
    }

    public static <T extends WebElement> WebElement waitForVisibilityOf(T element) {
        return initializeWait().until(ExpectedConditions.visibilityOf(element));
    }

    public static <T extends WebElement> WebElement waitToBeClickable(T element) {
        return initializeFluentWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    public static <T extends WebElement> void click(final T element) {
        element.click();
    }

    public static <T extends WebElement> void doubleClick(final T element) {
        Actions actions = initializeActions();
        actions.doubleClick(element).build().perform();
    }

    public static <T extends WebElement> void type(final T element, String value) {
        element.sendKeys(value);
    }

    public static <T extends WebElement> void moveToElement(final T element) {
        Actions actions = initializeActions();
        actions.moveToElement(element).build().perform();
    }

    public static <T extends WebElement> void scrollIntoView(final T element) {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].scrollIntoView(true);");
    }
}
