/*
 * Created on 28-Jan-2006
 */
package ca.spaz.cron.summary;

import java.util.*;

import ca.spaz.cron.foods.NutrientInfo;
import ca.spaz.cron.targets.Target;
import ca.spaz.cron.user.User;
import ca.spaz.util.StringUtil;

public class TEXTSummaryFormat extends SummaryFormat { 

   public String getFormatName() {
      return "Text";
   }
    
   public String export(List servings, Date start, Date end, int days, boolean targetsOnly) {
      StringBuffer sb = new StringBuffer();
      sb.append(StringUtil.charRun('=', 42));
      if (days > 1) {
         sb.append("\nNutrition Summary\n");
         sb.append(dateFormat.format(start) + " to "+ dateFormat.format(end) + "\n");
         sb.append("Daily Averages over " + days + " days\n");
      } else {
         sb.append("\nNutrition Summary for " + dateFormat.format(end) + "\n");
      }
      // add date & name
      sb.append(StringUtil.charRun('=', 42));
      sb.append('\n');
      sb.append('\n');
      //List NutrientInfo.getMacroNutrients();
      
      for (int i=0; i<NutrientInfo.CATEGORIES.length; i++) {
         sb.append(exportCategory(NutrientInfo.CATEGORIES[i], servings, days, targetsOnly));
      }
      
      return sb.toString();
   }
   
   public String exportCategory(String category, List servings, int days, boolean targetsOnly) {

      StringBuffer sb = new StringBuffer();

      sb.append(category);
      
      List nutrients = NutrientInfo.getCategory(category);
        
      double tcp = getTargetCompletion(servings, nutrients, days, false);
      if (!Double.isNaN(tcp)) {
         sb.append(" (");
         sb.append(nf.format(tcp));
         sb.append(")");
      }
      sb.append('\n');
      sb.append(StringUtil.charRun('=', 42));
      sb.append('\n');
      
      Iterator iter = nutrients.iterator();
      while (iter.hasNext()) { 
         NutrientInfo ni = (NutrientInfo)iter.next();
         sb.append(export(ni, servings, days, targetsOnly)); 
      }
      sb.append('\n');

      return sb.toString();
   }
   
   
   public String export(NutrientInfo ni, List servings, int days, boolean targetsOnly) {

      StringBuffer sb = new StringBuffer();
       
      double amount = getAmount(servings, ni) / (double)days;
       
      Target target = User.getUser().getTarget(ni);
      if (targetsOnly) {
         if (target.isUndefined() || !User.getUser().isTracking(ni)) {
            return sb.toString();
         }
      }
      String name = ni.getName();
      if (ni.getParent() != null) {
         name = "  " + name;
      }
      sb.append(StringUtil.padr(name, 21));
      sb.append('|');
      sb.append(StringUtil.padl(df.format(amount) + " ", 9));
      sb.append(StringUtil.padr(ni.getUnits(), 4));
      sb.append(' ');

      if (target.getMin() > 0) {
         sb.append(' ');
         sb.append(StringUtil.padl(nf.format(amount / target.getMin()), 5));
      }
      sb.append('\n');

      return sb.toString();
   }
   
}