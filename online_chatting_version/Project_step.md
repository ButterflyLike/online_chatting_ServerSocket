 
1:首先创建客户端程序：
	a)显示窗口---》显示窗口，使用继承的思想，创建窗口会更加的灵活。直接new Frame()不够灵活。
	
	b)在设置好显示和关闭功能实现之后，----》考虑将textField输入的字符串，怎么显示到textArea中
	我们可以使用event响应，或者使用 button按键监听，然后再监听方法中显示我们的功能。
		---》使用内部类，为textFeild的属性，设置一个监听器。然后我们将textField输入的东西，显示到textArea中。
			后来要使用，网络tcp-socket得到交互的数据，（并且现在不够完善），因为没有历史记录的功能。
	c)设置tcp-socket的java服务器和客户端的连接。
		server:
		
			ServerSocket ss = new ServerSocket(int port);
			Socket sk = ss.accept()
		
		client:
			Socket ss = new Socket(String ip,int port);
			//一行代码连接到本机的tcp协议的socket的server端。
	d)为了保证客户端程序窗口代码的间接性，我们应该将连接到客户端的程序代码定义到一个新的方法中。 聊天代码的启动的顺序是，先启动服务器端的程序，然后启动客户端程序。
	e)小技巧：开发的过程中，我们应该做的是：将程序的分支成段的代码关闭，为了更好的观察代码。
	f)下一步实现：将客户端输入到textfield中的字符串，通过输出流，显示在服务器端的console控制台上。    有bug  ----》只能发一个字符串。
	
	
	g)version0.4  让服务器端的控制台能够循环多次的接收到客户端发过来的数据。
	1）首先将客户端的代码进行改动，  1.1:）将输出流的代码实例化写在一连接服务器就实例化。以后不用再实例化了。1.2)输出流不能随意的关闭，那么客户端的socket也就不能随意的关闭。逻辑是：定义一个关闭方法。然后只在关闭整个客户端窗口之前关闭所有的输出流的资源，断开socket连接。这样才能确保循环的输出到textArea和服务器的console控制台。
	2）循环的读取客户端写过来的数据。
	
	h) version0.5  一定要注意0.4的改进，特别是服务器端的改进。a)当客户端的socket关闭以后，流就不存在了，这样服务器端的流就会异常的关闭。为了处理这个异常，应该这个捕获处将这个异常进行处理。b)另外的一个bug，启动多个server端的时候，会出现异常，修复办法：直接退出。
	
	i)version0.6 。
		 bug_1启动多个客户端的时候，服务端没有连接，对后一个链接没有任何的输出和退出响应。
		bug_1 : 的原因：  String line = dis.readUTF();当多个客户端启动的时候，第一个连接client-server已经阻塞在读取的状态。没有办法响应其他的客户端的连接了。
		bug_1:解决的办法是： 多线程，将每一个client对象用一个线程接收，处理这个线程，那么即使发生了阻塞，他也不会对其他的线程的对象造成影响。   -------------》又犯了一个bug_1.1 : 每一个输入流应该属于特定线程的属性。因为它是引用的类型。bug:如果设置成类属性的话，那么这个输入流指向最后一个client堆内存中的DataOutStream 的实体。之前的都不能接收了。
		bug_2:当客户端断开连接，服务端应该关闭，否则，会一直占用serversocket的端口资源。 并且accept()造成了阻塞。
			解决办法： 定义server实例化属性，linknum 和closeNum 在所有的客户端关闭以后，将ServerSockeT关闭。这个时候accept（）会抛出异常。被最近的try catch 捕获。
			那么socket无法获取就会发生socketException
		
		version0.6  依然存在bug：   服务端不能区分哪一个客户端的连接显示 ， 哪一个客户端的发送到服务端的数据，应该在控制台的有区分的显示出来。
	
	g)需求：将每一个客户端发过来的数据，发送给每一个客户端。	因为所有的客户端都是连接同一个服务端。所以写进输出流的字符串数据，可以被每一个客户端取得。那么现象应该是：每一个客户端都会接收到所有其他客户端数据。
	实现：
		怎么发送？
		每一个线程读到的数据，发送给所有的客户端对象。使用容器把客户端对象保存起来，每一个读的线程中，由同一个socket的流连接到了一起。所以得到所有的client
	对象之后，在线程中实现。多线程的写操作。
		
		怎么接收？
			多线程接收，在每一个线程中使用DataInputStram进行接收。然后死循环接收，会比较容易发生bug.
				bug是：当我们关闭了一个客户端服务程序之后，socket，dis,dos属于这个客户端的资源就关闭了。应该在读取线程的read方法中，处理这些SocketException socket关闭了，那么socket.getInputStream()这个方法就不用了。

	readUTF()这个方法也就读取不到数据了，因为流不存在了。那么需要处理EOFexception。
		
	SocketException:
	
	Thrown to indicate that there is an error creating or accessing a Socket.
		
	
	EOFException :
	
	Signals that an end of file or end of stream has been reached unexpectedly during input.
	
	
	
	
	
	
	
	
	
	