package com.ut.data.dataSource.remote.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClient extends ClientBase{
	
	private DatagramSocket datagramSocket;
	private int destPort;
	private String destAddress;
	private byte[] buf = new byte[1024];
	private volatile boolean isStop;

	private static final String BROADCAST_ADDRESS = "255.255.255.255";
	
	public UDPClient(String destAddress, int destPort) {
		super();

		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		this.destAddress = destAddress;
		this.destPort = destPort;
		
		open();
	}

	public UDPClient(int destPort) {
		this(null, destPort);
	}

	@Override
	public boolean isUseBroadcast() {
		return "255.255.255.255".equals(destAddress);
	}

	public void send(byte[] msg) throws IOException {
		DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(destAddress), destPort);
		datagramSocket.send(datagramPacket);
	}

	@Override
	public void broadcastSend(byte[] msg) throws IOException {
		DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(BROADCAST_ADDRESS), destPort);
		datagramSocket.send(datagramPacket);
	}

	public void open() {
		
		isStop = false;
		
		receiveExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				while (!isStop) {
					try {
						DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
						datagramSocket.receive(datagramPacket);

						if (receiveListener != null) {
							final byte[] msg = new byte[datagramPacket.getLength()];
							System.arraycopy(datagramPacket.getData(), 0, msg, 0, datagramPacket.getLength());

							if (handleExecutor.isShutdown()) {
								return;
							}

							handleExecutor.execute(new Runnable() {

								@Override
								public void run() {

									receiveListener.onReceive(msg);
								}
							});

						}
						
					} catch (IOException e) {
						e.printStackTrace();

					}
				}
				
			}
		});
	}

	public int getDestPort() {
		return destPort;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}

	public String getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(String destAddress) {
		this.destAddress = destAddress;
	}
	
	public void close() {
		isStop = true;
		super.close();
	}

}
