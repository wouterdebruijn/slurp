package nl.wouterdebruijn.slurp.infra;

public interface Observer {

    void update();

    void destroy();
}
