package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * the hardest version.
 * @author yinling
 * @version 0.6
 *
 */
public class Server {
	
	private ServerSocket  ss = null ;
	private boolean startFlag = false ;
	private int linkNum1 ;
	private int closeNum2 ;
	private List<Client> clients = new ArrayList<Client>();
	
	/**
	 * ��ɽ����ӿͻ���   ��   ��ȡ�ͻ��˷��͹��������ݷֿ��Ĺ��ܡ�
	 * �������ܽ������ͻ��˲������ӵ����⡣
	 * 		Ϊʲô����ɺ�����   ����Ϊ��������ʵ�ֵ�ʱ��ʵ�����߳�������ʱ��
	 * 	������쳣��
	 */
	public void start() {
		try {
			//���÷���˵�tcp-serverSocket  ���ղ��֡�
			ss = new ServerSocket(8888);
		}catch(BindException e) {
			System.out.println("�˿�ʹ���С�������");
			System.out.println("������֮���˳����򡣡���");
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
			
			System.exit(0);
		}catch(IOException ff) {
			
			ff.printStackTrace();
		}
		//���÷���˵�tcp-serverSocket  ���ղ��֡�
		//ѭ�����գ� 
		try {
			startFlag = true ;
			while(startFlag) {
				Socket sk = ss.accept();
				linkNum1++ ;
System.out.println("A Client connected.");				
				//���̴߳���ÿһ���ͻ��˵Ĵ��䵽������е�����
				//ʹ������������������readUTF(String str)������ʽ�Ķ�ȡ�ķ���������������ʽ�ģ�ÿһ���̲߳���Ӱ�������̵߳Ķ�ȡ�Ĺ�����ʵ�֡�
				Client cl = new Client(sk);
				clients.add(cl);
				Thread th = new Thread(cl);
				th.start();
				
			}	
		}catch(SocketException ff) {
			System.out.println("�������������׳��쳣��");
			System.out.println("����˳���Ҳ�Ѿ��رա�");
		}	
		 catch (IOException e) {
			System.out.println("��io�쳣");
		}
	}
	//�ڲ���ʵ��ÿһ�ͻ�������ʽ�Ķ�ȡ���ݵĹ��ܡ�������������߳����Ӱ�� ��
	
	@SuppressWarnings("unused")
	private  class Client implements Runnable{
		//��Ҫ����ÿһ�����̶߳��� ��û�н���ʹ���ⲿ��װ����Ψһ���߳�ʵ�����Ķ���
		private Socket s ;
		
		//��Ҫ������һ��ѭ��������������ѭ���Ķ�ȡ���ݹ��ܵģ��жϵ�������
		private boolean readFlag  =false ;
		
		private DataInputStream dis = null ;
		
		private DataOutputStream dos  = null ;
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}
		
		
		//���췽�������ʵ������
		@SuppressWarnings("unused")
		public Client(Socket s) {
			this.s = s ;
			try {
				dis = new DataInputStream(this.s.getInputStream());
				dos = new DataOutputStream(this.s.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			readFlag = true ;
		}
		@SuppressWarnings("unused")
		public Client() {}
		
		public void send(String str) {
			try {
				this.dos.writeUTF(str);
			} catch (IOException e) {
				
				clients.remove(this);
			}	
		}
		@Override
		public void run() {
			
			try {
				//����ÿһ�ζ�������������Ӧ�ý���ʼ������ʵ�֣����ڹ��췽������ɡ��������õĹ���ʵ�֣����Խ�ʡ�ڴ�ռ䡣
				
//				dis = new DataInputStream(this.s.getInputStream());
//				readFlag = true ;
				
				while(readFlag) {
					String str = dis.readUTF();
					System.out.println("From Client: "+str);
					
					for(int i = 0;i<clients.size();i++) {
						Client client = clients.get(i);
						client.send(str);
					}	
				}
			} 
			catch(EOFException EOF ) {
				closeNum2 ++ ;
				System.out.println("The Client have just closed.");
				if(linkNum1 == closeNum2 ) {
					try {
						ss.close();
					}catch(SocketException ff) {
						System.out.println("�������������׳��쳣��");
						System.out.println("����˳���Ҳ�Ѿ��رա�");
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("��io�쳣��");
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(dis != null ) {dis.close();}
					if(s != null) {s.close();}
				}catch(IOException ee) {
					ee.printStackTrace();
				}
			}
		}
		
	}
	public static void main(String[] args) {
		new Server().start();
		
	}

}
