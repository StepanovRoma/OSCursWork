package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

    private static final int PORT = 8080;
    public static LinkedList<ServerConnect> serverList = new LinkedList<ServerConnect>();
    public static Story story;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        story = new Story();
        System.out.println("Server started");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerConnect(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}

class Story {
    private LinkedList<String> story = new LinkedList<>();

    public void addStoryE1(String el) {
        if (story.size() >= 10) {
            story.removeFirst();
            story.add(el);
        } else {
            story.add(el);
        }
    }

    public void printStory(BufferedWriter writer) {
        if (story.size() > 0) {
            try {
                writer.write("History messages" + "\n");
                for (String vr : story) {
                    writer.write(vr + "\n");
                }
                writer.write("/...." + "\n");
                writer.flush();
            } catch (IOException ignored) {}
        }
    }
}

class ServerConnect extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter  out;

    public ServerConnect(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        Server.story.printStory(out);
        start();
    }
    @Override
    public void run() {
        String word;
        try {
            try {
                while (true) {
                    word = in.readLine();
                    if (word.equals("exit")) {
                        this.downService();
                        break;
                    }
                    System.out.println("log: " + word);
                    Server.story.addStoryE1(word);
                    for (ServerConnect vr : Server.serverList) {
                        vr.send(word);
                    }
                }
            } catch (NullPointerException ignored) {}
        } catch (IOException e) {
            this.downService();
        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    private void downService() {
        try {
           if (!socket.isClosed()) {
               socket.close();
               in.close();
               out.close();
               for (ServerConnect vr : Server.serverList) {
                   if (vr.equals(this)) {
                       vr.interrupt();
                       Server.serverList.remove(this);
                       break;
                   }
               }
           }
        } catch (IOException ignored) {}
    }
}
