package main.java.ds.core;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class FileHandler {
    private static FileHandler fileHandler;

    private Map<String, String> files_map;

    private String user_name;

    private String fileSeparator = System.getProperty("file.separator");
    private String rootDirectory;

    public static final String FILE_NAMES = "FileNames.txt";

    private final Logger LOG = Logger.getLogger(FileHandler.class.getName());

    private FileHandler(String userName) {
        files_map = new HashMap<>();

        this.user_name = userName;
        this.rootDirectory =   "." + fileSeparator + this.user_name;

        ArrayList<String> fullList = loadFileNames();

        Random r = new Random();

        // doubt
        for (int i = 0; i < 5; i++){
            files_map.put(fullList.get(r.nextInt(fullList.size())), "");
        }

        createAllFiles();
    }

    // singleton class
    public static synchronized FileHandler getInstance(String userName) {
        if (fileHandler == null) {
            fileHandler = new FileHandler(userName);

        }
        return fileHandler;
    }

    public boolean addFile(String fileName, String filePath) {
        this.files_map.put(fileName, filePath);
        return true;
    }

    private ArrayList<String> loadFileNames(){

        ArrayList<String> fileNames = new ArrayList<>();

        //Get file from main.resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (classLoader.getResourceAsStream(FILE_NAMES)));

        try {

            for (String line; (line = bufferedReader.readLine()) != null;) {
                fileNames.add(line);
            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    public String getFileNames() {
        String fileslist = "There are " + files_map.size() + " files=>\n";
        for (String s: files_map.keySet()) {
            fileslist += s + "\n";
        }
        return fileslist;
    }

    public void createAllFiles() {
        System.out.println("===========================");
        System.out.println("Total number of files is " + files_map.size());
        System.out.println("===========================");
        for (String fileName: files_map.keySet()) {
            System.out.println(fileName);
            try {
                String absoluteFilePath = this.rootDirectory + fileSeparator + fileName;
                File file = new File(absoluteFilePath);
                file.getParentFile().mkdir();
                if (file.createNewFile()) {
                    LOG.fine(absoluteFilePath + " Created");
                } else LOG.fine("File " + absoluteFilePath + " already exists");
                RandomAccessFile f = new RandomAccessFile(file, "rw");
                f.setLength(1024 * 1024 * 8);
            } catch (IOException e) {
                LOG.severe("File creation process failed");
                e.printStackTrace();
            }
        }
    }

    public File getFile(String fileName) {
        File file = new File(rootDirectory + fileSeparator + fileName);
        return file;
    }

    public Set<String> searchFile(String query) {
        String[] querySet = query.split(" ");

        Set<String> results = new HashSet<String>();

        for (String q: querySet){
            for (String key: this.files_map.keySet()){
                String[] fileNameSplit = key.split(" ");
                for (String f : fileNameSplit){
                    if (f.toLowerCase().equals(q.toLowerCase())){
                        results.add(key);
                    }
                }
            }
        }

        return results;
    }
}
