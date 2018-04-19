package com.bot.cookbetter.version2;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 
 * @author Shrikanth N C (snaraya7)
 *
 */
public class GoogleImageFetcher {


	private static String ERROR_IMAGE_LINK = "https://en.wikipedia.org/wiki/File:Image_not_available.png";

	public static String getAnImageLink(String ingredient) {
		String link = "https://www.google.com/search?q=" + ingredient + " recipe"
				+ "&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjZ-v7z7rfaAhVQtlMKHYW4CzIQ_AUICigB&biw=1280&bih=614";

		try {
			Document jsoupDocument = Jsoup.connect(link).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();

			for (Element imgElement : jsoupDocument.getElementsByTag("img")) {

				try {
					return imgElement.getElementsByAttribute("src").attr("src");
				} catch (Exception e) {
					continue;
				}

			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return ERROR_IMAGE_LINK;

	}

	public  static  void main(String... a){
	    System.out.print(getAnImageLink("chicken"));
    }
}