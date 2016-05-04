import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.xml.ws.handler.MessageContext.Scope;

public class WordListModel extends AbstractListModel<String>
{
   private static List<String> list = null;
   private static JList<String> wordList = null;
   private static JScrollPane scrollPane = null;

   
   public void setListInstance(JList<String> l, JScrollPane sp) {
	   wordList = l;
	   scrollPane = sp;
   }
   public static List<String> getList(){
	   return list;
   }
   
   public void insert(String s){
	   if (list == null){
		   list = new ArrayList<>();
	   }
	   list.add(s);
	   
	   if (scrollPane != null && wordList != null){
		   wordList.ensureIndexIsVisible(list.size()-1);
		   scrollPane.updateUI();
		   
	   }
   }

   public int getSize()
   {
	   if (list == null){
		   return 0;
	   }
      return list.size();
   }

   public String getElementAt(int n)
   {
	   if (list != null){
		   return list.get(n);
	   }else {
		   return "";
	   }
      
   }
}
