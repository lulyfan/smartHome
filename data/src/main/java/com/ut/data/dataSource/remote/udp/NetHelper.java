package com.ut.data.dataSource.remote.udp;

import android.util.SparseArray;

import com.ut.data.dataSource.remote.udp.cmd.Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NetHelper implements ClientBase.ReceiveListener {
	private ClientBase client;
	private Map<Byte, LinkedList<SendedTask>> sendedTaskMap = new HashMap<>();
	private ScheduledExecutorService executorService;
	private byte[] srcMac;
	
	private static final int TIMEOUT = 1500;

	private static NetHelper netHelper;

	public synchronized static NetHelper getInstance(ClientBase client) {

		if (client == null) {
			return null;
		}

		if (netHelper != null) {
			netHelper.stop();
		}

		netHelper = new NetHelper(client);
		return netHelper;
	}

	public static NetHelper getInstance() {
		return netHelper;
	}

	private NetHelper(ClientBase client) {
		this.client = client;
		client.setReceiveListener(this);
		executorService = client.handleExecutor;
	}

	public ClientBase getClient() {
		return client;
	}

	public void setClient(ClientBase client) {
		this.client = client;
	}

	public void asyncSend(final Msg msg, final ResponseListener listener) {

		if (executorService.isShutdown()) {
			return;
		}

		executorService.execute(new Runnable() {
			@Override
			public void run() {
				send(msg, listener);
			}
		});
	}

	public void asyncBroadcastSend(final Msg msg, final BroadcastResponseListener listener) {
		if (executorService.isShutdown()) {
			return;
		}

		executorService.execute(new Runnable() {
			@Override
			public void run() {
				broadcastSend(msg, listener);
			}
		});
	}

	private void send(Msg msg, ResponseListener listener) {
		if (client == null) {
			return;
		}

		final byte appCmd = msg.getAppCmd();
		ScheduledFuture scheduledFuture = executorService.schedule(new Runnable() {
				@Override
				public void run() {
					LinkedList<SendedTask> tasks = sendedTaskMap.get(appCmd);

					if (tasks.size() > 0) {
						SendedTask task = tasks.remove();
						if (tasks.size() == 0) {
							sendedTaskMap.remove(appCmd);
						}

						ResponseListener listener = task.responseListener;
						Msg sendMsg = task.sendmsg;

						if (listener != null) {
							listener.timeout(sendMsg);
						}
					}
				}
		}, TIMEOUT, TimeUnit.MILLISECONDS);

		SendedTask sendedTask = new SendedTask(msg, scheduledFuture);
		sendedTask.setResponseListener(listener);
		sendedTask.isBrodcastTask = false;

		if (sendedTaskMap.containsKey(msg.getAppCmd())) {
			LinkedList<SendedTask> tasks = sendedTaskMap.get(msg.getAppCmd());
			tasks.add(sendedTask);
		} else {
			LinkedList<SendedTask> tasks = new LinkedList();
			tasks.add(sendedTask);
			sendedTaskMap.put(msg.getAppCmd(), tasks);
		}

		try {
			client.send(msg.encode());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void broadcastSend(Msg msg, BroadcastResponseListener listener) {
		if (client == null) {
			return;
		}

		final byte appCmd = msg.getAppCmd();
		ScheduledFuture scheduledFuture = executorService.schedule(new Runnable() {
			@Override
			public void run() {
				LinkedList<SendedTask> tasks = sendedTaskMap.get(appCmd);

				if (tasks.size() > 0) {
					SendedTask broabcastTask = tasks.remove();
					if (tasks.size() == 0) {
						sendedTaskMap.remove(appCmd);
					}

					BroadcastResponseListener listener = broabcastTask.broadcastResponseListener;
					Msg sendMsg = broabcastTask.sendmsg;

					if (listener == null) {
						return;
					}

					if (broabcastTask.responseCount == 0) {
						listener.timeout(sendMsg);
					} else {
						listener.onACk(sendMsg, broabcastTask.ackMsgs);
						listener.onNAk(sendMsg, broabcastTask.nakCodes);
					}
				}
			}
		}, TIMEOUT, TimeUnit.MILLISECONDS);

		SendedTask sendedTask = new SendedTask(msg, scheduledFuture);
		sendedTask.setBroadcastResponseListener(listener);
		sendedTask.isBrodcastTask = true;
		sendedTask.ackMsgs = new ArrayList<>();
		sendedTask.nakCodes = new ArrayList<>();


		if (sendedTaskMap.containsKey(msg.getAppCmd())) {

			LinkedList<SendedTask> tasks = sendedTaskMap.get(msg.getAppCmd());
			SendedTask oldTask = tasks.removeFirst();
			oldTask.scheduledFuture.cancel(true);
			oldTask.scheduledFuture = null;
			tasks.add(sendedTask);

		} else {

			LinkedList<SendedTask> tasks = new LinkedList();
			tasks.add(sendedTask);
			sendedTaskMap.put(msg.getAppCmd(), tasks);
		}

		try {
			client.broadcastSend(msg.encode());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void stop() {
		if (client != null) {
			client.close();
		}
		netHelper = null;
	}

	public byte[] getSrcMac() {
		return srcMac;
	}

	public void setSrcMac(byte[] srcMac) {
		this.srcMac = srcMac;
	}

	@Override
	public void onReceive(byte[] msg) {

		Msg responseMsg = Msg.decode(msg);
		if (responseMsg == null) {
			System.out.println("decode fail");
			return;
		}
		
		byte appCmd = responseMsg.getAppCmd();
		LinkedList<SendedTask> tasks = sendedTaskMap.get(appCmd);
		
		if (tasks != null && tasks.size() > 0) {
			
			SendedTask sendTask = tasks.getFirst();
			sendTask.responseCount ++;

			int linkCmd = responseMsg.getLinkCmd() & 0xFF;

			if (sendTask.isBrodcastTask) {

				if (linkCmd == Info.LinkCMD.ACK) {
					sendTask.ackMsgs.add(responseMsg);
				} else if (linkCmd == Info.LinkCMD.NAK) {
					int nakCode = responseMsg.getContent()[0] & 0xFF;
					sendTask.nakCodes.add(nakCode);
				}

			} else {

				tasks.remove(sendTask);
				if (tasks.size() == 0) {
					sendedTaskMap.remove(appCmd);
				}

				sendTask.scheduledFuture.cancel(true);
				sendTask.scheduledFuture = null;

				if (sendTask.responseListener != null) {
					Msg sendedMsg = sendTask.sendmsg;

					if (linkCmd == Info.LinkCMD.ACK) {
						sendTask.responseListener.onACk(sendedMsg, responseMsg);
					} else if (linkCmd == Info.LinkCMD.NAK) {
						sendTask.responseListener.onNAk(sendedMsg, responseMsg.getContent()[0]);
					}
				}
			}
		}
		
		if (receiveListener != null) {
			receiveListener.onReceive(responseMsg);
		}
	}
	
	private ReceiveListener receiveListener;

	public void setReceiveListener(ReceiveListener receiveListener) {
		this.receiveListener = receiveListener;
	}

	public interface ReceiveListener {
		void onReceive(Msg msg);
	}

	public interface ResponseListener {
		void onACk(Msg sendMsg, Msg responseMsg);
		void onNAk(Msg sendMsg, int nakCode);
		void timeout(Msg sendMsg);
	}

	public interface BroadcastResponseListener {
		void onACk(Msg sendMsg, List<Msg> responseMsgs);
		void onNAk(Msg sendMsg, List<Integer> nakCodes);
		void timeout(Msg sendMsg);
	}
	
	public static class SendedTask {
		boolean isBrodcastTask;
		int responseCount;                                //收到的回复数量，发送广播可能收到多个回复，否则一般为一个回复
		List<Msg> ackMsgs;
		List<Integer> nakCodes;
		Msg sendmsg;
		ScheduledFuture scheduledFuture;
		private ResponseListener responseListener;
		private BroadcastResponseListener broadcastResponseListener;
		
		public SendedTask(Msg sendmsg, ScheduledFuture scheduledFuture) {
			this.sendmsg = sendmsg;
			this.scheduledFuture = scheduledFuture;
		}

		public void setResponseListener(ResponseListener responseListener) {
			this.responseListener = responseListener;
		}

		public void setBroadcastResponseListener(BroadcastResponseListener broadcastResponseListener) {
			this.broadcastResponseListener = broadcastResponseListener;
		}
	}
}
