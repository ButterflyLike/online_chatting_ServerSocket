package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

@SuppressWarnings("serial")
public class ChatClient extends Frame{
	//因为涉及到聊天的交互数据，所以设置成实例化的属性
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
	
		
		//TextField,有不同的构造器，具体看api文档，可以设置初始化的显示字符串，也可以规定列数，还有一指定初始化字符串和列数。
		tf = new TextField();
		//TextArea，多行文本框，可以作为输入，也可以做输出的显示。重载的构造器方法，可以指明行数和列数。
		ta = new TextArea();
		this.add(tf, BorderLayout.SOUTH);
		this.add(ta, BorderLayout.NORTH);
		//包围式的将组建显示出来。
		this.pack();
		
		connectServer();
		//匿名内部类，使用适配器为窗口设置关闭功能的监听器。
		//设置监听器：可以使用内部类。也可以使用外部类。还可以使用匿名内部类（逻辑简单的实现，所以这次使用这种额匿名的内部类。）
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//关闭：g)version0.4 应该在关闭整个窗口之前将所有的系统资源关闭。
				disconnect();
				System.exit(0);
			}	
		});
		
		
		new Thread(new ReceThread()).start();
		tf.addActionListener(new ActionMonitor());
	
		this.setVisible(true);
		
		//将textField中输入的东西，先考虑怎么样将它显示在textArea中。
		//textFidld里面有设置监听器中的方法。因为设置监听器有三种方法，下面这一种是匿名内部类的实现的方法。
		//还可以考虑将它设置成为私有的内部类的形式。
//		tf.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//			//getSource():The object on which the Event initially occurred属于eventObject类中的方法。
//			TextField  TF = (TextField)e.getSource();
//			//textArea这个类对象可以设置显示的字符串的setText()的方法 。
//			ta.setText(TF.getText());
//			//按下enter回车键，之后将textField的内容设置为空。
//			TF.setText(null);
//			}
//		});	
	}
	

	//g)version0.4   定义关闭方法。在launchFrame()的方法中，窗口关闭匿名内部类中的窗口关闭之前，调用这个关闭资源的方法。
	public void disconnect() {
		try {
			dos.close();
			dis.close();
			socket.close();
		}catch(SocketException ee) {
			System.out.println("发生了SocketException的异常！");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	//连接
	public void connectServer() {
		//连接客户端。现在设置成一旦启动窗口就要连接客户端，最好为了保证窗口程序的简洁性，最好将连接的操作定义成一个独立的方法中。
		try {
			//f)将Socket ss设置成外部的包装类的实例化的属性。
			socket = new Socket("127.0.0.1",8888);
			flag = true ;
			//socket.close(); 注意分步骤完成的时候，一定不能关闭这个socket对象，因为输出到服务端的时候，这个socket对象还是要使用的。
		//g)version0.4  连接成功之后，就直接将输出流实例化对象。
			
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
			//其实不够完善，因为我们只能看到最后一次的输入，之前的输入的信息都没有了。先这样写，后来在进行改进。
			//ta.setText(input);
			tf.setText("");
			
			//f)这一步的开发完成的任务是：通过socket对象将输入到textField的字符串，输出到服务器端的窗口中。
			//这时我们需要使用外部的包装类中launchFrame()方法的局部变量，这个函数没有返回值，调用这个函数结束之后，存在于栈内存中的socket的第对象就会被清除。
			//为了解决这个问题：需要将socket设置成实例化的对象。
			try {
				//给socket套接上一个字节输出流，然后套接上一个处理流DataOutputStream 这样做的好处是：
				//可以直接写入各种的数据类型的数据到输出流中。writeUTF(String strs),将字符串以utf-16的编码方式写入到输出流中。
				//也可以使用使用其他的字符输出流。
				//g)version0.4  将这个输出流定义成可以重复使用的，可以节省内存空间。
				//DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(input);
				dos.flush();
				//dos.close(); g)version0.4 输出流也先别关闭，也应该在一个控制方法中，将这个输出流关闭。
				//socket.close();g)version0.4。不能关闭，想要循环的输入字符串，获得输入字符串到textArea和服务端的consle，那么就不能将这个socket关闭，连接失败就不能首发数据了，应该协写一个独立的方法。
				
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
					System.out.println("socket 关闭了，socket.getInputStream方法不能使用了。");
				}catch(EOFException EOF) {
					System.out.println("读不到字符串数据了，因为这个客户端退出了！");
				}
				catch (IOException e) {
					e.printStackTrace();
				}	
			}	
		}	
	}
}

