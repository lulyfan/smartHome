package com.ut.data.dataSource.remote.bluetooth;

import com.ut.data.dataSource.remote.udp.ClientBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BleHelper implements ClientBase.ReceiveListener {
	private ClientBase client;
	private Map<Byte, LinkedList<SendedTask>> sendedTaskMap = new HashMap<>();
	private ScheduledExecutorService executorService;
	private byte[] srcMac;
	private Object lock = new Object();
	private long lastSendTime;
	private String bleKey;        //蓝牙锁管理密码

	private static final int TIMEOUT = 500;
	private static final int GAP_TIME = 50;

	private static BleHelper bleHelper;

	public synchronized static BleHelper getInstance(ClientBase client) {

		if (client == null) {
			return null;
		}

		if (bleHelper != null) {
			bleHelper.close();
		}

		bleHelper = new BleHelper(client);
		return bleHelper;

	}

	public static BleHelper getInstance() {
		return bleHelper;
	}

	private BleHelper(ClientBase client) {
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

	public void asyncSend(final BleMsg msg, final ResponseListener listener) {

		if (executorService.isShutdown()) {
			return;
		}

		int delay = 0;

		synchronized (lock) {
			long now = System.currentTimeMillis();             //目的是为了让蓝牙传输的每个数据之间存在最小的间隔时间
			if (now - lastSendTime < GAP_TIME) {
				delay = GAP_TIME;
			}

			lastSendTime = now + delay;

			executorService.schedule(new Runnable() {
				@Override
				public void run() {
					send(msg, listener);
				}
			}, delay, TimeUnit.MILLISECONDS);
		}
	}

	public void asyncSend(final BleMsg msg) {

		if (executorService.isShutdown()) {
			return;
		}

		int delay = 0;

		synchronized (lock) {
			long now = System.currentTimeMillis();             //目的是为了让蓝牙传输的每个数据之间存在最小的间隔时间
			if (now - lastSendTime < GAP_TIME) {
				delay = GAP_TIME;
			}

			lastSendTime = now + delay;

			executorService.schedule(new Runnable() {
				@Override
				public void run() {
					send(msg);
				}
			}, delay, TimeUnit.MILLISECONDS);
		}
	}

	private void send(BleMsg msg, ResponseListener listener) {
		if (client == null) {
			return;
		}

		final byte code = msg.getCode();
		ScheduledFuture scheduledFuture = executorService.schedule(new Runnable() {
				@Override
				public void run() {
					LinkedList<SendedTask> tasks = sendedTaskMap.get(code);

					if (tasks.size() > 0) {
						SendedTask task = tasks.remove();
						if (tasks.size() == 0) {
							sendedTaskMap.remove(code);
						}

						ResponseListener listener = task.responseListener;
						BleMsg sendMsg = task.sendmsg;

						if (listener != null) {
							listener.timeout(sendMsg);
						}
					}
				}
		}, TIMEOUT, TimeUnit.MILLISECONDS);

		SendedTask sendedTask = new SendedTask(msg, scheduledFuture);
		sendedTask.setResponseListener(listener);

		if (sendedTaskMap.containsKey(msg.getCode())) {
			LinkedList<SendedTask> tasks = sendedTaskMap.get(msg.getCode());
			tasks.add(sendedTask);
		} else {
			LinkedList<SendedTask> tasks = new LinkedList();
			tasks.add(sendedTask);
			sendedTaskMap.put(msg.getCode(), tasks);
		}

		send(msg);

	}

	private void send(BleMsg msg) {
		try {
			client.send(msg.encode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnect() {
		return client.isConnect();
	}
	
	public void close() {
		if (client != null) {
			client.close();
			client = null;
		}
		bleHelper = null;
	}

	public byte[] getSrcMac() {
		return srcMac;
	}

	public void setSrcMac(byte[] srcMac) {
		this.srcMac = srcMac;
	}

	@Override
	public void onReceive(byte[] msg) {

		BleMsg responseMsg = BleMsg.decode(msg);
		if (responseMsg == null) {
			System.out.println("decode fail");
			return;
		}
		
		byte code = responseMsg.getCode();
		LinkedList<SendedTask> tasks = sendedTaskMap.get(code);
		
		if (tasks != null && tasks.size() > 0) {
			
			SendedTask sendTask = tasks.getFirst();
			boolean isResponseError = responseMsg.isResponseError();

			tasks.remove(sendTask);
			if (tasks.size() == 0) {
				sendedTaskMap.remove(code);
			}

			sendTask.scheduledFuture.cancel(true);
			sendTask.scheduledFuture = null;

			if (sendTask.responseListener != null) {
				BleMsg sendedMsg = sendTask.sendmsg;

				if (!isResponseError) {
					sendTask.responseListener.onACk(sendedMsg, responseMsg);
				} else {
					sendTask.responseListener.onNAk(sendedMsg);
				}
			}
		}
		
		if (receiveListener != null) {
			receiveListener.onReceive(responseMsg);
		}
	}

	public String getBleKey() {
		return bleKey;
	}

	public void setBleKey(String bleKey) {
		this.bleKey = bleKey;
	}

	private ReceiveListener receiveListener;

	public void setReceiveListener(ReceiveListener receiveListener) {
		this.receiveListener = receiveListener;
	}

	public interface ReceiveListener {
		void onReceive(BleMsg msg);
	}

	public interface ResponseListener {
		void onACk(BleMsg sendMsg, BleMsg responseMsg);
		void onNAk(BleMsg sendMsg);
		void timeout(BleMsg sendMsg);
	}
	
	public static class SendedTask {
		BleMsg sendmsg;
		ScheduledFuture scheduledFuture;
		private ResponseListener responseListener;
		
		public SendedTask(BleMsg sendmsg, ScheduledFuture scheduledFuture) {
			this.sendmsg = sendmsg;
			this.scheduledFuture = scheduledFuture;
		}

		public void setResponseListener(ResponseListener responseListener) {
			this.responseListener = responseListener;
		}
	}
}
