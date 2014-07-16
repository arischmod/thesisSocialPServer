/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import java.util.Map;

/** 
 * Loads all features available in pServer for the specific client 
 * @author arix
 */
public interface FeatureLoader {
        
    /**
     * Get the all Features available in pServer for the specific client    
     * @return a Map with all features and their default values     
     */
    public Map<String, Float> getFeatures();
    
    /**
     * load all Features available in pServer for the specific client             
     */
    public void loadFeatures();        
}
