package tsedneva;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Driver;
import java.util.concurrent.TimeUnit;

/** Email utils operations.  */
final class Utils {

    /** Logins to the email. */
    static void login(WebDriver driver, String log, String pass) {
        WebElement enterButton = driver.findElement(By.cssSelector("a.HeadBanner-Button-Enter"));
        enterButton.click();

        // A/B testing detection
        WebElement loginField;
        WebElement passwordField;
        WebElement loginButton;

        try {
            loginField = driver.findElement(By.cssSelector("input.textinput__control[name=\"login\"]"));
            loginField.sendKeys(log);

            loginButton = driver.findElement(By.cssSelector("button.passp-form-button"));
            loginButton.click();

            //next page
            passwordField = driver.findElement(By.cssSelector("#passp-field-passwd"));
            passwordField.sendKeys(pass);
            loginButton = driver.findElement(By.cssSelector("button.passp-form-button"));
            loginButton.click();

        } catch (NoSuchElementException e) {

            loginField = driver.findElement(By.cssSelector("input.passport-Input-Controller[name=\"login\"]"));
            loginField.sendKeys(log);
            passwordField = driver.findElement(By.cssSelector("input.passport-Input-Controller[name=\"passwd\"]"));
            passwordField.sendKeys(pass);

            loginButton = driver.findElement(By.cssSelector("button.passport-Button[type=\"submit\"]"));
            loginButton.click();
        }
    }
    static void logout(WebDriver driver, String logoutStr){
        driver.navigate().to(logoutStr);
        driver.navigate().to("https://mail.yandex.ru");
    }
    /** Sends email. */
    static void sendEmail(WebDriver driver, EmailTemplate myEmail) {
        createEmail(driver, myEmail);

        WebElement sendButton = driver.findElement(By.cssSelector(".mail-Compose-ComplexSendButton > button"));
        sendButton.click();

        (new WebDriverWait(driver, 25)).until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child")));

    }

    static WebDriver createWindow() {
        System.setProperty("webdriver.chrome.driver", "/Users/tsedneva/driver/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get ("https://mail.yandex.ru");

        return driver;
    }

    static void closeWindow(WebDriver driver) {
        driver.quit();
    }

    /** Sends email to myself. */
    static void sendEmailToMyself(WebDriver driver) {
        WebElement writeButton = driver.findElement(By.cssSelector("a.mail-ComposeButton"));
        writeButton.click();

        WebElement addressMyself = driver.findElement(By.cssSelector("span.mail-Compose-Field-Caption"));
        addressMyself.click();

        WebElement emailTheme = driver.findElement(By.cssSelector("input.mail-Compose-Field-Input-Controller"));
        emailTheme.sendKeys("test first email");

        WebElement emailText = driver.findElement(By.cssSelector(".cke_wysiwyg_div"));
        emailText.sendKeys("this is my first test email");
        WebElement sendButton = driver.findElement(By.cssSelector(".mail-Compose-ComplexSendButton > button"));
        sendButton.click();
    }

    /** Sends email to the wrong address. */
    static void wrongEmail(WebDriver driver, String address) {
        WebElement writeButton = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.mail-ComposeButton")));

        writeButton.click();

        // TODO:
        //createEmail(driver, new EmailTemplate());

        WebElement emailTo = driver.findElement(By.cssSelector(".js-compose-field[name=\"to\"]"));
        emailTo.sendKeys(address);

        WebElement sendButton = driver.findElement(By.cssSelector(".mail-Compose-ComplexSendButton > button"));

        sendButton.click();
        assertNotNull(driver.findElement(By.cssSelector(".ns-view-compose-field-to-error")));
    }

    /** Creates email */
    static void createEmail(WebDriver driver, EmailTemplate email) {
        // async load component
        WebElement writeButton = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.mail-ComposeButton")));
        writeButton.click();
        WebElement emailTo = driver.findElement(By.cssSelector(".js-compose-field[name=\"to\"]"));
        emailTo.sendKeys(email.email);

        WebElement emailTheme = driver.findElement(By.cssSelector("input.mail-Compose-Field-Input-Controller"));
        emailTheme.sendKeys(email.topic);

        WebElement emailText = driver.findElement(By.cssSelector(".cke_wysiwyg_div"));
        emailText.sendKeys(email.body);
    }

    /** Checks drafts email. */
    static void checkDrafts(WebDriver driver, EmailTemplate email) {
        email.body = "Tanya test template 11";
        createEmail(driver, email);

        WebElement emailTrashFolder = driver.findElement(By.cssSelector("a.ns-view-folder[href=\"#trash\"]"));
        emailTrashFolder.click();
        WebElement emailModalSaveButton = driver.findElement(By.cssSelector("button.nb-button[data-action=\"save\"]"));
        emailModalSaveButton.click();

        WebElement emailDraftFolder = driver.findElement(By.cssSelector("a.ns-view-folder[href=\"#draft\"]"));
        emailDraftFolder.click();

        //open the first element in list
        WebElement openEmail = driver.findElement(
                By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child"));
        openEmail.click();

        WebElement emailTopic = driver.findElement(By.cssSelector("input.mail-Compose-Field-Input-Controller"));
        String thisTopic = emailTopic.getAttribute("value");
        WebElement emailText = driver.findElement(By.cssSelector(".cke_wysiwyg_div"));
        String thisText = emailText.getText();

        assertEquals(email.topic, thisTopic);
        assertEquals(email.body, thisText);
    }

    /** Gets and validates email */
    static void receiveEmail(WebDriver driver, EmailTemplate originalEmail) {

        WebElement myInbox = driver.findElement(By.cssSelector("a.ns-view-folder[href=\"#inbox\"]"));
        myInbox.click();
        driver.navigate().to(driver.getCurrentUrl());

        WebElement openEmail = driver.findElement(
                By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child"));
        openEmail.click();

        WebElement emailTopic = driver.findElement(By.cssSelector(".mail-Message-Toolbar-Subject"));
        String receivedTopic = emailTopic.getText();

        WebElement emailText = driver.findElement(By.cssSelector(".mail-Message-Body-Content"));
        String receivedText = emailText.getText();

        assertEquals(originalEmail.topic, receivedTopic);
        assertEquals(originalEmail.body, receivedText);
    }

    /** Sends reply email */
    static void reply(WebDriver driver, String answer) {
        WebElement replyEl = driver.findElement(By.cssSelector(".js-quick-reply-placeholder-single-reply"));
        replyEl.click();

        WebElement gotoTextField = driver.findElement(By.cssSelector(".ns-view-compose-go-to-compose"));
        gotoTextField.click();
        WebElement emailText = driver.findElement(By.cssSelector(".cke_wysiwyg_div"));
        emailText.sendKeys(answer);
        WebElement sendButton = driver.findElement(By.cssSelector(".mail-Compose-ComplexSendButton > button"));
        sendButton.click();

        WebElement doneMessage = driver.findElement(By.cssSelector(".mail-Done-Title"));

        assertEquals(doneMessage.getText(), "Письмо отправлено.");
    }

    /** Deletes email. */
    static void deleteEmail(WebDriver driver) {

        (new WebDriverWait(driver, 25)).until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".js-quick-reply-placeholder-single-reply")));

        WebElement deleteButton = (new WebDriverWait(driver, 25)).until(
            ExpectedConditions.elementToBeClickable(By.cssSelector(".mail-Toolbar-Item_delete")));

        deleteButton.click();

        WebElement openEmail = driver.findElement(
                By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child"));
        openEmail.click();
    }

    private Utils() {}
}