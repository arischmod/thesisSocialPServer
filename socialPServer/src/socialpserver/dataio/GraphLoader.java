package socialpserver.dataio;

import java.util.Set;

/**
 * Loads a graph.
 *
 * @author arix
 */
public interface GraphLoader {

    /**
     * load user Associations user-friendship links (edges)
     */
    public void loadGraph();

    /**
     * @return user Associations user-friendship links (edges)
     */
    public Set<String[]> getGraph();
}
