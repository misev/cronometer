/*
 * Created on 31-Dec-2005
 */
package ca.spaz.cron.actions;

import java.awt.event.ActionEvent;

import javax.swing.*;

import ca.spaz.cron.ui.ServingTable;
import ca.spaz.util.ImageFactory;


public class DeleteServingsAction extends AbstractAction {
   private ServingTable servingTable;
   
   public DeleteServingsAction(ServingTable servingTable) {
      super("Delete Servings");
      this.servingTable = servingTable;
      putValue(SMALL_ICON, new ImageIcon(ImageFactory.getInstance().loadImage("/img/Delete24.gif")));
      putValue(SHORT_DESCRIPTION, "Delete the selected servings from this list");
       
   }      
   
   public void actionPerformed(ActionEvent e) {
      assert (servingTable != null);
      servingTable.deleteSelectedServings();      
   }
}