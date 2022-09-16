package com.golovanov.mapper;

import com.golovanov.entity.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class WebsiteMapper extends RecursiveTask<LinkedHashSet<WebPage>> {

    private volatile LinkedHashSet<WebPage> pages = new LinkedHashSet<>();

    private volatile ConcurrentHashMap<String, String> checkedLinks;

    private String path;

    private String root;

    public WebsiteMapper(ConcurrentHashMap<String, String> checkedLinks, String path, String root) {
        this.checkedLinks = checkedLinks;
        this.path = path;
        this.root = root;
    }

    private synchronized LinkedHashSet<String> getChildren(String path) {
        LinkedHashSet<String> urls = new LinkedHashSet<>();
        try {
            Document doc = Jsoup.connect(path).maxBodySize(0).timeout(10000).get();
            Elements elements = doc.select("a[href]");
            elements.stream()
                    .map(e -> e.attr("abs:href"))
                    .filter(e -> !e.equals(root))
                    .filter(e -> e.startsWith(root))
                    .filter(e -> !e.contains("#") && !e.contains("?") && !e.contains("'"))
                    .filter(e -> !e.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)"))
                    .forEachOrdered(urls::add);
            Thread.sleep(500);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return urls;
    }

    @Override
    protected LinkedHashSet<WebPage> compute() {
        synchronized (pages) {
            try {
                if (!checkedLinks.containsKey(path)) {
                    checkedLinks.put(path, path);
                    Document doc = getDocument(path);
                    pages.add(getPage(doc));
                    LinkedHashSet<String> urls = this.getChildren(path);
                    Set<WebsiteMapper> taskList = Collections.synchronizedSet(new LinkedHashSet<>());
                    for (String link : urls) {
                        taskList.add((WebsiteMapper) new WebsiteMapper(checkedLinks, link, root).fork());
                        for (WebsiteMapper task : taskList) {
                            pages.addAll(task.join());
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return pages;
    }

    private Document getDocument(String path) throws IOException {
        return Jsoup.connect(path).maxBodySize(0).timeout(10000).get();
    }

    private WebPage getPage(Document doc) {
        WebPage page = new WebPage();
        page.setPath(doc.location());
        page.setCode(doc.connection().response().statusCode());
        page.setContent(String.valueOf(doc));
        // TODO: 16/09/2022 remove
        System.out.println(page.getPath() + " -> " + System.currentTimeMillis() + " " + Thread.currentThread());
        System.out.println("checkedLinks size -> " + checkedLinks.size());
        return page;
    }
}
