import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Manxkaffee on 15.11.2017.
 */
public class UIFunctions {
    @FXML public ListView <String> resultList;
    @FXML public SplitPane leftRightSplit;
    @FXML public CheckBox includeBS;
    @FXML public CheckBox includeKinoX;
    @FXML public Button watchButton;
    @FXML public TextField searchField;
    @FXML public ListView<String> episodeList;
    @FXML public ListView<String> streamList;

    ArrayList<String> resultListItems = new ArrayList<>();
    ArrayList<String> episodeListItems = new ArrayList<>();
    ArrayList<String> streamListItems = new ArrayList<>();

    public String showLinkEnd="";
    public boolean bsShow = false;
    public boolean kinoXShow = false;


    //Checkt, ob man Bs und KinoX suchen möchte, Printet alle Ergebnisse und Löscht die Liste dann.
    public void search(){
        if(includeBS.isSelected())searchBS();
        if(includeKinoX.isSelected())searchKinoX();
        resultList.setItems(FXCollections.observableArrayList(resultListItems));
        resultListItems.clear();
    }

    public void searchBS(){
        try{
            //Öffne alle Serien von bs.to
            Document doc = Jsoup.connect("https://bs.to/andere-serien").get();
            for(Element div:doc.select("div")){
                if(div.attr("id").equals("seriesContainer")){
                    //Füg alles in die ErgebnisListe hinzu, was mit der Suchangrage übereinstimmt.
                    for(Element show:div.select("a")){
                        if(show.text().toLowerCase().contains(searchField.getText().toLowerCase()))resultListItems.add(show.text()+"  (BS)");
                    }
                }
            }
        }catch(Exception e){System.out.println(e.getMessage());}

    }
    public void searchKinoX(){
        try{
            Document doc = Jsoup.connect("https://kinox.to/Search.html?q="+searchField.getText().replace(" ", "+")).get();
            for (Element table : doc.select("table")){
                if(table.attr("id").equals("RsltTableStatic")){
                    for(Element show : table.select("a")){
                        resultListItems.add(show.attr("href").replace("/Stream/","").replace(".html","")+"  (KinoX)");
                    }
                }
            }
        }catch(Exception e){System.out.println(e.getMessage());}
    }

    //Printet alle Folgen der ausgesuchten Serie, und Löscht dann die Liste.
    public void showSelected(){
        if(resultList.getSelectionModel().getSelectedItem().contains("(BS)")){
            openShowBS(resultList.getSelectionModel().getSelectedItem().replace("  (BS)",""));
            bsShow=true;
            kinoXShow=false;
        }
        else if(resultList.getSelectionModel().getSelectedItem().contains("KinoX")){
            openShowKinoX(resultList.getSelectionModel().getSelectedItem().replace("  (KinoX)",""));
            bsShow=false;
            kinoXShow=true;
        }

        episodeList.setItems(FXCollections.observableArrayList(episodeListItems));
        episodeListItems.clear();
    }
//testcomment
    public void openShowBS(String showName){
        watchButton.setDisable(true);
        try{
            Document doc = Jsoup.connect("https://bs.to/andere-serien").get();

            //Öffne den Link der Show um die Staffeln zu finden
            for(Element showLink : doc.select("a")){
                if(showLink.text().equals(showName)){
                    showLinkEnd = showLink.attr("href");
                    doc = Jsoup.connect("https://bs.to/" + showLinkEnd).get();
                }
            }
            //Such die Staffeln auf der ShowWebseite
            for(Element div : doc.select("div")){
                if(div.attr("id").equals("seasons")){
                    //Öffne jeden Staffellink um die Episoden zu finden
                    for(Element staffelLink : div.select("a")){
                        Document staffelDoc = Jsoup.connect("https://bs.to/" + staffelLink.attr("href")).get();
                        //Such den EpisodenTable
                        for(Element table : staffelDoc.select("table")){
                            if(table.attr("class").equals("episodes")){
                                //Geh durch die Episoden, füg sie zur EpisodenListe hinzu.
                                for(Element episode : table.select("a")){
                                    if(episode.text().length()<=3)episodeListItems.add(episode.attr("href").replace(showLinkEnd, ""));
                                }
                            }
                        }
                    }
                }
            }

        }catch(Exception e){System.out.println(e.getMessage());}

    }
    //Bei KinoX kann ich momentan nur die Seite selber öffnen, deswegen wird hier erst nur der watchButton Enabled
    public void openShowKinoX(String showName){
        watchButton.setDisable(false);
    }



    public void openStreamList(){
        if(bsShow&&!kinoXShow) openStreamListBS();
        else if(kinoXShow&&!bsShow)openStreamListKinoX();
    }

    public void openStreamListKinoX(){
    }
    //Eine sehr hässliche Lösung um die Streams zu kriegen!!
    public void openStreamListBS(){
        try{
            Document doc = Jsoup.connect("https://bs.to/"+showLinkEnd+episodeList.getSelectionModel().getSelectedItem()).get();
            for (Element link : doc.select("a")){
                if(link.attr("href").contains(episodeList.getSelectionModel().getSelectedItem()+"/"))streamListItems.add(link.text());
            }
            streamList.setItems(FXCollections.observableArrayList(streamListItems));
            streamListItems.clear();
        }catch(Exception e){System.out.println(e.getMessage());}
    }

    public void unlockWatchButton(){
        watchButton.setDisable(false);
    }

    public void openVideo(){
        if(bsShow&&!kinoXShow) openVideoBS();
        else if(kinoXShow&&!bsShow)openVideoKinoX();
    }
    public void openVideoKinoX() {
        try {
            String link = "https://kinox.to/Stream/" + resultList.getSelectionModel().getSelectedItem().replace("  (KinoX)", "") + ".html";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.indexOf("win") >= 0) openInBrowserWindows(link);
            else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) openInBrowserLinux(link);
        }catch (Exception e){System.out.println(e.getMessage());}
    }
    public void openVideoBS(){
        try{
            Document doc = Jsoup.connect("https://bs.to/"+showLinkEnd+episodeList.getSelectionModel().getSelectedItem()+"/"+streamList.getSelectionModel().getSelectedItem()).get();
            for (Element link : doc.select("a")){
                if(link.attr("class").equals("hoster-player")){
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.indexOf("win") >= 0) openInBrowserWindows(link.attr("href"));
                    else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) openInBrowserLinux(link.attr("href"));
                }
            }
        }catch (Exception e){System.out.println(e.getMessage());}
    }
    public void openInBrowserLinux(String url){
        try{
            Runtime rt = Runtime.getRuntime();
            String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror",
                    "netscape", "opera", "links", "lynx" };

            StringBuffer cmd = new StringBuffer();
            for (int i = 0; i < browsers.length; i++)
                if(i == 0)
                    cmd.append(String.format(    "%s \"%s\"", browsers[i], url));
                else
                    cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
            // If the first didn't work, try the next browser and so on

            rt.exec(new String[] { "sh", "-c", cmd.toString() });
        }catch (Exception e){System.out.println(e.getMessage());}
    }
    public void openInBrowserWindows(String url){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL(url).toURI());
            } catch (Exception e) { e.printStackTrace();}
        }
    }
}

