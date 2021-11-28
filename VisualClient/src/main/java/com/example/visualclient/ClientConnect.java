package com.example.visualclient;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.visualclient.HelloController;

class ClientConnect {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputUser;
    private String addr;
    private int port;
    private String nickname;
    private Date time;
    private String dtime;
    private SimpleDateFormat dt1;
    private HelloController helloController;
    public boolean flag = false;

    public ClientConnect(String addr, int port, String nickname, HelloController helloController) {
        this.helloController = helloController;
        this.addr = addr;
        this.port = port;
        this.nickname = nickname;
        try {
            this.socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
            System.exit(0);
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            new ReceiveMsg().start();
            out.write(this.nickname + " connected\n");
            //new SendMsg().start();
        } catch (IOException e) {
            ClientConnect.this.downService();
        }
        flag = true;
    }

    public void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    private class ReceiveMsg extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    helloController.appendMsg(str);
                    //System.out.println(str);
                }
            } catch (IOException e) {
                ClientConnect.this.downService();
            }
        }
    }

    public void send(String str) {
        try {
            time = new Date();
            dt1 = new SimpleDateFormat("HH:mm:ss");
            dtime = dt1.format(time);
            out.write("(" + dtime + ") " + nickname + ": " + str + "\n");
            out.flush();
        } catch (IOException e) {
            ClientConnect.this.downService();
        }
    }

    public class SendMsg extends Thread {
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    userWord = inputUser.readLine();
                    send(userWord);
                } catch (IOException e) {
                    ClientConnect.this.downService();
                }
            }
        }
    }
}
