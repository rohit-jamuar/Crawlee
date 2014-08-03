##Crawlee

Simple Web Crawler
  * Crawls over the list of URLs provided (via **name_of_text_file**). 
  * Fetches hyperlinks from DOM, adds them to the list of URLs to be crawled over. 
  * The crawling process continues till **maximum_number_links_to_crawl** is reached. 
  * The crawler also dumps crawled URLs' document (HTML) to local disk.

#### External Dependency:
  * [Jsoup](http://jsoup.org/download)

#### How to run:
  1. Create a newline-separated text-file of URLs that you want crawled.
  2. Include Jsoup's jar in build path.
  3. Compile _Crawl.java_ using __javac__.
  4. Provide following command-line arguments at time of run 
     
     `<name_of_text_file> <maximum_number_links_to_crawl>`
