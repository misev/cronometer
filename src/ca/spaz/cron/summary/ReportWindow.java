/*
 * Created on 28-Jan-2006
 */
package ca.spaz.cron.summary;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.datasource.Datasources;
import ca.spaz.cron.ui.DateChooser;
import ca.spaz.gui.*;
import ca.spaz.util.ImageFactory;
import ca.spaz.util.ToolBox;

import com.toedter.calendar.JDateChooser;

public class ReportWindow extends WrappedPanel { 
   private WebViewer htmlViewer;
   private JPanel toolbar;
   private JComboBox formatBox;
   private JCheckBox targetsOnlyBox;
   private JButton saveBtn;
   private NutritionSummaryPanel summary;
   private Vector formats;
   private String report;
   
   public ReportWindow(NutritionSummaryPanel summary) {
      this.summary = summary;
      setLayout(new BorderLayout());
      add(getToolbar(), BorderLayout.NORTH);
      add(getHTMLViewer(), BorderLayout.CENTER);
      generateReport();
   }
   
   private void generateReport() {
      boolean targetsOnly = getTargetsOnlyBox().isSelected();
 
      int numDays = 1;
      List servings = new ArrayList(); 
      servings.addAll(Datasources.getFoodHistory().getConsumedOn(getStartDate()));
      
      if (!ToolBox.isSameDay(getStartDate(), getEndDate())) {
         Date last = new Date(getStartDate().getTime());
         Calendar cal = new GregorianCalendar();
         cal.setTime(getStartDate());
         while (!cal.getTime().after(getEndDate())) {
            if (!ToolBox.isSameDay(cal.getTime(), last)) {
               last = cal.getTime();
               servings.addAll(Datasources.getFoodHistory().getConsumedOn(last));
               
               numDays++;
            }         
            cal.add(Calendar.HOUR, 12);         
         }
      }
            
      report = getFormat().export(servings, getStartDate(), getEndDate(), numDays, targetsOnly);
      if (getFormat() instanceof HTMLSummaryFormat) {         
         getHTMLViewer().setHTML(report);
      } else {
         getHTMLViewer().setText(report);
      }      
   }
   
   private Date getStartDate() {
      return startDate;
   }

   private Date getEndDate() {
      return endDate;
   }


   private Vector getFormats() {
      if (formats == null) {
         formats = new Vector();
         formats.add(new HTMLSummaryFormat());
         formats.add(new TEXTSummaryFormat());
      }
      return formats;
   }
   
   private JPanel getToolbar() {
      if (toolbar == null) {
         toolbar = new JPanel();
         toolbar.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

         toolbar.add(Box.createHorizontalGlue());
         toolbar.add(getStartDateButton()); 
         toolbar.add(new JLabel(" to ")); 
         toolbar.add(getEndDateButton());
         toolbar.add(Box.createHorizontalGlue());
         toolbar.add(getFormatBox());
         toolbar.add(Box.createHorizontalGlue());
         toolbar.add(getTargetsOnlyBox());
         toolbar.add(Box.createHorizontalGlue());
         toolbar.add(getSaveButton()); 
      }
      return toolbar;
   }

   private JDateChooser dateChooser;
   private JDateChooser getDatePicker() {
      if (dateChooser == null) {
         dateChooser = new JDateChooser();
      }
      return dateChooser;
   }
   

   private JButton startDateBtn;
   private Date startDate = new Date();
   private JButton endDateBtn;
   private Date endDate = new Date();
   private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
   
   private JButton getStartDateButton() {
      if (startDateBtn == null) {
         startDateBtn = new JButton(df.format(startDate));
         startDateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               startDate = pickDate(startDate);
               if (startDate.after(endDate)) {
                  endDate = startDate;
                  endDateBtn.setText(df.format(endDate));
               }
               startDateBtn.setText(df.format(startDate));
               generateReport();
            }
         });
         //startDateBtn.setEnabled(false);
      }
      return startDateBtn;
   }
   
   private JButton getEndDateButton() {
      if (endDateBtn == null) {
         endDateBtn = new JButton(df.format(endDate));
         endDateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               endDate = pickDate(endDate);
               if (endDate.before(startDate)) {
                  startDate = endDate;
                  startDateBtn.setText(df.format(startDate));
               }
               endDateBtn.setText(df.format(endDate));
               generateReport();
            }            
         });
      }
      return endDateBtn;
   }

   public Date pickDate(Date startDate) {
      return DateChooser.pickDate(this, startDate);
   }
  
   private JComboBox getFormatBox() {
      if (formatBox == null) {
         formatBox = new JComboBox(getFormats());
         formatBox.setPreferredSize(new Dimension(60, 12));
         formatBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.SELECTED) {
                  generateReport();
               }
            }
         });
      }
      return formatBox;
   } 
   
   private SummaryFormat getFormat() {
      return (SummaryFormat)getFormatBox().getSelectedItem();
   }
   
   private JCheckBox getTargetsOnlyBox() {
      if (targetsOnlyBox == null) {
         targetsOnlyBox = new JCheckBox("Targets Only", true);
         targetsOnlyBox.setToolTipText("Show only items with valid targets.");
         targetsOnlyBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.SELECTED ||
                   e.getStateChange() == ItemEvent.DESELECTED ) {
                  generateReport();
               }
            }
         });
      }
      return targetsOnlyBox;
   }
   
   private JButton getSaveButton() {
      if (null == saveBtn) {
         saveBtn = new JButton(new ImageIcon(ImageFactory.getInstance().loadImage("/img/Save24.gif")));
         saveBtn.setToolTipText("Save to File");       
         saveBtn.setBorderPainted(false);
         saveBtn.setRolloverEnabled(true);
         saveBtn.setMargin(new Insets(0,0,0,0));
         saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doSaveReport();
            }
         });
      }
      return saveBtn;
   }
   
   public WebViewer getHTMLViewer() {
      if (htmlViewer == null) {
         htmlViewer = new WebViewer();
         htmlViewer.setPreferredSize(new Dimension(450,450));
         htmlViewer.getHTMLPane().setFont(new Font("Courier",  Font.PLAIN, 12));
      }
      return htmlViewer;
   }
   
   public String getTitle() {
      return "Nutrition Report";
   }

   public String getSubtitle() {      
      return "Nutrition Report";
   }

   public String getInfoString() {
      return "Nutrition Report";
   }

   public ImageIcon getIcon() {
      return  new ImageIcon(ImageFactory.getInstance().loadImage("/img/apple-50x50.png"));
   }

   public boolean showSidebar() { 
      return false;
   }

   public boolean isCancellable() { 
      return false;
   }

   public void doCancel() {  }

   public void doAccept() {  }
 
   public void doSaveReport() {
      String xtn = ".txt";
      if (getFormat() instanceof HTMLSummaryFormat) {
         xtn = ".html";
      }   
      JFileChooser fd = new JFileChooser();
      fd.setSelectedFile(new File("report"+xtn));
      if (fd.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
         File f = fd.getSelectedFile();
         if (f != null) {
            try {               
               PrintStream ps = new PrintStream(
                     new BufferedOutputStream(new FileOutputStream(f)));
               ps.print(report);
               ps.close();
            } catch (IOException ie) {
               ErrorReporter.showError(ie, CRONOMETER.getInstance()); 
            }
         }
      }
   }
  
   
}