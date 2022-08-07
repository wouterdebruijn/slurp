package nl.wouterdebruijn.slurp.entity;

public class SessionPlayerBody {
    public String session;
    public String username;

    public SessionPlayerBody(String session, String username) {
        this.session = session;
        this.username = username;
    }
}
