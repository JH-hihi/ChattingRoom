package qq;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

public class Server extends Thread{
	private ServerSocket server;
	private Socket s;
	private ArrayList<Chatting>list=new ArrayList<Chatting>();
	private JFrame jf=new JFrame("聊天室服务器");
	private JPanel jp=new JPanel(null);
	private JLabel online=new JLabel("在线列表:");
	private List onlinelist=new List();
	private JScrollPane jsp1=new JScrollPane(onlinelist);
	private JTextArea message=new JTextArea();
	private JScrollPane jsp2=new JScrollPane(message);
	private JButton send=new JButton("群发消息");
	private JButton offline=new JButton("强制下线");
	
	public Server() {
		jf.setSize(450,550);
		jf.setLocation(200,200);
		online.setBounds(30,20,100,30);
		//设置在线列表
		jsp1.setBounds(20,50,250,300);
		jsp1.setViewportView(onlinelist);
		jsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//设置输入框
		jsp2.setBounds(20,360,250,100);
		jsp2.setViewportView(message);
		jsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//设置按钮
		send.setBounds(270,360,90,30);
		offline.setBounds(270,50,90,30);
		//添加组件
		jp.add(online);
		jp.add(jsp1);
		jp.add(jsp2);
		jp.add(send);
		jp.add(offline);
		jf.add(jp);
		jf.setContentPane(jp);
		jf.setLayout(null);
		jf.setVisible(true);  //使窗体可见
		new Thread(this).start();
		//监听服务器的下线	
		jf.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		for(Chatting chat:list) {
	    			chat.writer.println("FuwuqiExit");
	    		}
	    		JOptionPane.showMessageDialog(jp,"服务器已关闭!","提示",JOptionPane.INFORMATION_MESSAGE);
	    		System.exit(0);
	    	}
		});
		//监听send	
		send.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent arg0) {
		    	  if(!message.getText().equals(null)) {
						for(Chatting chat:list) {
							chat.writer.println("服务器说："+message.getText());
							chat.writer.flush();					
						}
		    	  }
		    	  message.setText("");							
		      }	
		});
		//监听offline	
		offline.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent arg0) {
					String str=onlinelist.getSelectedItem().toString();
					for(int i=0;i<list.size();i++) {
						if(list.get(i).username==str) {
							list.get(i).writer.println("Exit#");
							list.get(i).writer.flush();
							break;
						}
					}
		      }	
		});
	}
	public void run(){
		try {
			server=new ServerSocket(8888);
			while(true) {
				s=server.accept();
				Chatting chat=new Chatting(s);
				list.add(chat);
				chat.start();
			}					
		}catch(IOException ex) {
			ex.printStackTrace();
		}finally {
			//关闭资源
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	    	
	    }	
	}

	class Chatting extends Thread {
		String username;
		BufferedReader reader;
		PrintWriter writer;		
		public Chatting(Socket s) throws IOException {
			reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer=new PrintWriter(s.getOutputStream(),true);
		}
		public void run() {
			try {
				while(!isInterrupted()) {
					String ss=reader.readLine();
					String str[]=ss.split("#");					
					Here:if(str[0].equals("Login")) {//实时更新客户端上线	
						username=str[1];
						if(list.size()==1) {
							onlinelist.add(username);
						}
						else {
							for(int i=list.size()-2;i>=0;i--) {
								if(list.get(i).username.equals(str[1])) {							
									this.writer.print("Repeat Login#"+username+"\n");//账号重复登录
									this.writer.flush();
									list.remove(list.size()-1);
									break Here;
								}
							}
							onlinelist.add(username);
							String usernames="Online#";
							for(Chatting chat:list) {
								usernames+=chat.username+"#";
							}
							for(Chatting chat:list) {
								chat.writer.println(usernames);
								chat.writer.flush();
							}
						}					
					}
					else if(str[0].equals("Exit")){//实时刷新客户端下线
						list.remove(this);					
						String usernames="Online#";
						onlinelist.removeAll();
						for(Chatting chat:list) {
							usernames+=chat.username+"#";
							onlinelist.add(chat.username);
						}
						for(Chatting chat:list) {
							chat.writer.println(usernames);
							chat.writer.flush();
						}	
						this.interrupt();
					}
					else if(str[0].equals("Siliao")) {//转发私聊消息
						for(Chatting chat : list) {
							if(str[2].equals(chat.username)) {
								chat.writer.println("Siliao#"+str[1]+"#"+str[3]);
								chat.writer.flush();
								break;
							}
						}
					}
					else if(str[0].equals("SiliaoExit")) {//私聊结束
						for(Chatting chat : list) {
							if(str[2].equals(chat.username)) {
								chat.writer.println("SiliaoExit#"+str[1]);
								chat.writer.flush();
								break;
							}
						}
					}
					else if(!ss.equals("")){//服务器转发群聊消息
						for(Chatting chat:list) {
							chat.writer.println(ss);
							chat.writer.flush();
						}
					}
				}				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}		
	}
	public static void main(String[] args) {
		new Server();   
	}
}
