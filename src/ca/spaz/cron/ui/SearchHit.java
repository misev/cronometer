package ca.spaz.cron.ui;

import ca.spaz.cron.datasource.FoodProxy;


public class SearchHit implements Comparable {
   private FoodProxy fp;
   private int score;
   
   public SearchHit(FoodProxy fp) {
      this.fp = fp;
   }
   
   /**
    * A heuristic scoring function to give a smart sort of the results
    * @param query the user's search terms, as entered
    */
   public void computeScore(String[] query) {
      score = 100; // base score, so we don't go negative
      
   // start off with big bonus for having a history of being used
      score += fp.getReferences() * 100; 
      
   // I hate how USDA is full of babyfoods. Come on, really.
      if (fp.getDescription().startsWith("Babyfood")) {
         score -= 100; 
      }
      
   // penalize longer strings. We're usually searching for short, simple foods.
      score -= getFoodProxy().getDescription().length();
      
   // add bonus for search terms being early in the description
      for (int i=0; i<query.length; i++) {
         score += 50 * (1.0 - (fp.getDescription().indexOf(query[i]) / (double)fp.getDescription().length()));
      }
      
   }
   
   public int getScore() { return score; }
   public FoodProxy getFoodProxy() { return fp; }

   public int compareTo(Object obj) {
      SearchHit hit = (SearchHit) obj;
      
      if (getScore() > hit.getScore()) return -1;
      if (getScore() < hit.getScore()) return 1;

      // break tie with name length
     //if (getFoodProxy().getDescription().length() < hit.getFoodProxy().getDescription().length()) return -1;
     //if (getFoodProxy().getDescription().length() > hit.getFoodProxy().getDescription().length()) return 1;
      
      // break tie with alphabetical order
      return getFoodProxy().getDescription().compareToIgnoreCase(hit.getFoodProxy().getDescription());      
   }
    
}
