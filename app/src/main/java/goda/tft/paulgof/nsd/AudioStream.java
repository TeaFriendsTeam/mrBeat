package goda.tft.paulgof.nsd;

/**
 * Created by paulgof on 11.04.17.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AudioStream {

    private Handler updateHandler;
    private Socket socket;
    private int port = -1;

    AudioClient audioClient;
    AudioServer audioServer;

    public AudioStream(Handler handler) {
        updateHandler = handler;
        audioServer = new AudioServer(handler);
    }

    public void tearDown() {
        audioServer.tearDown();
        audioClient.tearDown();
    }

    public void connectToServer(InetAddress address, int port) {
        audioClient = new AudioClient(address, port);
    }

    public void sendMessage(String msg) { //***
        if (audioClient != null) {
            audioClient.sendMessage(msg);
        }
    }

    public int getLocalPort() {
        return port;
    }

    public void setLocalPort(int port) {
        this.port = port;
    }


    public synchronized void updateMessages(String msg, boolean local) { //***



        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);
        updateHandler.sendMessage(message);

    }

    private synchronized void setSocket(Socket socket) {

        if (this.socket != null) {
            if (this.socket.isConnected()) {
                try {
                    this.socket.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
        this.socket = socket;
    }

    private Socket getSocket() {
        return socket;
    }

    private class AudioServer { //***
        ServerSocket mServerSocket = null;
        Thread mThread = null;

        public AudioServer(Handler handler) {
            mThread = new Thread(new ServerThread());
            mThread.start();
        }

        public void tearDown() {
            mThread.interrupt();
            try {
                mServerSocket.close();
            } catch (IOException ioe) {}
        }

        class ServerThread implements Runnable {

            @Override
            public void run() {

                try {
                    // Since discovery will happen via Nsd, we don't need to care which port is
                    // used.  Just grab an available one  and advertise it via Nsd.
                    mServerSocket = new ServerSocket(0);
                    setLocalPort(mServerSocket.getLocalPort());

                    while (!Thread.currentThread().isInterrupted()) {
                        setSocket(mServerSocket.accept());
                        if (audioClient == null) {
                            int port = socket.getPort();
                            InetAddress address = socket.getInetAddress();
                            connectToServer(address, port);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AudioClient { //***

        private InetAddress mAddress;
        private int PORT;


        private Thread mSendThread;
        private Thread mRecThread;

        public AudioClient(InetAddress address, int port) {

            this.mAddress = address;
            this.PORT = port;

            mSendThread = new Thread(new SendingThread());
            mSendThread.start();
        }

        class SendingThread implements Runnable {

            BlockingQueue<String> mMessageQueue;
            private int QUEUE_CAPACITY = 10;

            public SendingThread() {
                mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
            }

            @Override
            public void run() {
                try {
                    if (getSocket() == null) {
                        setSocket(new Socket(mAddress, PORT));

                    }

                    mRecThread = new Thread(new ReceivingThread());
                    mRecThread.start();

                } catch (UnknownHostException e) {
                } catch (IOException e) {
                }

                while (true) {
                    try {
                        String msg = mMessageQueue.take();
                        sendMessage(msg);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }

        class ReceivingThread implements Runnable {

            @Override
            public void run() {

                BufferedReader input;
                try {
                    input = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    while (!Thread.currentThread().isInterrupted()) {

                        String messageStr = null;
                        messageStr = input.readLine();
                        if (messageStr != null) {
                            updateMessages(messageStr, false);
                        } else {
                            break;
                        }
                    }
                    input.close();

                } catch (IOException e) {
                }
            }
        }

        public void tearDown() {
            try {
                getSocket().close();
            } catch (IOException ioe) {
            }
        }

        public void sendMessage(String msg) { //***
            try {
                Socket socket = getSocket();
                if (socket == null) {
                } else if (socket.getOutputStream() == null) {
                }



                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(getSocket().getOutputStream())), true);
                out.println(msg);
                out.flush();
                updateMessages(msg, true);
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            } catch (Exception e) {
            }
        }
    }

}
