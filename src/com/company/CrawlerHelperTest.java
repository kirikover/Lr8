package com.company;

public class CrawlerHelperTest {

    public static void main (String[] args) {

        CrawlerHelper helper = new CrawlerHelper();

        String testURL = "http://users.cms.caltech.edu/~donnie/cs11/java/lab2/index.html";
        String getURL = helper.cutURLEndFormat(testURL);
        System.out.println("Before cut: [" + testURL + "]");
        System.out.println("After cut: [" + getURL + "]");
        System.out.println("");

        testURL = "http://users.cms.caltech.edu/~donnie/cs11/java/lab2/";
        getURL = helper.cutURLEndFormat(testURL);
        System.out.println("Before cut: [" + testURL + "]");
        System.out.println("After cut: [" + getURL + "]");
        System.out.println("");

        testURL = "<a href=\"../javastyle.html\">CS11 Java Style Guidelines</a>.  There is even a --> [../javastyle.html] --> 11, 28";
        getURL = helper.getURLFromHTMLTag(testURL);
        System.out.println("Before cut: [" + testURL + "]");
        System.out.println("After cut: [" + getURL + "]");
        System.out.println("");

        testURL = "   <a href=\"../\"";
        getURL = helper.getURLFromHTMLTag(testURL);
        System.out.println("Before cut: [" + testURL + "]");
        System.out.println("After cut: [" + getURL + "]");
        System.out.println("");

        testURL = "http://users.cms.caltech.edu/~donnie/cs11/java/lab2/index.html";
        getURL = CrawlerHelper.urlFromBackRef(testURL, "../javastyle.html");
        System.out.println("Before glue: [" + testURL + "] + [../javastyle.html]");
        System.out.println("After glue: [" + getURL + "]");
        System.out.println("");

        testURL = "http://java.sun.com/javase/6/docs/api/java/lang/Boolean.html#parseBoolean(java.lang.String)";
        getURL = CrawlerHelper.cutTrashAfterFormat(testURL);
        System.out.println("Before cutting trash: [" + testURL + "]");
        System.out.println("After cutting trash: [" + getURL + "]");
        System.out.println("");


        testURL = "http://download.oracle.com/javase/tutorial/essential/concurrency/sync.html";
        System.out.println(" protocol inside : [" + testURL + "]");
        System.out.println("True answer: [https], test answer: [" + getURL + "]");
        System.out.println("");

    }
}
