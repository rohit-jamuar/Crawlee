##Crawlee

Simple Web Crawler
  * Crawls over the list of URLs provided. 
  * Fetches links from the DOM while crwaling, adds them to the list of URLs to be crawled. 
  * The crawling process continues till the **maximum_number_links_to_crawl**. 
  * The crawler also dumps crawled URLs' document to local disk.

#### External Dependency:
  * [Jsoup](http://jsoup.org/download)

#### How to run:
  1. Create a newline-separated text-file of URLs that you want crawled.
  2. Include Jsoup's jar in build path.
  3. Compile _Crawl.java_ using __javac__.
  4. Provide following command-line arguments at time of run 
     
     `<name_of_text_file> <maximum_number_links_to_crawl>`
