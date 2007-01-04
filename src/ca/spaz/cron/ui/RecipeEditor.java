/*
 * Created on 24-Nov-2005
 */
package ca.spaz.cron.ui;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.*;

import ca.spaz.cron.CRONOMETER;
import ca.spaz.cron.foods.*;
import ca.spaz.util.ImageFactory;

public class RecipeEditor extends FoodEditor {
 
   private Recipe recipe;
   private ServingTable servingTable;
   private JButton addBtn, delBtn, printBtn;
   private JLabel gramsLabel;
   private JPanel servingPanel; 
   private JToolBar toolBar;
   
   public RecipeEditor(CRONOMETER app, Recipe r) {
      super(app, r);
      this.recipe = r;

      getServingTable().setServings(r.getServings());      
   }
   
   protected String getTitle() {
      return "Recipe Editor";
   }
   
   protected void initialize() {
      this.setLayout(new BorderLayout(4, 4));
      this.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
      this.add(getGeneralPanel(), BorderLayout.NORTH);
      this.add(getCenterPanel(), BorderLayout.CENTER);
      this.add(getEastPanel(), BorderLayout.EAST);
      
      getMacroNutrientsTable().setEditable(false);
      getMineralsTable().setEditable(false);
      getVitaminsTable().setEditable(false);
      getAminoAcidsTable().setEditable(false);
      getLipidsTable().setEditable(false);
   }
   
   private void updateNutrients() { 
      getMeasureSelector().updateMeasure();

      getMacroNutrientsTable().setFood(recipe);
      getMineralsTable().setFood(recipe);
      getVitaminsTable().setFood(recipe);
      getAminoAcidsTable().setFood(recipe);
      getLipidsTable().setFood(recipe);
      getGramsLabel().setText("Recipe Weight: "+
            Math.round(recipe.getTotalGrams()*10)/10.0+"g");
   }
   
   private JLabel getGramsLabel() {
      if (gramsLabel == null) {
         gramsLabel = new JLabel();
      }
      return gramsLabel;
   }

   private JTabbedPane getEastPanel() {
      JTabbedPane jp = new JTabbedPane();
      jp.add("Recipe", getServingPanel());
      jp.add("Nutrients", getNutrientPanel());
      return jp;
   }
   
   public ServingTable getServingTable() {
      if (null == servingTable) {
         servingTable = new ServingTable();
         servingTable.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
               recipe.setServings(servingTable.getServings());
               getMeasureEditor().resetMeasures();
               getDeleteButton().setEnabled(!servingTable.getSelectedServings().isEmpty());
               updateNutrients(); 
            }           
         });   
         servingTable.addServingSelectionListener(new ServingSelectionListener() {
            public void servingSelected(Serving food) {
               getDeleteButton().setEnabled(!servingTable.getSelectedServings().isEmpty());
            }
            public void servingDoubleClicked(Serving food) { }            
         });
      }
      return servingTable;
   }
   
   private JToolBar getToolBar() {
      if (null == toolBar) {
          toolBar = new JToolBar();
          toolBar.setRollover(true);
          toolBar.setOrientation(JToolBar.HORIZONTAL);
          toolBar.setFloatable(false);
          toolBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
          toolBar.add(getAddButton());
          toolBar.add(getDeleteButton());
          toolBar.add(Box.createHorizontalStrut(20));
          toolBar.add(getPrintButton());
          toolBar.add(Box.createGlue());
          toolBar.add(getGramsLabel());
      }
      return toolBar;
  }
   

   public void doPrint() {
      try {         
         MessageFormat headerFormat = new MessageFormat("Recipe '"+recipe.getDescription()+"'");
         MessageFormat footerFormat = new MessageFormat("- {0} -");
         getServingTable().getTable().print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat);          
      } catch (PrinterException e) {
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, e.getMessage());
      }
   }
   
   
  /* public void doSave() {
      recipe.setDescription(getNameField().getText());     
      recipe.setComment(getCommentEditor().getText());
      recipe.setMeasures(getMeasureEditor().getMeasures());     
      if (recipe.getSource() != Datasources.getUserFoods()) {
         Datasources.getUserFoods().addFood(recipe);
      } else {
         recipe.update();
      }
      getDialog().dispose();
   }*/

   private JPanel getServingPanel() {
      if (null == servingPanel) {
         servingPanel = new JPanel(new BorderLayout(4,4));
         servingPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));        
         servingPanel.add(getToolBar(),BorderLayout.NORTH);
         servingPanel.add(getServingTable(),BorderLayout.CENTER);
      }
      return servingPanel;
   }
   
   private JButton getAddButton() {
      if (null == addBtn) {
          ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/Add24.gif"));
          addBtn = new JButton(icon);
          addBtn.setToolTipText("Add a new serving.");
          addBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 doAddFood();
              }
          });
      }
      return addBtn;
  }

   private JButton getPrintButton() {
      if (null == printBtn) {
         ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage(
               "/img/Print24.gif"));
         printBtn = new JButton(icon);
         printBtn.setToolTipText("Print the recipe.");
         printBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               doPrint();
            }
         });
      }
      return printBtn;
   }
   
   private JButton getDeleteButton() {
      if (null == delBtn) {
          ImageIcon icon = new ImageIcon(ImageFactory.getInstance().loadImage("/img/Delete24.gif"));
          delBtn = new JButton(icon);
          delBtn.setEnabled(false);
          delBtn.setToolTipText("Delete the selected serving.");
          delBtn.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  doDeleteServing();
              }
          });
      }
      return delBtn;
  }
   
  private void doDeleteServing() {
     getServingTable().deleteSelectedServings();
  }

   private void doAddFood() {
      SearchDialog sd = new SearchDialog(getDialog());
      sd.display();
      Serving s = sd.getSelectedServing();
      if (s != null) {
         if (s.getFoodProxy().getSource() == recipe.getSource() && 
               s.getFoodProxy().getSourceID() == recipe.getSourceUID()) {
            JOptionPane.showMessageDialog(this, "A recipe can not contain itself!");
         } else {
            getServingTable().addServing(s);
         }
      }
   }
 
   
   
   
}