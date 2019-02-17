package tsedneva;

public class EmailTemplate {

    String email;
    String topic;
    String body;

    EmailTemplate (String email, String topic, String body)
    {
        this.email=email;
        this.topic=topic;
        this.body=body;
    }

    EmailTemplate ()
    {
        this.email="tsedneva@ya.ru";
        this.topic="test email";
        this.body="bla-bla-bla test email";
    }


}
