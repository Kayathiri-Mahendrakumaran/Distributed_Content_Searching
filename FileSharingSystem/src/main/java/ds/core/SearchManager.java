package  ds.core;

import  ds.Constants;
import  ds.utils.ConsoleTable;
import  ds.Handlers.QueryHitHandler;

import java.util.*;

class SearchManager {

    private MessageBroker messageBroker;

    private Map<Integer, Result> fileDownloadOptions;

    SearchManager(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    int doSearch(String keyword) {

        Map<String, Result> searchResults
                = new HashMap<String, Result>();

        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();
        queryHitHandler.setSearchResult(searchResults);
        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

        this.messageBroker.doSearch(keyword);

        System.out.println("Please be patient till the file results are returned ...");

        try {
            Thread.sleep(Constants.SEARCH_TIMEOUT);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printSearchResults(searchResults);
        this.clearSearchResults();
        return fileDownloadOptions.size();
    }

    List<String> doUISearch(String keyword) {

        Map<String, Result> searchResults
                = new HashMap<String, Result>();

        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();
        queryHitHandler.setSearchResult(searchResults);
        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

        this.messageBroker.doSearch(keyword);

        System.out.println("Please be patient till the file results are returned ...");

        try {
            Thread.sleep(Constants.SEARCH_TIMEOUT);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> results = new ArrayList<String>();

        int fileIndex = 1;

        this.fileDownloadOptions = new HashMap<Integer, Result>();

        for (String s : searchResults.keySet()) {
            Result searchResult = searchResults.get(s);
            String temp = "" + searchResult.getFileName() + "\t" +
                    searchResult.getAddress() + ":" + searchResult.getPort() + "\t" +
                    searchResult.getHops() + "\t" + searchResult.getTimeElapsed() + "ms";
            this.fileDownloadOptions.put(fileIndex, searchResult);
            results.add(temp);
            fileIndex++;
        }

        this.clearSearchResults();

        return results;
    }

    private void clearSearchResults() {
        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();

        queryHitHandler.setSearchResult(null);
    }

    private void printSearchResults(Map<String, Result> searchResults) {

        System.out.println("\nFile search results : ");

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("Option No");
        headers.add("FileName");
        headers.add("Source");
        headers.add("QueryHit time (ms)");
        headers.add("Hop count");

        ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();

        int fileIndex = 1;

        this.fileDownloadOptions = new HashMap<Integer, Result>();

        for (String s : searchResults.keySet()) {
            Result searchResult = searchResults.get(s);
            this.fileDownloadOptions.put(fileIndex, searchResult);

            ArrayList<String> row1 = new ArrayList<String>();
            row1.add("" + fileIndex);
            row1.add(searchResult.getFileName());
            row1.add(searchResult.getAddress() + ":" + searchResult.getPort());
            row1.add("" + searchResult.getTimeElapsed());
            row1.add("" + searchResult.getHops());

            content.add(row1);

            fileIndex++;
        }

        if (fileDownloadOptions.size() == 0) {
            System.out.println("Sorry. No files are found!!!");

            return;
        }

        ConsoleTable ct = new ConsoleTable(headers, content);
        ct.printTable();

    }

    public Result getFileDetails(int fileIndex) {
        return this.fileDownloadOptions.get(fileIndex);
    }
}
