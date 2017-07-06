import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by Travis Brindley on 7/5/2017.
 * Assignment:  Create a baseball stat program that will calculate the batting average and slugging percentage for a user defined number of players
 */

public class BaseballStats {
    private static int GetTeamSize() {
        Scanner scan = new Scanner(System.in);

        System.out.print("How many players do you have on your team?  ");
        while (!scan.hasNextInt()) {
            System.out.println("\n invalid input.  How many players do you have on the team? ");
            scan.next();
        }
        int totalPlayer = scan.nextInt();
        return totalPlayer;
    }

    private static String[] GetPlayerName(int teamSize) { //Get player names
        Scanner scan = new Scanner(System.in);
        String[] playerRoster = new String[teamSize];
        int playerCounter = 1;
        for (int i = 0; i < teamSize; i++) {
            System.out.printf("Please enter player %d's name: ", playerCounter);
            while (!scan.hasNextLine()) {
                System.out.println("\nInvalid input, please enter a player's name");
                scan.nextLine();
            }
            playerRoster[i] = scan.nextLine();
            playerCounter += 1;
        }
        return playerRoster;
    }

    private static int[][] GetPlayerStats(int teamSize, String[] teamRoster) {
        Scanner scan = new Scanner(System.in);
        int atBats;
        int[][] teamStats = new int[teamSize][];

        for (int i = 0; i < teamStats.length; i++) { // cycles through roster
            System.out.printf("\nHow many at bats does %s have?", teamRoster[i]);
            int batcount = 1;
            atBats = scan.nextInt();
            teamStats[i] = new int[atBats];
            for (int j = 0; j < atBats; j++) { //repeats prompt for each at bat;

                System.out.printf(" \nWhat did %s get on at bat #%d. (0 for out, 1 for single, 2 for double, 3 for triple, 4 for HR)? ", teamRoster[i], batcount);
                while (!scan.hasNextInt()) {
                    System.out.printf("\n invalid input, how many at bats does %s have?", teamRoster[i]);
                    scan.nextInt();
                }

                int atbat = scan.nextInt();

                while(atbat < 0 && atbat > 4){
                    System.out.printf(" \nInvalid Input.  What did %s get on at bat #%d. (0 for out, 1 for single, 2 for double, 3 for triple, 4 for HR)? ", teamRoster[i], batcount);
                    atbat = scan.nextInt();
                }
                teamStats[i][j] = atbat;
                batcount += 1;
            }
        }
        return teamStats;
    }

    private static void GetAverages(String[] teamRoster, int[][] teamStats) {

        for (int i = 0; i < teamStats.length; i++) {
            int getavg = 0;
            int getslug = 0;
            for (int j = 0; j < teamStats[i].length; j++) {

                if (teamStats[i][j] > 0) {
                    getavg += 1;
                }
                getslug += teamStats[i][j];
            }
            int totalAB = teamStats[i].length;
            BigDecimal slug = BigDecimal.valueOf(getslug);
            BigDecimal avg = BigDecimal.valueOf(getavg);
            BigDecimal length = BigDecimal.valueOf(totalAB);
            BigDecimal baverage = avg.divide(length, 3, RoundingMode.HALF_DOWN);
            BigDecimal slugging = slug.divide(length, 3, RoundingMode.HALF_DOWN);

            System.out.printf("%2s%12s%18s \n", "Player", "AVG", "Slugging");
            System.out.println("------------------------------------");
            System.out.printf("%s% 15.3f %15.3f", teamRoster[i], baverage, slugging);
            System.out.println();
        }
    }

    private static void SaveToXML(String[] teamRoster, int[][] teamStats) {
        try {
            //source for most of the code:  https://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            //Creates root
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("BaseballTeam");
            doc.appendChild(root);

            //Lists players
            Element player = doc.createElement("Player");
            root.appendChild(player);

            for (int i = 0; i < teamStats.length; i++) {
                String number;
                number = Integer.toString(i);
                player.setAttribute("id", number);

                //Stores player name  (get from teamRoster[i])
                Element playerName = doc.createElement("playerName");
                playerName.appendChild(doc.createTextNode(teamRoster[i]));
                player.appendChild(playerName);

                for (int j = 0; j < teamStats[i].length; j++) {
                    String AtBatNumber = "AB";
                    AtBatNumber += Integer.toString(j);

                    String AtBatResult;
                    AtBatResult = Integer.toString(teamStats[i][j]);
                    //at bat results from teamStats[i][j]
                    Element atBat = doc.createElement(AtBatNumber);
                    atBat.appendChild(doc.createTextNode(AtBatResult));
                    player.appendChild(atBat);
                }
            }

            //write the results into xml file
            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer transformer = tfFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("SavedBaseballRoster.xml"));

            transformer.transform(source, result);

            System.out.println("File Saved!");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to the Grand Circus Baseball Application");

        do {
            try {
                int teamSize = GetTeamSize();
                String[] teamRoster = GetPlayerName(teamSize);
                int[][] teamStats = GetPlayerStats(teamSize, teamRoster);
                GetAverages(teamRoster, teamStats);

                System.out.println("\n Would you like to permanently save to a file? \n" +
                        "  Press 1 for yes\n" +
                        "  Press 2 for no");

                while (!scan.hasNextInt()) {
                    System.out.println("Invalid input, would you ike to permanently save to a file?\n" +
                            "  Press 1 for yes\n" +
                            "  Press 2 for no");
                    scan.nextInt();

                }
                int saveIt = scan.nextInt();
                while (saveIt != 1 && saveIt != 2) {
                    System.out.println("Invalid input, would you ike to permanently save to a file?\n" +
                            "  Press 1 for yes\n" +
                            "  Press 2 for no");
                    saveIt = scan.nextInt();
                }
                switch (saveIt) {
                    case 1:
                        SaveToXML(teamRoster, teamStats);
                }

                scan.nextLine(); // garbage catcher
                System.out.println("Would you like to run this again? (Y/N) ");
                String keepLooping = scan.nextLine();

                while (!keepLooping.equalsIgnoreCase("y") || !keepLooping.equalsIgnoreCase("n")) {
                    System.out.println("Invalid response.  would you like to run this program again? (Y/N)  ");
                    keepLooping = scan.nextLine();
                }
                if (keepLooping.equalsIgnoreCase("n")) {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid data type:  Please enter a number");
            }


        } while (true);
        System.out.println("Thanks!");
    }
}
