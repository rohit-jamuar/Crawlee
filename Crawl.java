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
     * It extracts the host-name from URL, which is then used by code to make sure that URLs (being crawled over)
     have different hosts.
     * */
    public static String extractName(String urlName)
    {
    	String remove[] = {"http://", "https://", "ftp://"};
    	String splitCharacters[] = {"/", ":"};
    	
    	for (String str : remove)
        urlName = urlName.replaceAll(str, "");
      for (String str : splitCharacters)
        urlName = urlName.split(str)[0];
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
     * fetchLinks (String inputURL) - Fetches all the links present in document 'inputDoc'.
     * */
    public static ArrayList<String> fetchLinks (Document inputDoc)
    {
        ArrayList<String> urlList = new ArrayList<String>();
        Elements links = inputDoc.select("a[href]");
        
        for(Element e: links)
            urlList.add(e.attr("abs:href"));
        
        return urlList;
    }
    
    
    /**
     * @param args
     * args[0] -- name of file
     * args[1] -- maximum number of URLs to be crawled
     */
    public static void main(final String args[])
    {
        if (args.length == 2 && fileExists(args[0]))
        {
            int maxURLsToBeStored = Integer.parseInt(args[1]);
            HashSet<String> urlRef = new HashSet<String>();
            Queue<String> urlQueue = new LinkedList<String>();
            
            BufferedReader br = null;                                
            try 
            {
                String currentLine;
                br = new BufferedReader(new FileReader(args[0]));
                
                while ((currentLine = br.readLine()) != null && urlRef.size() < maxURLsToBeStored)
                {
                    currentLine = currentLine.replaceAll("\\s","");
                    urlQueue.add(currentLine);
                    
                    while (urlQueue.size() > 0 && urlRef.size() < maxURLsToBeStored)
                    {
                        String urlToBeProcessed = urlQueue.remove();
                        String processedURL = extractName(urlToBeProcessed); 
                        
                        if (!urlRef.contains(processedURL))
                        {
                            Document currentURLDocument = getDocument(urlToBeProcessed);
                            if (currentURLDocument != null && htmlWriter(currentURLDocument, processedURL+".html"))
                            {
                            	urlRef.add(processedURL);
                                
                            	for (String link : fetchLinks(currentURLDocument))
                            		if (link.length() > 1) urlQueue.add(link);
                            	
                            	System.out.println("Processed -- " + urlToBeProcessed);
                            }
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
