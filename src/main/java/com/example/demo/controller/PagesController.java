package com.example.demo.controller;

import com.example.demo.model.Link;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author Bruno Ramirez
 */
@RestController
public class PagesController {

    @GetMapping("/api/link/preview")
    public Link getLinkPreviewInfo(@RequestParam(value = "url", required = true) String url) {
        Link link = null;
        try {
            link = extractLinkPreviewInfo(url);
        } catch (IOException e) {
            System.out.println("Unable to connect to : {}");
            System.out.println(url);
        }
        return link;
    }

    private Link extractLinkPreviewInfo(String url) throws IOException{
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        Document document = Jsoup.connect(url).get();
        String title = getMetaTagContent(document, "meta[name=title]");
        String description = getMetaTagContent(document, "meta[name=description]");
        String ogUrl = StringUtils.defaultIfBlank(getMetaTagContent(document, "meta[property=og:url]"), url);
        String ogDesc = getMetaTagContent(document, "meta[property=og:description]");
        String ogTitle = getMetaTagContent(document, "meta[property=og:title]");
        String ogImage = getMetaTagContent(document, "meta[property=og:image]");
        Link link = new Link();
        link.setTitle(StringUtils.defaultIfBlank(ogTitle, title));
        link.setUrl(url);
        link.setDescription(StringUtils.defaultIfBlank(ogDesc, description));
        link.setImage(ogImage);
        return link;
    }

    private String getMetaTagContent(Document document, String cssQuery) {
        Element elm = document.select(cssQuery).first();
        if (elm != null) {
            return elm.attr("content");
        }
        return "";
    }

    @GetMapping("/document/{documentId}/version/{versionId}/preview")
    public ResponseEntity<String> getDocumentPreview(@PathVariable Long documentId, @PathVariable Long versionId) {

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <meta property=\"og:title\" content=\"caratula.msg\" />\n" +
                        "    <meta property=\"og:description\" content=\"Dot manage your doodles and social apps\" />\n" +
                        "    <meta property=\"og:image\" content=\"https://i.ibb.co/DkFfbCj/logo-svg.png\" />" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <script>\n" +
                        "    </script>\n" +
                        "<img src=\"https://i.ibb.co/DkFfbCj/logo-svg.png\"/>" +
                        "</body>\n" +
                        "</html>");

    }
}
