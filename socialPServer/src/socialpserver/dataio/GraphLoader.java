package socialpserver.dataio;

import java.util.Set;

/**
 * Loads a graph.
 *
 * @author arix
 */
public interface GraphLoader {

    /**
     * load user Associations user-friendship links (edges) and keep them in RAM
     */
    public void loadGraph();

    /**
     * @return user Associations user-friendship links (edges)
     */
    public Set<String[]> getGraph();

     /**
      * Remove the Graph from memory (RAM)     
     */
    public void emptyGraph();

}
