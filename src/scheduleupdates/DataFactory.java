/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleupdates;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFactory {
    public static Map<Integer, List<ScheduleChange>> classesChanges;
    private static Integer[] classesID = {3, 5, 6, 7, 8, 9, 10, 11, 38, 39, 40, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 41, 36}; //3 = y1; 13 = ya1; 22 = yb1;
        
    public static void loadData() throws IOException {
        final String pageURL = "http://deshalit.iscool.co.il/default.aspx";
        Pattern ptrn = Pattern.compile("<td class=\"MsgCell.+\\s+.+"); //filter through all page
        Pattern ptrn2 = Pattern.compile("[\\d.]+.+"); //just for the information line
        Matcher matcher = null;
        String xml = "";
        
        WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
        HtmlPage page = webClient.getPage(pageURL);
        
        for (Integer classID : classesID) {
            ScriptResult result = page.executeJavaScript("document.getElementById('dnn_ctr11396_TimeTableView_ClassesList').value=" + classID +";");
            page = (HtmlPage) result.getNewPage();
            result = page.executeJavaScript("__doPostBack('dnn$ctr11396$TimeTableView$btnChanges','');");
            page = (HtmlPage) result.getNewPage();

            xml = page.asXml();

            matcher = ptrn.matcher(xml);

            List<String> matches = new ArrayList<String>();
            while (matcher.find()) {
                matches.add(matcher.group());
            }

            ListIterator itr = matches.listIterator();
            while(itr.hasNext()) {
                Object element = itr.next();
                matcher = ptrn2.matcher((String)element);
                matcher.find();
                itr.set(matcher.group());
            }

            matches.stream().forEach(s -> addToMap(classID, s.split(", ")));
        }
        webClient.close();
    }
    
    public static void addToMap(Integer id, String[] info) {
        if (classesChanges.get(id) == null) {
            classesChanges.put(id, new ArrayList<ScheduleChange>());
        }
        classesChanges.get(id).add(new ScheduleChange(Integer.parseInt(info[0].substring(0, 2)), info[1].charAt(info[1].length()-1) - '0' ,info[2]));
    }
}
