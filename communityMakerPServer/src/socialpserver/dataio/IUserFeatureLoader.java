/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import java.util.Map;

/**
 * Loads all features that are rated by the users
 * @author arix
 */
public interface IUserFeatureLoader {
    
    /**
     * Get the rated features of the specific user
     * @param user userID
     * @return a Map with all rated by the user features and their values
     * for the specific user
     */
    public Map<String, Float> getUserFeatures(String user);
    
}
