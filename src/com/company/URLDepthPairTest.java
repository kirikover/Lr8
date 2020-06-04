package com.company;

import java.net.MalformedURLException;
import java.io.IOException;

public class URLDepthPairTest {

    public static void main (String[] args) {


        String[] strs = { "http://123", "https://123", "http:///123", "http:/", "1http://", "http:", "http//" };
        int[] depth = {-1, 5, 5000};
        boolean[] answers = {true, false, true, false, false, false, false };
        int len = strs.length;
        int lenNum = depth.length;


        int fails = 0;
        for (int i = 0; i < len; i++) {
            boolean testAnswer = URLDepthPair.isHttpPrefixInURL(strs[i]);
            if (testAnswer != answers[i]) {
                System.out.println("Fail static method on: " + strs[i]);
                fails++;
            }
        }

        if (fails == 0) {
            System.out.println("Static method isHttpPrefixInURL() is success");
        }
        else {
            System.out.println("Static method isHttpPrefixInURL() is fail");
        }
        System.out.println("");

        for (int i = 0; i < len; i++) {
            URLDepthPair u;
            for (int j = 0; j < lenNum; j++) {
                try {

                    u = new URLDepthPair(strs[i], depth[j]);
                    System.out.println(strs[i] + " + " + depth[j] + " success");

                } catch (MalformedURLException ex) {
                    System.out.println(strs[i] + " + " + depth[j] + " called MalformedURLException");
                } catch (IllegalArgumentException e) {
                    System.out.println(strs[i] + " + " + depth[j] + " called IllegalArgumentException");
                }
            }
        }

        System.out.println("\nConstructor tester finished");

        System.out.println("\nTester finished");
    }
}

