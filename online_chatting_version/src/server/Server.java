package server;

import java.io.*;
import java.net.*;
import java.util.*;


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
	 * 完成将连接客户端   和   读取客户端发送过来的数据分开的功能。
	 * 这样就能解决多个客户端不能连接的问题。
	 * 		为什么定义成函数？   是因为主方法中实现的时候，实例化线程类对象的时候
	 * 	会出现异常。
	 */
	public void start() {
		try {
			//设置服务端的tcp-serverSocket  接收部分。
			ss = new ServerSocket(8888);
		}catch(BindException e) {
			System.out.println("端口使用中。。。。");
			System.out.println("三秒钟之后将退出程序。。。");
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
			
			System.exit(0);
		}catch(IOException ff) {
			
			ff.printStackTrace();
		}
		//设置服务端的tcp-serverSocket  接收部分。
		//循环接收： 
		try {
			startFlag = true ;
			while(startFlag) {
				Socket sk = ss.accept();
				linkNum1++ ;
System.out.println("A Client connected.");				
				//多线程处理每一个客户端的传输到输出流中的数据
				//使用类属性输入流进行readUTF(String str)的阻塞式的读取的方法，由于是阻塞式的，每一个线程不会影响其他线程的读取的功能是实现。
				Client cl = new Client(sk);
				clients.add(cl);
				Thread th = new Thread(cl);
				th.start();
				
			}	
		}catch(SocketException ff) {
			System.out.println("连接阻塞方法抛出异常。");
			System.out.println("服务端程序也已经关闭。");
		}	
		 catch (IOException e) {
			System.out.println("有io异常");
		}
	}
	//内部类实现每一客户端阻塞式的读取数据的功能。不会对其他的线程造成影响 。
	
	@SuppressWarnings("unused")
	private  class Client implements Runnable{
		//需要设置每一个的线程对象 ，没有仅仅使用外部包装类中唯一的线程实例化的对象。
		private Socket s ;
		
		//需要再设置一个循环的条件，控制循环的读取数据功能的，判断的条件。
		private boolean readFlag  =false ;
		
		private DataInputStream dis = null ;
		
		private DataOutputStream dos  = null ;
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}
		
		
		//构造方法完成是实例化。
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
				//不能每一次都创建输入流，应该将初始化的是实现，放在构造方法中完成。这样重用的功能实现，可以节省内存空间。
				
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
						System.out.println("连接阻塞方法抛出异常。");
						System.out.println("服务端程序也已经关闭。");
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("有io异常。");
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
