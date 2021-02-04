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
	private JFrame jf=new JFrame("�����ҷ�����");
	private JPanel jp=new JPanel(null);
	private JLabel online=new JLabel("�����б�:");
	private List onlinelist=new List();
	private JScrollPane jsp1=new JScrollPane(onlinelist);
	private JTextArea message=new JTextArea();
	private JScrollPane jsp2=new JScrollPane(message);
	private JButton send=new JButton("Ⱥ����Ϣ");
	private JButton offline=new JButton("ǿ������");
	
	public Server() {
		jf.setSize(450,550);
		jf.setLocation(200,200);
		online.setBounds(30,20,100,30);
		//���������б�
		jsp1.setBounds(20,50,250,300);
		jsp1.setViewportView(onlinelist);
		jsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//���������
		jsp2.setBounds(20,360,250,100);
		jsp2.setViewportView(message);
		jsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//���ð�ť
		send.setBounds(270,360,90,30);
		offline.setBounds(270,50,90,30);
		//������
		jp.add(online);
		jp.add(jsp1);
		jp.add(jsp2);
		jp.add(send);
		jp.add(offline);
		jf.add(jp);
		jf.setContentPane(jp);
		jf.setLayout(null);
		jf.setVisible(true);  //ʹ����ɼ�
		new Thread(this).start();
		//����������������	
		jf.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		for(Chatting chat:list) {
	    			chat.writer.println("FuwuqiExit");
	    		}
	    		JOptionPane.showMessageDialog(jp,"�������ѹر�!","��ʾ",JOptionPane.INFORMATION_MESSAGE);
	    		System.exit(0);
	    	}
		});
		//����send	
		send.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent arg0) {
		    	  if(!message.getText().equals(null)) {
						for(Chatting chat:list) {
							chat.writer.println("������˵��"+message.getText());
							chat.writer.flush();					
						}
		    	  }
		    	  message.setText("");							
		      }	
		});
		//����offline	
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
			//�ر���Դ
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
					Here:if(str[0].equals("Login")) {//ʵʱ���¿ͻ�������	
						username=str[1];
						if(list.size()==1) {
							onlinelist.add(username);
						}
						else {
							for(int i=list.size()-2;i>=0;i--) {
								if(list.get(i).username.equals(str[1])) {							
									this.writer.print("Repeat Login#"+username+"\n");//�˺��ظ���¼
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
					else if(str[0].equals("Exit")){//ʵʱˢ�¿ͻ�������
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
					else if(str[0].equals("Siliao")) {//ת��˽����Ϣ
						for(Chatting chat : list) {
							if(str[2].equals(chat.username)) {
								chat.writer.println("Siliao#"+str[1]+"#"+str[3]);
								chat.writer.flush();
								break;
							}
						}
					}
					else if(str[0].equals("SiliaoExit")) {//˽�Ľ���
						for(Chatting chat : list) {
							if(str[2].equals(chat.username)) {
								chat.writer.println("SiliaoExit#"+str[1]);
								chat.writer.flush();
								break;
							}
						}
					}
					else if(!ss.equals("")){//������ת��Ⱥ����Ϣ
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
