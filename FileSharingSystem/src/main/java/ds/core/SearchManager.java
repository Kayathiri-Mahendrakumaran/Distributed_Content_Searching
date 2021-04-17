package  ds.core;

import  ds.utils.ConsoleTable;
import  ds.Handlers.QueryHitHandler;

import java.util.*;

class SearchManager {

    private MessageBroker messageBroker;
    public static final int SEARCHING_TIMEOUT = 3000;
    private Map<Integer, Result> fileDownloadOptions;

    SearchManager(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    int searchFiles(String keyword) {

        Map<String, Result> searchResults
                = new HashMap<String, Result>();

        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();
        queryHitHandler.setSearchResult(searchResults);
        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

        this.messageBroker.do_Search(keyword);

        System.out.println("Searching Please wait ...");

        try {

            Thread.sleep(SEARCHING_TIMEOUT);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        print_search_results_table(searchResults);
        this.clearSearchResults();
        return fileDownloadOptions.size();
    }


    private void clearSearchResults() {
        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();

        queryHitHandler.setSearchResult(null);
    }

    private void print_search_results_table(Map<String, Result> searchResults) {

        System.out.println("\nFile search matching results : ");

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("User SelectOption");
        headers.add("FileName");
        headers.add("SourceIP");
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
            row1.add(searchResult.get_FileName());
            row1.add(searchResult.get_Address() + ":" + searchResult.get_Port());
            row1.add("" + searchResult.get_TimeElapsed());
            row1.add("" + searchResult.get_Hops());

            content.add(row1);

            fileIndex++;
        }

        if (fileDownloadOptions.size() == 0) {
            System.out.println("No files matched the search!!!");
            return;
        }

        ConsoleTable consoleTable = new ConsoleTable(headers, content);
        consoleTable.printTable();

    }

    public Result get_file_details(int fileIndex) {
        return this.fileDownloadOptions.get(fileIndex);
    }
}
