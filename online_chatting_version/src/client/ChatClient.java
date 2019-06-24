package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

@SuppressWarnings("serial")
public class ChatClient extends Frame{
	//��Ϊ�漰������Ľ������ݣ��������ó�ʵ����������
	TextField tf ;
	TextArea ta ;
	//f)
	Socket socket =null;
	DataOutputStream dos = null;//g)
	private boolean flag = false ;
	DataInputStream dis = null;
	
	public static void main(String[] args) {
		new ChatClient().launchFrame();
		
	}
	public void launchFrame() {
		this.setLocation(400, 300);
		this.setSize(300, 400);
	
		
		//TextField,�в�ͬ�Ĺ����������忴api�ĵ����������ó�ʼ������ʾ�ַ�����Ҳ���Թ涨����������һָ����ʼ���ַ�����������
		tf = new TextField();
		//TextArea�������ı��򣬿�����Ϊ���룬Ҳ�������������ʾ�����صĹ���������������ָ��������������
		ta = new TextArea();
		this.add(tf, BorderLayout.SOUTH);
		this.add(ta, BorderLayout.NORTH);
		//��Χʽ�Ľ��齨��ʾ������
		this.pack();
		
		connectServer();
		//�����ڲ��࣬ʹ��������Ϊ�������ùرչ��ܵļ�������
		//���ü�����������ʹ���ڲ��ࡣҲ����ʹ���ⲿ�ࡣ������ʹ�������ڲ��ࣨ�߼��򵥵�ʵ�֣��������ʹ�����ֶ��������ڲ��ࡣ��
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//�رգ�g)version0.4 Ӧ���ڹر���������֮ǰ�����е�ϵͳ��Դ�رա�
				disconnect();
				System.exit(0);
			}	
		});
		
		
		new Thread(new ReceThread()).start();
		tf.addActionListener(new ActionMonitor());
	
		this.setVisible(true);
		
		//��textField������Ķ������ȿ�����ô��������ʾ��textArea�С�
		//textFidld���������ü������еķ�������Ϊ���ü����������ַ�����������һ���������ڲ����ʵ�ֵķ�����
		//�����Կ��ǽ������ó�Ϊ˽�е��ڲ������ʽ��
//		tf.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//			//getSource():The object on which the Event initially occurred����eventObject���еķ�����
//			TextField  TF = (TextField)e.getSource();
//			//textArea�����������������ʾ���ַ�����setText()�ķ��� ��
//			ta.setText(TF.getText());
//			//����enter�س�����֮��textField����������Ϊ�ա�
//			TF.setText(null);
//			}
//		});	
	}
	

	//g)version0.4   ����رշ�������launchFrame()�ķ����У����ڹر������ڲ����еĴ��ڹر�֮ǰ����������ر���Դ�ķ�����
	public void disconnect() {
		try {
			dos.close();
			dis.close();
			socket.close();
		}catch(SocketException ee) {
			System.out.println("������SocketException���쳣��");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	//����
	public void connectServer() {
		//���ӿͻ��ˡ��������ó�һ���������ھ�Ҫ���ӿͻ��ˣ����Ϊ�˱�֤���ڳ���ļ���ԣ���ý����ӵĲ��������һ�������ķ����С�
		try {
			//f)��Socket ss���ó��ⲿ�İ�װ���ʵ���������ԡ�
			socket = new Socket("127.0.0.1",8888);
			flag = true ;
			//socket.close(); ע��ֲ�����ɵ�ʱ��һ�����ܹر����socket������Ϊ���������˵�ʱ�����socket������Ҫʹ�õġ�
		//g)version0.4  ���ӳɹ�֮�󣬾�ֱ�ӽ������ʵ��������
			
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e1) {
			
			e1.printStackTrace();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
	}
	private class ActionMonitor implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			TextField tfd = (TextField)e.getSource();
			String input = tfd.getText().trim(); 
			//��ʵ�������ƣ���Ϊ����ֻ�ܿ������һ�ε����룬֮ǰ���������Ϣ��û���ˡ�������д�������ڽ��иĽ���
			//ta.setText(input);
			tf.setText("");
			
			//f)��һ���Ŀ�����ɵ������ǣ�ͨ��socket�������뵽textField���ַ�����������������˵Ĵ����С�
			//��ʱ������Ҫʹ���ⲿ�İ�װ����launchFrame()�����ľֲ��������������û�з���ֵ�����������������֮�󣬴�����ջ�ڴ��е�socket�ĵڶ���ͻᱻ�����
			//Ϊ�˽��������⣺��Ҫ��socket���ó�ʵ�����Ķ���
			try {
				//��socket�׽���һ���ֽ��������Ȼ���׽���һ��������DataOutputStream �������ĺô��ǣ�
				//����ֱ��д����ֵ��������͵����ݵ�������С�writeUTF(String strs),���ַ�����utf-16�ı��뷽ʽд�뵽������С�
				//Ҳ����ʹ��ʹ���������ַ��������
				//g)version0.4  ��������������ɿ����ظ�ʹ�õģ����Խ�ʡ�ڴ�ռ䡣
				//DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(input);
				dos.flush();
				//dos.close(); g)version0.4 �����Ҳ�ȱ�رգ�ҲӦ����һ�����Ʒ����У������������رա�
				//socket.close();g)version0.4�����ܹرգ���Ҫѭ���������ַ�������������ַ�����textArea�ͷ���˵�consle����ô�Ͳ��ܽ����socket�رգ�����ʧ�ܾͲ����׷������ˣ�Ӧ��Эдһ�������ķ�����
				
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}	
		}	
	}
	private class ReceThread implements Runnable{

		@Override
		public void run() {
			while(flag) {
				try {
					dis = new DataInputStream(socket.getInputStream());
					String  str = dis.readUTF();
					ta.setText(ta.getText()+str+"\n");	
				}catch(SocketException s) {
					flag = false ;
					System.out.println("socket �ر��ˣ�socket.getInputStream��������ʹ���ˡ�");
				}catch(EOFException EOF) {
					System.out.println("�������ַ��������ˣ���Ϊ����ͻ����˳��ˣ�");
				}
				catch (IOException e) {
					e.printStackTrace();
				}	
			}	
		}	
	}
}

