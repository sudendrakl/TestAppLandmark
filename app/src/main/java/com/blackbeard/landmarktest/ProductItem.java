package com.blackbeard.landmarktest;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by sudendra.kamble on 08/09/16.
 */

public class ProductItem {
  String imageUrl;
  String leftBadgeStr;
  String rightBadgeStr;
  String typrStr;
  String itemName;
  String priceCurrStr;
  String priceStr;
  String priceOldStr;

  public ProductItem(String imageUrl, String leftBadgeStr, String rightBadgeStr, String typrStr,
      String itemName, String priceCurrStr, String priceStr, String priceOldStr) {
    this.imageUrl = imageUrl;
    this.leftBadgeStr = leftBadgeStr;
    this.rightBadgeStr = rightBadgeStr;
    this.typrStr = typrStr;
    this.itemName = itemName;
    this.priceCurrStr = priceCurrStr;
    this.priceStr = priceStr;
    this.priceOldStr = priceOldStr;
  }

  static List<ProductItem> parse(Element element) {
    Elements elements = element.getElementsByClass("product-item");
    List<ProductItem> list = new ArrayList<>(elements.size());
    for (Element e : elements) {
      Elements imgHolder = e.getElementsByClass("img-holder");
      Elements img = imgHolder.get(0).getElementsByTag("img");
      String imageUrl = img.size() > 0 ? img.get(0).attributes().get("src") : null;

      Elements badgeImg = imgHolder.get(0).getElementsByTag("span");
      String leftBadgeStr = null, rightBadgeStr = null;

      for (Element bi : badgeImg) {
        if (bi.className().contains("left")) {
          leftBadgeStr = bi.html();
        } else if (bi.className().contains("right")) {
          rightBadgeStr = bi.html();
        }
      }
      Elements type = e.getElementsByAttributeValueContaining("itemprop", "concept");
      String typrStr = type.size() > 0 ? type.get(0).html() : null;

      Elements name = e.getElementsByAttributeValueContaining("itemprop", "name");
      String itemName = name.size() > 0 ? name.get(0).html() : null;

      Elements priceEle = e.getElementsByClass("price");
      String priceCurrStr = null, priceStr = null, priceOldStr = null;
      if (priceEle.size() > 0) {
        Elements priceCurr =
            priceEle.get(0).getElementsByAttributeValue("itemprop", "priceCurrency");
        priceCurrStr = priceCurr.size() > 0 ? priceCurr.get(0).html() : null;

        Elements price = priceEle.get(0).getElementsByAttributeValue("itemprop", "price");
        priceStr = price.size() > 0 ? price.get(0).html() : null;

        Elements priceOld = priceEle.get(0).getElementsByTag("del");
        priceOldStr = priceOld.size() > 0 ? priceOld.get(0).html() : null;
      }

      list.add(
          new ProductItem(imageUrl, leftBadgeStr, rightBadgeStr, typrStr, itemName, priceCurrStr,
              priceStr, priceOldStr));
    }

    return list;
  }
}
