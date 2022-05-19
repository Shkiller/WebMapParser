import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveTask;

public class WebMap extends RecursiveTask<List<String>> {
    private String originalUrl;
    private String indentSymbol;
    private String url;
    private ConcurrentSkipListSet<String> urls = new ConcurrentSkipListSet<>();
    private final int FIST_INDEX = 0;
    private final int TIME = 180;

    public WebMap(String url, String indentSymbol, String originalUrl, ConcurrentSkipListSet<String> urls) {
        this.originalUrl = originalUrl;
        this.url = url;
        this.indentSymbol = indentSymbol;
        this.urls = urls;
    }

    @Override
    protected List<String> compute() {

        Document doc;
        //загрузка ссылки
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException ioException) {
            urls.add(url);
            return new ArrayList<>();
        }
        //проверка ссылки
        if (doc != null) {
            List<WebMap> taskList = new ArrayList<>();
            List<String> result = new ArrayList<>();
            urls.add(url);
            Elements elements = doc.select("a");
            indentSymbol = indentSymbol + "\t";
            //перебор ссылок
            for (var element : elements) {
                String newUrl = element.attr("href");
                //ссылка вида /company/
                if (!newUrl.equals(""))
                    if (newUrl.charAt(FIST_INDEX) == '/' && !newUrl.equals("/")) {
                        newUrl = originalUrl + newUrl;
                        if (!urls.contains(newUrl) && newUrl.contains(url)) {
                            try {
                                Thread.sleep(TIME);
                            } catch (InterruptedException e) {
                                return new ArrayList<>();
                            }
                            WebMap task = new WebMap(newUrl, indentSymbol, originalUrl, urls);
                            task.fork();
                            taskList.add(task);
                        }
                        //ссылка вида https://skillbox.ru/company/
                    } else {
                        if (newUrl.contains(url) && !newUrl.equals(url)) {
                            if (!urls.contains(newUrl)) {
                                try {
                                    Thread.sleep(TIME);
                                } catch (InterruptedException e) {
                                    return new ArrayList<>();
                                }
                                WebMap task = new WebMap(newUrl, indentSymbol, originalUrl, urls);
                                taskList.add((WebMap) task.fork());
                            }
                        }
                    }
            }
            result.add(indentSymbol + url + "\n");
            for (var task : taskList) {
                result.addAll(task.join());
            }
            return result;
        }
        return new ArrayList<>();
    }
}
