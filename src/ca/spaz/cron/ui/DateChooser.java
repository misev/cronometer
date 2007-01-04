/*
 * Created on Mar 11, 2006 by davidson
 */
package ca.spaz.cron.ui;

import java.util.Date;

import javax.swing.*;

import ca.spaz.gui.*;

import com.toedter.calendar.JCalendar;

public class DateChooser extends WrappedPanel {

   private JCalendar cal;
   
   public DateChooser(Date curDate) {
      cal = new JCalendar(curDate);
       
      /*cal.addPropertyChangeListener(new PropertyChangeListener() {

         public void propertyChange(PropertyChangeEvent evt) {
            System.out.println(evt.getPropertyName() + "|" + evt.getNewValue());
         }
         
      });*/
      
      add(cal);
   }
      
   public static Date pickDate(JComponent parent, Date d) {
      DateChooser dc = new DateChooser(d);      
      WrapperDialog.showDialog(parent, dc);
      return dc.cal.getDate(); 
   }

   public String getTitle() {
      return "Choose Date";
   }

   public String getSubtitle() {
      return "Pick a Date to view";
   }

   public String getInfoString() {
      return "Choose Date";
   }

   public ImageIcon getIcon() {
      return null;
   }


   public boolean showSidebar() { 
      return false;
   }
   
   public boolean isCancellable() {
      return false;
   }

   public void doCancel() {
   }

   public void doAccept() {
   }
   
   
}