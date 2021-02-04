package qq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientShow extends Thread{
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	private String username;//用户名
	private JFrame jf=new JFrame("聊天室登录界面");
	private JPanel jp=new JPanel();
	private JLabel User=new JLabel("账号");
	private JLabel password=new JLabel("密码");
	private JTextField userTxt=new JTextField();
	private JPasswordField passTxt=new JPasswordField();
	private JButton sure=new JButton("登录");
	private JButton quit=new JButton("取消");
	private ImageIcon image=new ImageIcon("D:\\java\\java-code\\ChattingRoom\\qq.jpeg");
	private JLabel QQimage=new JLabel(image);
	private CopyOnWriteArrayList<String>friendnames=new CopyOnWriteArrayList<String>();//存储好友列表
	
    public ClientShow(){ //登录界面
    	jf.setSize(350,400);
    	jf.setLocation(800,300);
    	QQimage.setBounds(120,10,100,100);
    	User.setBounds(50,145,50,50);
    	userTxt.setBounds(100,150,150,35);
    	password.setBounds(50,195,50,50);
    	passTxt.setBounds(100,200,150,35);
    	sure.setBounds(100, 250,70,30);
    	quit.setBounds(170,250,70, 30);
    	jp.add(QQimage);
    	jp.add(User);
    	jp.add(userTxt);
    	jp.add(password);   	
    	jp.add(passTxt);
    	jp.add(sure);
    	jp.add(quit);
    	jp.setLayout(null);
    	jf.add(jp);
		jf.setContentPane(jp);
		jf.setVisible(true);  //使窗体可见
		this.start();
	}
    public void run() {
		//监听sure按钮
		sure.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent arg0) {		    	 
		    	 username=userTxt.getText().trim();
		    	 char c[]=passTxt.getPassword();
		    	 String mima=new String(c);
		    	 if(username.equals("")||mima.equals("")) {
		    		 JOptionPane.showMessageDialog(jp,"账号或者密码不可以为空！","提示",JOptionPane.INFORMATION_MESSAGE);
		    	 }
		    	 else {
		    		 try {
		        		 s=new Socket("localhost",8888);
		    			 in=new BufferedReader(new InputStreamReader(s.getInputStream()));
		    			 out=new PrintWriter(s.getOutputStream(),true);
		    			 out.println("Login#"+username);
				    	 new Thread(new Client()).start();
						 jf.dispose();
		    		 }catch(IOException ex) {
		    			 JOptionPane.showMessageDialog(jp,"服务器未连接!","提示",JOptionPane.INFORMATION_MESSAGE);
		    			 System.exit(0);
		    		 }
		    	 }
		      }	
		});
		//监听quit按钮
		quit.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent arg0) {
		    	  int i=JOptionPane.showConfirmDialog(jp, "退出系统？");
		    	  if(i==0) {
		    		  System.exit(0);
		    	  }
		      }	
		});
    }
    class Client extends Thread{	
    	private JFrame jf=new JFrame(userTxt.getText().trim()+"的聊天室");
    	private JPanel jp=new JPanel();
    	private JLabel friend=new JLabel("好友列表");
    	private List friendlist=new List();//显示好友列表
    	private JScrollPane jsp1=new JScrollPane(friendlist); 
    	private JLabel online=new JLabel("在线好友");
    	private List onlinelist=new List();//显示在线好友
    	private JScrollPane jsp2=new JScrollPane(onlinelist);
    	private JLabel group=new JLabel("群聊");
    	private JTextArea groupchat=new JTextArea();//显示群聊
    	private JScrollPane jsp3=new JScrollPane(groupchat);
    	private JTextArea message=new JTextArea();//消息输入框
    	private JScrollPane jsp4=new JScrollPane(message);
    	private JButton send=new JButton("发送");    	
    	private ArrayList<PrivateChat>private_chats=new ArrayList<PrivateChat>();//私聊用户
    	public Client() { //用户个人主页		
        	jf.setSize(700,600);
        	jf.setLocation(800,200);
        	//设置好友列表
        	friend.setBounds(10,10,100,40);
        	jsp1.setBounds(10,50,150,500);
    		jsp1.setViewportView(friendlist);
    		jsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    		//设置在线好友显示
        	online.setBounds(160,10,100,40);
        	jsp2.setBounds(160,50,150,500);
    		jsp2.setViewportView(onlinelist);
    		jsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    		//设置群聊显示
        	group.setBounds(310,10,100,50);
        	jsp3.setBounds(310,50,360,440);
    		jsp3.setViewportView(groupchat);
    		jsp3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    		//设置消息发送框
        	jsp4.setBounds(310,490,300,60);
    		jsp4.setViewportView(message);
    		jsp4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        	send.setBounds(610,490,60,60);
        	//添加组件
        	jp.add(friend);
        	jp.add(jsp1);
        	jp.add(online);
        	jp.add(jsp2);
        	jp.add(group);
        	jp.add(jsp3);
        	jp.add(jsp4);
        	jp.add(send);
        	jf.add(jp);
    		jf.setContentPane(jp);
    		jf.setLayout(null);
    		jf.setVisible(true);  //使窗体可见   	
    		String names[]= {"Lisa","Amy","George","Lily","Rawson"};
    		for(String ss:names) {
    			if(!ss.equals(username)) {
    				friendlist.add(ss);
    				friendnames.add(ss);
    			}			
    		}
    		//监听客户端的下线	
    		jf.addWindowListener(new WindowAdapter() {
    	    	public void windowClosing(WindowEvent e) {
    	    		out.println("Exit#"+username);
    	    		out.flush();
    	    		JOptionPane.showMessageDialog(jp,username+"已下线!","提示",JOptionPane.INFORMATION_MESSAGE);
  		    		System.exit(0); 
    	    	}
    		});
    		//监听send按钮
    		send.addActionListener(new ActionListener() {
    		      public void actionPerformed(ActionEvent arg0) {
    		    	  out.print(username+":"+message.getText()+"\r\n");
    		    	  message.setText("");
    		    	  out.flush();
    		      }	
    		});
    		//监听私聊事件
    		onlinelist.addMouseListener(new MouseAdapter() {
    			public void mouseClicked(MouseEvent e) {
    				if(e.getButton()==MouseEvent.BUTTON1&&e.getClickCount()==2) {
    					String siliaoname=onlinelist.getSelectedItem().toString();
    					PrivateChat chat=new PrivateChat(siliaoname);
    					private_chats.add(chat);
    				}
    			}
    		});
    		friendnames.add("#");
    	}

        public void run() {
        	try {
    			while(true){
    	    		//接收服务器消息
    				String ss=in.readLine();
    				String str[]=ss.split("#");
	    		    if(str[0].equals("Repeat Login")) {//重复登录
	    				JOptionPane.showMessageDialog(jp, "已经在线了，不可重复登录！", "提示",JOptionPane.INFORMATION_MESSAGE);
	    				System.exit(0);
	    			}
	    			else if(str[0].equals("Exit")) {//服务器要求退出
	    	    		out.println("Exit#"+username);
	    	    		out.flush();
	    				JOptionPane.showMessageDialog(jp,username+"被强制提出聊天室!","提示",JOptionPane.INFORMATION_MESSAGE);
    					System.exit(0);
    				}
    				else if(str[0].equals("Online")) {//更新在线好友
    					onlinelist.removeAll();
    					for(int i=1;i<str.length;i++) {
    						if(!str[i].equals(username)) {
    							onlinelist.add(str[i]);
        						boolean update=false;
        						for(String name:friendnames) {
        							if(!str[i].equals(name)) { 
        								update=true;
        							}
        							else {
        								update=false;
        								break;
        							}
        						}
        						if(update==true) {
    								friendnames.add(str[i]);//添加好友
    								friendlist.add(str[i]);
        						}
    						}   						
    					}
    				}
    				else if(str[0].equals("Siliao")) {//私聊消息
    					boolean add=false;
    					if(private_chats.size()==0) {
        					PrivateChat chat1=new PrivateChat(str[1]);
        					private_chats.add(chat1);
        					chat1.message1.append(str[1]+":"+str[2]+"\n");
    					}
    					else {
        					for(PrivateChat chat:private_chats) {					
        						if(!str[1].equals(chat.name)) {
        							add=true;   							
        						}
        						else {
        							add=false;
        							chat.message1.append(str[1]+":"+str[2]+"\n");
        							break;
        						}
        					}
        					if(add==true) {
            					PrivateChat chat2=new PrivateChat(str[1]);
            					private_chats.add(chat2);
            					chat2.message1.append(str[1]+":"+str[2]+"\n");
        					}
    					}
    				}
    				else if(str[0].equals("SiliaoExit")) {//私聊关闭
	    	    		for(int i=0;i<private_chats.size();i++) {
	    	    			if(str[1].equals(private_chats.get(i).name)) {
	    	    				private_chats.remove(i);
	    	    			}
	    	    		}
    				}
    				else if(str[0].equals("FuwuqiExit")) {
    					JOptionPane.showMessageDialog(jp,"服务器已退出!","提示",JOptionPane.INFORMATION_MESSAGE);
    					System.exit(0);
    				}
    				else { //群聊消息
    					groupchat.append(ss+"\n");
    				}
    			}
        	}catch(IOException ex) {
        		ex.printStackTrace();
    		}
        }
        class PrivateChat extends Thread {
        	String name;
        	JFrame jf1=new JFrame();
        	JPanel jp1=new JPanel(null);
        	JTextArea message1=new JTextArea();
        	JScrollPane jsp5=new JScrollPane(message1);
        	JTextArea message2=new JTextArea();
        	JScrollPane jsp6=new JScrollPane(message2);
        	JButton send1=new JButton("发送");
        	public PrivateChat(String str) {
        		name=str;
        		jf1.setTitle(username+"和"+str+"的聊天界面");
            	jf1.setSize(450,530);
            	jf1.setLocation(800,200);
            	jsp5.setBounds(10,10,380,400);
        		jsp5.setViewportView(message1);
        		jsp5.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            	jsp6.setBounds(10,410,300,60);
        		jsp6.setViewportView(message2);
        		jsp6.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        		send1.setBounds(310,410,80,60);
        		jp1.add(jsp5);
        		jp1.add(jsp6);
        		jp1.add(send1);
        		jf1.add(jp1);
        		jf1.setContentPane(jp1);
        		jf1.setLayout(null);
        		jf1.setVisible(true);  //使窗体可见     		
        		//监听send1
	    		send1.addActionListener(new ActionListener() {
	    		      public void actionPerformed(ActionEvent arg0) {
	    		    	  out.println("Siliao#"+username+"#"+name+"#"+message2.getText());
	    		    	  out.flush();
	    		    	  message1.append(username+":"+message2.getText()+"\n");
	    		    	  message2.setText("");	    	  
	    		      }	
	    		});
	    		//监听私聊窗口的关闭	
	    		jf1.addWindowListener(new WindowAdapter() {
	    	    	public void windowClosing(WindowEvent e) {
	    	    		for(int i=0;i<private_chats.size();i++) {
	    	    			if((name).equals(private_chats.get(i).name)) {
	    	    				private_chats.remove(i);
	    	    			}
	    	    		}
	    	    		out.println("SiliaoExit#"+username+"#"+name);
	    	    		out.flush();
	    	    		JOptionPane.showMessageDialog(jp1,"退出私聊!","提示",JOptionPane.INFORMATION_MESSAGE);
	    	    	}
	    		});
        	}
        }
    }

	public static void main(String[] args) {
        new ClientShow(); 
	}

}
