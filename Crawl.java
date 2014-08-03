import java.io.IOException;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


class Crawl
{
    
    /*
     * fileExists(String fPath) - Checks whether a file exists at the path provided.
     * Returns true, if it exists, else false. 
     * */
    public static boolean fileExists(String fPath)
    {
        File f = new File(fPath);
        if(f.exists()) return true;
        else return false;
    }
    
    /*
     * extractName(String urlName) - Normalises a URL
     * It extracts the host-name from URL, which is then used by code to make sure that URLs being crawled are different hosts.
     * */
    public static String extractName(String urlName)
    {
        String remove[] = {"http://", "https://", "ftp://"};
        for (String str : remove)
            urlName = urlName.replaceAll(str, "");
        if (urlName.indexOf('/') != -1)
            urlName =  urlName.split("/")[0];
        urlName = urlName.replaceAll("[^a-zA-Z0-9]", "_");
        return urlName;
    }
    
	/*
	 * htmlWriter(Document doc, String fileName) - Writes the 'doc' object to file whose path to file named 'fileName'
	 * Before writing to file, it checks if a file with name 'fileName' exists at current location. If so, it checks 
	 * if the file is more than a day old. If it was modified (written to) more than a day back, the function
	 * overwrites the file with newly fetched content. If the file with name 'fileName' is not found, the function
	 * writes the content in a newly created file. 
	 * */
    public static boolean htmlWriter(Document doc, String fileName)
    {
        BufferedWriter bw = null;
        try 
        {    
            if (doc != null && fileName.compareTo(".html") != 0)
            {
                if(fileExists(fileName))
                {
                    File f = new File(fileName);
                    if (System.currentTimeMillis() - f.lastModified() < 86400000)
                        return true;
                }
                bw = new BufferedWriter(new FileWriter(fileName));
                bw.write(doc.toString());
                bw.close();
                return true;
            }
            else
                throw new IOException();
        } 
        catch (IOException e) 
        {
            return false;
        }
        finally
        {
            try
            {
                if (bw != null) bw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /*
     * getDocument(String inputURL) - Returns the document (HTML) of the URL 'inputURL'.
     * */
    public static Document getDocument(String inputURL)
    {
        try
        {
            if (inputURL.length() > 1)
                return Jsoup.connect(inputURL).get();
            else
                throw new IOException(); 
        }
        catch (IOException e)
        {
            return null;
        }
    }
    
    /*
     * fetchLinks (String inputURL) - Fetches all the links present on DOM of 'inputURL' URL 
     * */
    public static ArrayList<String> fetchLinks (String inputURL)
    {
        ArrayList<String> urlList = new ArrayList<String>();
        
        Document currentDocument = getDocument(inputURL);
        Elements links = currentDocument.select("a[href]");
        
        for(Element e: links)
            urlList.add(e.attr("abs:href"));
        
        return urlList;
    }
    
    /*
     * arg[0] -- file_name : contains a newline-separated list of URLs
     * arg[1] -- maximum number of URLs to be processed by Crawler
     * 
     * */
    public static void main(String args[])
    {
        if (args.length == 2 && fileExists(args[0]))
        {
            int maxURLsToBeStored = Integer.parseInt(args[1]);
            HashSet<String> url_ref = new HashSet<String>();
            Queue<String> url_queue = new LinkedList<String>();
            
            BufferedReader br = null;                                
            try 
            {
                String currentLine;
                br = new BufferedReader(new FileReader(args[0]));
                
                while ((currentLine = br.readLine()) != null && url_ref.size() < maxURLsToBeStored)
                {
                    currentLine = currentLine.replaceAll("\\s","");
                    
                    url_queue.add(currentLine);
                    
                    while (url_queue.size() > 0 && url_ref.size() < maxURLsToBeStored)
                    {
                        String urlToBeProcessed = url_queue.remove();
                        String processedURL = extractName(urlToBeProcessed); 
                        
                        if (!url_ref.contains(processedURL))
                        {
                            Document currentURLDocument = getDocument(urlToBeProcessed);
                            if (currentURLDocument != null && htmlWriter(currentURLDocument, processedURL+".html"))
                            {
                            	url_ref.add(processedURL);
                                
                            	for (String link : fetchLinks(urlToBeProcessed))
                                    url_queue.add(link);
                                
                            	System.out.println("Processed " + urlToBeProcessed);
                            }
                            else
                                System.out.println("Error!");
                        }
                    }
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (br != null) br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
            System.out.println("Invalid number of arguments passed!");    
    }
}
