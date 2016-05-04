
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.xml.ws.handler.MessageContext.Scope;

/**
 * This frame contains a long word list and a label that shows a sentence made up from the chosen
 * word.
 */
public class LongListFrame extends JFrame
{
   private JList<String> wordList;

   public LongListFrame(WordListModel m)
   {
	  setSize(600, 400);

      wordList = new JList<String>(m);
      wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      wordList.setPrototypeCellValue("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
      wordList.setVisibleRowCount(12);

      JScrollPane scrollPane = new JScrollPane(wordList);
      m.setListInstance(wordList, scrollPane);

      add(scrollPane);
      pack();
   }

   public void showDialog(){
	   new SettingDialog(this).setVisible(true);
   }

   private class SettingDialog extends JDialog
   {
      public SettingDialog(LongListFrame frame)
      {
         super(frame, "Input RoomId", true);

         // add HTML label to center
         setPreferredSize(new Dimension(400,300));

         JPanel pan=new JPanel();
         pan.setLayout(new GridBagLayout());


         JLabel urLabel = new JLabel("RoomId");
         pan.add(urLabel);
         JTextField urlText = new JTextField("", 20);
     	  pan.add(urlText);

     	JDialog outer = this;


         // add OK button to southern border
      // OK button closes the dialog

         JButton ok = new JButton("OK");
         ok.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
//               	settings.setProperty("url", urlText.getText());
//               	settings.setProperty("outputFile", outputFileText.getText());
//               	settings.saveSettings();
            	  Comment.setRoomId(Integer.parseInt(urlText.getText()));
                  setVisible(false);
               }
            });


         pan.add(ok);
         add(pan);

         pack();
      }
   }

}


