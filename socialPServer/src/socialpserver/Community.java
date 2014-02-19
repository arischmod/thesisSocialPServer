/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import socialpserver.dataio.FeatureLoader;
import socialpserver.dataio.UserFeatureLoader;

/**
 * Represents a Community of users it is a set of Users that belong in the same
 * Cluster
 *
 * @author arix
 */
public class Community {

    private Set<String> community;

    /**
     * Create a new Community object (a set of users that belong in the same
     * Cluster)
     */
    public Community() {
        this.community = new HashSet<>();
    }

    /**
     * Create a new Community object (a set of users that belong in the same
     * Cluster)
     *
     * @param userCommunity the users that will be contained in this Community
     * every user is a String with the userID
     */
    public Community(Set<String> userCommunity) {
        this.community = new HashSet<>();
        for (String user : userCommunity) {
            community.add(user);
        }
    }

    //   java Set handling
    /**
     * Get the members contained in this Community structure (Cluster)
     *
     * @return a Set of Stings(userIDs)
     */
    public Set<String> getCommunityMembers() {
        return community;
    }

    /**
     * add a member to this Community (Cluster)
     *
     * @param member is the userID
     */
    public void addMember(String member) {
        community.add(member);
    }

    /**
     * remove a member of this Community (Cluster)
     *
     * @param member is the userID
     */
    public void removeMember(String member) {
        community.remove(member);
    }

    /**
     * is the # of this Community members
     *
     * @return # of Users contained in this Community (Cluster)
     */
    public int size() {
        return community.size();
    }

    
    //  Evaluation methods
    /**
     * Calculate the intra cluster similarity to do this: find the cluster
     * centroid and sum the Cosine Similarity between centroid and all users
     *
     * @param fLoader loads all features available in pServer for the specific
     * client and also has the Location from where the features will be pulled
     * @param ufLoader loads the features that are rated by the users and also
     * has the Location from where the features will be pulled
     * @param communityID it is used to store centroids features so after the
     * Inter similarity can be calculated
     * @return clusters intraSimilarity
     */
    protected Float intraSimilarity(FeatureLoader fLoader, UserFeatureLoader ufLoader, int communityID, SetOfCommunities clusteringScheme) {
        Float communitySimilarity;

        Map<String, Map<String, Float>> communityUserFeatures = loadCommunityUserFeatures(ufLoader);
//        Map<String, Float> allFeatures = new HashMap<>(fLoader.getFeatures());
//        Map<String, Float> centroidFeatures = getCentroidFeatures(communityUserFeatures, allFeatures);
        Map<String, Float> centroidFeatures = getCentroidFeatures(communityUserFeatures);
        clusteringScheme.allCentroidFeatures.put(communityID, centroidFeatures);
        communitySimilarity = 0.0f;

        for (String user : community) {
            communitySimilarity = communitySimilarity + getCosineVectorSimilarity(communityUserFeatures.get(user), centroidFeatures);
            communityUserFeatures.remove(user);
        }
        Float averageIntraClusterSimilarity;
        if (!community.isEmpty())             
            averageIntraClusterSimilarity = communitySimilarity / community.size();
        else
            averageIntraClusterSimilarity = 0.0f;
        return averageIntraClusterSimilarity;
    }

    /**
     * Calculate centroid To do this get all pServer features and put the user
     * mean feature value as Value
     *
     * @param communityUserFeatures
     * @param centroidFeatureList
     * @return a Map with centroid features and their values
     */
    private Map<String, Float> getCentroidFeatures(Map<String, Map<String, Float>> communityUserFeatures) {
        // sum of feature values
        Map<String, Float> communityFeaturesUnion = new HashMap<>();
        for (String user : communityUserFeatures.keySet()) {
            Map<String, Float> userFeatures = communityUserFeatures.get(user);
            for (String feature : userFeatures.keySet()) {
                if (communityFeaturesUnion.get(feature) != null) {
                    communityFeaturesUnion.put(feature, communityFeaturesUnion.get(feature) + userFeatures.get(feature));
                } else {
                    communityFeaturesUnion.put(feature, userFeatures.get(feature));
                }
            }
        }

        // average of feature values
        for (String feature : communityFeaturesUnion.keySet()) {
//            if (centroidFeatureList.get(feature) > 0.0) {
            communityFeaturesUnion.put(feature, communityFeaturesUnion.get(feature) / community.size());
//            }
        }
        return communityFeaturesUnion;
    }

    /**
     * Load users feature List the features that user has rated and their rating
     * values
     *
     * @param ufLoader loads the features that are rated by the users and also
     * has the Location from where the features will be pulled
     * @return a Map with user features and their values
     */
    private Map<String, Map<String, Float>> loadCommunityUserFeatures(UserFeatureLoader ufLoader) {
        Map<String, Map<String, Float>> communityUserFeatures = new HashMap<>();
        for (String user : community) {
            communityUserFeatures.put(user, ufLoader.getUserFeatures(user));
        }
        return communityUserFeatures;
    }

    /**
     * Calculate Cosine Similarity of tow users by comparing their features
     *
     * @param user1Features
     * @param user2Features
     * @return the cosine similarity value (Float)
     */
    protected static Float getCosineVectorSimilarity(Map<String, Float> user1Features, Map<String, Float> user2Features) {
        Float sum = 0.0f, norm1 = 0.0f, norm2 = 0.0f;
        if (user1Features.isEmpty() || user2Features.isEmpty()) {
            return 0.0f;
        }

        for (String feature : user1Features.keySet()) {
            if (user2Features.containsKey(feature)) {
                sum = sum + user1Features.get(feature) * user2Features.get(feature);
            }
        }
     //   socialpserver.SocialPServer.algorithmOutputLogger.info("     sum = " + sum);
        for (String feature : user1Features.keySet()) {
            norm1 = norm1 +  (float) Math.pow(user1Features.get(feature), 2);
        }
     //   socialpserver.SocialPServer.algorithmOutputLogger.info("     norm1 = " + norm1);
        for (String feature : user2Features.keySet()) {
            norm2 = norm2 +  (float) Math.pow(user2Features.get(feature), 2);
        }
     //   socialpserver.SocialPServer.algorithmOutputLogger.info("     norm2 = " + norm2);
        Float sim;
        sim = sum /  (float) (Math.sqrt(norm1) * Math.sqrt(norm2));
        
     //   socialpserver.SocialPServer.algorithmOutputLogger.info("     cos sim = " + sim);
        if (norm1 == 0 || norm2 == 0) {
            sim = 0.0f;
        }
        return sim;
    }
}
