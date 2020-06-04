package com.company;

import java.lang.Exception;
import java.util.*;
import java.net.MalformedURLException;
import java.net.*;
import java.io.*;

public class Crawler {

    public static final int HTTP_PORT = 80;
    public static final String HOOK_REF = "<a href=\"";
    public static final String HOOK_HTTP = "<a href=\"http://";
    public static final String HOOK_HTTPS = "<a href=\"https://";
    public static final String HOOK_BACK = "<a href=\"../";
    public static final String BAD_REQUEST_LINE = "HTTP/1.1 400 Bad Request";



    public static final String testURL = "http://users.cms.caltech.edu/~donnie/cs11/java/";
    //public static final String testURL = "http://users.cms.caltech.edu/~donnie/cs11/java/lectures/cs11-java-lec1.pdf";
    public static final int testDepth = 1;


    LinkedList<URLDepthPair> notVisitedList;
    LinkedList<URLDepthPair> visitedList;

    int depth;

    public Crawler() {
        notVisitedList = new LinkedList<URLDepthPair>();
        visitedList = new LinkedList<URLDepthPair>();
    }



    public static void main (String[] args) {

        Crawler crawler = new Crawler();

        crawler.getFirstURLDepthPair(args);
        crawler.startParse();
        crawler.showResults();
        //crawler.testParse();
    }



    public void startParse() {
        System.out.println("Stating parsing:\n");

        URLDepthPair nowPage = notVisitedList.getFirst();

        while (nowPage.getDepth() <= depth && !notVisitedList.isEmpty()) {


            nowPage = notVisitedList.getFirst();

            Socket socket = null;

            try {

                socket = new Socket(nowPage.getHostName(), HTTP_PORT);
                System.out.println("Connection to [ " + nowPage.getURL() + " ] created!");
                try {
                    socket.setSoTimeout(5000);
                }
                catch (SocketException exc) {
                    System.err.println("SocketException: " + exc.getMessage());
                    moveURLPair(nowPage, socket);
                    continue;
                }

                CrawlerHelper.getInfoAboutUrl(nowPage.getURL(), true);


                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Отправка запроса на получение html-страницы
                out.println("GET " + nowPage.getPagePath() + " HTTP/1.1");
                out.println("Host: " + nowPage.getHostName());
                out.println("Connection: close");
                out.println("");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = in.readLine();

                if (line.startsWith(BAD_REQUEST_LINE)) {
                    System.out.println("ERROR: BAD REQUEST!");
                    System.out.println(line + "\n");

                    this.moveURLPair(nowPage, socket);
                    continue;
                } else {
                    System.out.println("REQUEST IS GOOD!\n");
                }


                System.out.println("---Start of file---");

                int strCount = 0;
                int strCount2 = 0;
                while(line != null) {
                    try {

                        line = in.readLine();
                        strCount += 1;

                        String url = CrawlerHelper.getURLFromHTMLTag(line);
                        if (url == null) continue;

                        // Если ссылка ведёт на сайт с протоколом https - пропускаем
                        if (url.startsWith("https://")) {
                            System.out.println(strCount2 + " --> " + strCount + " |  " + url + " --> https-refference\n");
                            continue;
                        }

                        if (url.startsWith("../")) {
                            String newUrl = CrawlerHelper.urlFromBackRef(nowPage.getURL(), url);
                            System.out.println(strCount2 + " --> " + strCount + " |  " + url + " --> " +  newUrl + "\n");
                            this.createURlDepthPairObject(newUrl, nowPage.getDepth() + 1);
                        }

                        else if (url.startsWith("http://")) {
                            String newUrl = CrawlerHelper.cutTrashAfterFormat(url);
                            System.out.println(strCount2 + " --> " + strCount + " |  " + url + " --> " + newUrl + "\n");
                            this.createURlDepthPairObject(newUrl, nowPage.getDepth() + 1);
                        }


                        else {
                            String newUrl;
                            newUrl = CrawlerHelper.cutURLEndFormat(nowPage.getURL()) + url;

                            System.out.println(strCount2 + " --> " + strCount + " |  " + url + " --> " + newUrl + "\n");
                            this.createURlDepthPairObject(newUrl, nowPage.getDepth() + 1);
                        }

                        strCount2 += 1;
                    }
                    catch (Exception e) {
                        break;
                    }
                }

                if (strCount == 1) System.out.println("No http refs in this page!");
                System.out.println("---End of file---\n");

                System.out.println("Page had been closed\n");

            }
            catch (UnknownHostException e) {
                System.out.println("Opps, UnknownHostException catched, so [" + nowPage.getURL() + "] is not workable now!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            moveURLPair(nowPage, socket);
            nowPage = notVisitedList.getFirst();
        }
    }


    private void moveURLPair(URLDepthPair pair, Socket socket) {
        this.visitedList.addLast(pair);
        this.notVisitedList.removeFirst();

        if (socket == null) return;

        try {
            // Закрытие сокета
            socket.close();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createURlDepthPairObject(String url, int depth) {

        URLDepthPair newURL = null;
        try{

            newURL = new URLDepthPair(url, depth);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        notVisitedList.addLast(newURL);
    }


    public LinkedList<URLDepthPair> getVisitedSites() {
        return this.visitedList;
    }

    public LinkedList<URLDepthPair> getNotVisitedSites() {
        return this.notVisitedList;
    }


    public void showResults() {
        System.out.println("---Rezults of working---");

        System.out.println("Scanner scanned next sites:");
        int count = 1;
        for (URLDepthPair pair : visitedList) {
            System.out.println(count + " |  " + pair.toString());
            count += 1;
        }

        System.out.println("");
        System.out.println("Not visited next sites (because of depth limit of some another reasons):");
        count = 1;
        for (URLDepthPair pair : notVisitedList) {
            System.out.println(count + " |  " + pair.toString());
            count += 1;
        }

        System.out.println("-----End of rezults-----");
    }




    public void getFirstURLDepthPair(String[] args) {
        CrawlerHelper help = new CrawlerHelper();
        URLDepthPair urlDepth = help.getURLDepthPairFromArgs(args);
        if (urlDepth == null) {
            System.out.println("Args are empty or have exception. Now you need to enter URL and depth manually!\n");
            urlDepth = help.getURLDepthPairFromInput();
        }

        this.depth = urlDepth.getDepth();
        urlDepth.setDepth(0);


        notVisitedList.add(urlDepth);
        System.out.println("First site: " + urlDepth.toString() + "\n");
    }



    private void testParse() {


        URLDepthPair pair;
        depth = testDepth;
        try {
            pair = new URLDepthPair(testURL, 0);
            notVisitedList.add(pair);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Start pair created!");


        URL url = null;
        try {
            url = new URL(pair.getURL());
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return;
        }

        System.out.println("URL formed");


        try {
            Socket socket = new Socket(url.getHost(), HTTP_PORT);
            System.out.println("Connection to [ " + url + " ] created!");

            CrawlerHelper.getInfoAboutUrl(url, true);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("GET " + url.getPath() + " HTTP/1.1");
            out.println("Host: " + url.getHost());
            out.println("Connection: close");
            out.println("");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            if (line.startsWith(BAD_REQUEST_LINE)) {
                System.out.println("ERROR: BAD REQUEST!");
                System.out.println(line + "\n");
            } else {
                System.out.println("REQUEST IS GOOD!\n");
            }
            System.out.println("---Start of file---");
            int strCount = 1;
            while(line != null) {
                try {


                    line = in.readLine();

                    if (line.indexOf(HOOK_HTTP) != -1)
                        System.out.println(strCount + " |  " + line);
                    else if (line.indexOf(HOOK_REF) != -1)
                    {
                        int indexStart = line.indexOf(HOOK_REF) + HOOK_REF.length();
                        int indexEnd = line.indexOf("\"", indexStart);
                        String subRef = line.substring(indexStart, indexEnd);
                        String fullSubRef = url + subRef;
                        URL newUrl = URLDepthPair.getUrlObjectFromUrlString(fullSubRef);
                        String newUrlProtocol = newUrl.getProtocol();

                        System.out.println(strCount + " |  " + line + " --> [" + subRef + "] --> " + indexStart + ", " + indexEnd);
                        System.out.println("Full ref = " + newUrl.toString() + ", protocol = " + newUrlProtocol + "\n");
                    }
                    strCount += 1;
                }
                catch (Exception e) {
                    break;
                }
            }
            if (strCount == 1) System.out.println("No http refs in this page!");
            System.out.println("---End of file---\n");
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + pair.getHostName());
            return;
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + pair.getHostName());
            return;
        }
    }
}
