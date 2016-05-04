//package socket;

import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.IRObject;
import org.json.simple.parser.JSONParser;


/**
 * This program makes a socket connection to the atomic clock in Boulder, Colorado, and prints the time that the server
 * sends.
 * 
 * @version 1.20 2004-08-03
 * @author Cay Horstmann
 */
public class BilibiliLiveDanmaku
{
   public static void main(String[] args) throws IOException
   {
	   EventQueue.invokeLater(new Runnable()
       {
          public void run()
          {
        	 WordListModel m = new WordListModel();
             JFrame frame = new LongListFrame(m);
             frame.setTitle("BilibiliLiveDanmaku");
             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             frame.setVisible(true);
             ((LongListFrame) frame).showDialog();
             Runnable cmt = new Comment(m);
             Thread t = new Thread(cmt);
             t.start();
          }
       });
	   
      
   }
}


class Comment implements Runnable
{
	private static WordListModel m = null;
	private static int roomId = 0;
	
	public static void setRoomId(int id) {
		roomId = id;
	}

	public Comment(WordListModel model) {
		m = model;
	}
	
	private int randInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try (Socket s = new Socket("livecmt-1.bilibili.com", 88))
      {
			s.setKeepAlive(true);
    	 System.out.println("success connect");
    	 OutputStream outs = s.getOutputStream();
//    	 String handshake = String.format("0101000c%08X%08X", 5269, 102190);
//    	 String handshake = String.format("0101000c%08X%08X", roomId, randInt(100000, 190000));
    	 String handshake = String.format("0101000c%08X%08X", roomId, 0);
    	 String backup = "0101000c00001495000186e6";
    	 byte[] b = DatatypeConverter.parseHexBinary(handshake);
    	 byte[] ensure = DatatypeConverter.parseHexBinary("01020004");
    	 outs.write(b);
    	 System.out.println("send success");
         InputStream inStream = s.getInputStream();
         System.out.println(inStream);
         byte[] bb = new byte[2048];
         JSONParser parser=new JSONParser();
         long t0 = System.currentTimeMillis();
         long t1 = 0;
         Runnable r1 = new HandShake(outs);
         Thread tt = new Thread(r1);
         tt.start();
         int readCount = 0;
         while(true){
        	 if (!s.isConnected()){
        		 System.out.println("**********closed****");
        	 }
        	 Arrays.fill(bb, (byte)0);
        	 readCount = inStream.read(bb, 0, 2048);
        	  System.out.println("read from inStream, total: " + readCount);
        	  if(readCount < 10){
        		  try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		  continue;
        	  }
	           String str = new String(bb, StandardCharsets.UTF_8);
	           List<String> rt = Extract.extractAll("{", "}", str);
	           Iterator<String> it = rt.iterator();
	           while(it.hasNext()){
	        	   String st = it.next();
	        	   st = "{" + st + "}";
	        	   try {
					Object obj = parser.parse(st);
					JSONObject obj1 = (JSONObject)obj;
					String cmd = (String) obj1.get("cmd");
					if (cmd.equals("DANMU_MSG")){
						JSONArray info = (JSONArray) obj1.get("info");
						String msg = (String) info.get(1);
						JSONArray userInfo = (JSONArray) info.get(2);
						String name = (String) userInfo.get(1);
						System.out.println(name + "：" + msg);
						m.insert(name + "：" + msg);
					}
					} catch (ParseException e) {
					}
	           }
         	}
      	} catch (IOException e1) {
		}
	}
	
}

class HandShake implements Runnable
{
	private OutputStream outPut = null;
	private byte[] ensure = DatatypeConverter.parseHexBinary("01020004");
	
	public HandShake(OutputStream outs) {
		// TODO Auto-generated constructor stub
		outPut = outs;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		long t0 = System.currentTimeMillis();
        long t1 = 0;
        try {
			outPut.write(ensure);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
        while(true){
       	 t1 = System.currentTimeMillis();
       	 if (t1 - t0 >= 30000){
       		 try {
				outPut.write(ensure);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       		 System.out.println("ensure" + (t1 - t0)/1000);
       		 t0 = t1;
       	 } else {
       		 try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	 }
	}
	
}
}

class Extract
{
	public static String extract(String begin, String end, String html){
		if (html == null || html.equals("")){
			return "";
		}
	    int start = html.indexOf(begin);
	    int ends = 0;
	    if (start >= 0){
	    	start += begin.length();
	    	if (end != null){
	    		ends = html.indexOf(end, start);
	    	}
	    	if (ends >= 0){
	    		return html.substring(start, ends);
	    	}
	    }
		return "";
	}
	
	public static List<String> extractAll(String begin, String end, String html){
		List<String> list = new ArrayList<>();
		if (html == null || html.equals("")){
			return list;
		}
		int fromPos = 0;
		int endPos = 0;
		int start = 0;
		
		while(true){
			start = html.indexOf(begin, fromPos);
			if (start >= 0){
				start += begin.length();
				endPos = html.indexOf(end, start);
				if (endPos >= 0){
					list.add(html.substring(start, endPos));
					fromPos = endPos + end.length();
					continue;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		return list;
	}
}
