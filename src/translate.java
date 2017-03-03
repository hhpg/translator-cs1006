import org.atteo.evo.inflector.English;

import java.util.Scanner; /* need to import scanner to get user input */
import java.io.BufferedReader; /* .io.* to be able to read from .csv file */
import java.util.ArrayList; /* array list to store translated tokens */
import java.io.IOException; /* exception needs to be thrown */
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.lang.Math.*;

public class translate {
    private ArrayList < word > translatedTokens = new ArrayList < > (); /* array list that stores the value of the translated tokens */
    private boolean isMasc = true;
    private boolean isPlural = false;
    private boolean begin = true;


    translate() throws IOException { /* constructor that calls the getInput function when called in order to get the msg to be translated */
        getInput();
    }

    private void getInput() throws IOException { /* needs to throw exceptions in order to call translate function */

        System.out.println("ENGLISH TO SPANISH TRANSLATOR");
        System.out.println("------------------------------");
        System.out.println("Enter a sentence you would like to translate"); /* default output msg to user */

        Scanner sc = new Scanner(System.in); /* scanner instance to get input */
        String input = sc.nextLine(); /* gets user input */

        String[] tokens = input.split(" ?(?<!\\G)((?<=[^\\p{Punct}])(?=\\p{Punct})|\\b) ?"); /* regex to ensure that punctuation is considered as a token */

        for (int i = 0; i < tokens.length; i++) { /* iterates through all elements in tokens array and calls the translate function to translate the elements to Spanish if possible */
            translate(tokens[i]);
        }

        grammarRules(); /* Calls the grammarRules method to enforce all possible grammar rules */

        String punc = ".,:;!?..."; /* Contains punctuation to match to some string */

        System.out.print("\n");
        for (int i = 0; i < translatedTokens.size(); i++) { /* Iterates through all translated tokens */
            if(i==0) {
                translatedTokens.get(i).makeUCase();
            }
            else if (i < translatedTokens.size() - 1 && punc.contains(translatedTokens.get(i + 1).getName())) {
                System.out.print(translatedTokens.get(i).getName());
                continue;
            } else if (translatedTokens.get(i).getIsUCase() == true) {
                translatedTokens.get(i).makeUCase();
            }

            System.out.print(translatedTokens.get(i).getName() + " ");

        }

        System.out.print("\n");

        for(int i = 0; i < translatedTokens.size(); i++) {
            System.out.print(translatedTokens.get(i).getType() + " ");
        }
    }
    private void translate(String token) throws IOException { /* needs to throw such exceptions in order to read from given .csv file */
        String punc = ".,:;!?...";
        String translatedWord = "";
        boolean isUpperCase = false;

        BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary.csv"), "UTF8")); /* BufferReader instance used to read the required .csv file */
        int counter = 0; /* initialises counter to 0 since counter is inspected later */

        for (String contents = fileRead.readLine(); contents != null; contents = fileRead.readLine()) { /* iterates through all rows in .csv file */

            for (String data: contents.split(",")) { /* iterates through all elements split around a comma (since .csv files seperate fields with commas */
                if (punc.contains(token)) {
                    word x = new word();
                    x.setName(token);
                    x.setType("punctuation");
                    translatedTokens.add(x);
                    return;
                }
                if (counter == 0 && token.equals(data)) {
                    counter++;
                    continue;

                } else if (counter == 0 && token.equals(English.plural(data)) && !token.equals("is")) {
                    this.isPlural = true;
                    counter++;
                    continue;
                } else if (counter == 0 && token.toLowerCase().equals(data)) {
                    isUpperCase = true;
                    counter++;
                    continue;

                } else if (counter == 0 & token.toLowerCase().equals(English.plural(data)) && !token.equals("is")){
                    isUpperCase = true;
                    this.isPlural = true;
                    counter++;
                } else if (counter == 0 && !token.equals(data)) {
                    if (getLev(token, data)) {
                        //translatedWord = data;
                        //counter++;
                        //continue;
                    }
                    break;
                } else if (counter == 1) {
                    translatedWord = data;
                    counter++;
                    continue;
                } else if (counter == 2) {
                    if (!isUpperCase && this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        translatedTokens.add(x);
                    } else if (!isUpperCase && !this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        translatedTokens.add(x);
                    } else if (isUpperCase && this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        x.isUCase(true);
                        translatedTokens.add(x);
                    } else if (isUpperCase && !this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        x.isUCase(true);
                        translatedTokens.add(x);
                    }
                    return;
                }

            }
        }

    }

    private boolean getLev(String tokenToTranslate, String readData) { //Change this, close to finishing

        int distance = distance(tokenToTranslate, readData);
        return distance == 1;

    }
    private void rule0() {

        for(int i = 0; i < translatedTokens.size() - 1; i++) {
            if(translatedTokens.get(i).getName().equals("ella") && (translatedTokens.get(i+1).getType().equals("noun") || translatedTokens.get(i+1).getType().equals("adjective"))) {
                translatedTokens.get(i).setName("su");
                translatedTokens.get(i).setType("possessive");
            }
        }

    }

    private void mascOrFem(int pos) {


    /*
    private void listToString(ArrayList<word> tokens) {
        String x, y = "";

        ArrayList<Integer> indeces = new ArrayList<Integer>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getName().equals("conjunction")) {
                indeces.add(i);

            }


        }

*/
    }

    private void rule1() {
        String adj, noun;


        for (int i = 0; i < translatedTokens.size(); i++) {
            if (translatedTokens.get(i).getType().equals("noun")) {
                noun = translatedTokens.get(i).getName();
                if (noun.endsWith("a") || noun.endsWith("d") || noun.endsWith("z") || noun.endsWith("-ión")) {
                    isMasc = false;
                    if(i>0) {
                        if (translatedTokens.get(i - 1).getType().equals("article") && translatedTokens.get(i - 1).getName().toLowerCase().equals("el")) {
                            translatedTokens.get(i - 1).setName("la");
                        } else if (translatedTokens.get(i - 1).getType().equals("article") && translatedTokens.get(i - 1).getName().substring(0, 2).equals("un")) {
                            translatedTokens.get(i - 1).setName("una");

                        }
                    }

                } else {
                    isMasc = true;
                    if (i > 0) {

                        if (translatedTokens.get(i - 1).getType().equals("article") && translatedTokens.get(i - 1).getName().toLowerCase().equals("la")) {
                            translatedTokens.get(i - 1).setName("el");


                        } else if (translatedTokens.get(i - 1).getType().equals("article") && translatedTokens.get(i - 1).getName().substring(0, 1).equals("una")) {
                            translatedTokens.get(i - 1).setName("un");

                        }
                    }
                }
            }

            if (!this.isMasc) { /* feminine;  Rule 1 */

                  if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getName().endsWith("o")) {
                    adj = translatedTokens.get(i).getName();
                    int size = adj.length();
                    adj = adj.substring(0, size - 1) + "a";
                    translatedTokens.get(i).setName(adj);
                    continue;

                }

            } else if (this.isMasc) { /* masculine */ /* rule 1 */

                  if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getName().endsWith("o")) {
                    adj = translatedTokens.get(i).getName();
                    int size = adj.length();
                    adj = adj.substring(0, size - 1) + "o";
                    translatedTokens.get(i).setName(adj);
                    continue;


                }

            }


        }
    }

    private void rule3() {
        int article_pos = -1;
        for (int i = 0; i < translatedTokens.size(); i++) { /* Checks for unordered adjective/noun stuff */

            if (translatedTokens.get(i).getType().equals("article")) {
                article_pos = i;
            }

            else if (translatedTokens.get(i).getType().equals("noun") && article_pos >=0) {
                word x = translatedTokens.get(i);
                translatedTokens.add(article_pos+1, x);
                translatedTokens.remove(i+1);

            }

            }
            /* if there isn't an article present in the list then the following for loop is executed in */
            for (int i = 1; i < translatedTokens.size(); i++) { /* Checks for unordered adjective/noun stuff */
                if (translatedTokens.get(i).getType().equals("noun") && translatedTokens.get(i-1).getType().equals("adjective")) {
                    word x = translatedTokens.get(i);
                    word y = translatedTokens.get(i-1);

                    translatedTokens.set(i-1, x);
                    translatedTokens.set(i, y);

                }

            }


        }


    private void rule2And4() {
        String noun, adj, article, possessive;
        for (int i = 0; i < translatedTokens.size(); i++) { /* Rule 2 and 4 */

            if (translatedTokens.get(i).getType().equals("adjective") && this.isPlural) {
                adj = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(adj, i);
            } else if (translatedTokens.get(i).getType().equals("article") && this.isPlural&& isMasc) {
                article = "los";
                translatedTokens.get(i).setName(article);
            } else if (translatedTokens.get(i).getType().equals("article") && this.isPlural && !isMasc) {
                article = "las";
                translatedTokens.get(i).setName(article);
            } else if (translatedTokens.get(i).getType().equals("noun") && this.isPlural) {
                noun = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(noun, i);
            } else if (translatedTokens.get(i).getType().equals("possessive") && this.isPlural) {
                possessive = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(possessive, i);
            }
        }
    }

    private void rule5() {
        for (int i = 0; i < translatedTokens.size() - 1; i++) { /* rule 5 */
            if (translatedTokens.get(i).getName().equals("tú") && translatedTokens.get(i + 1).getName().equals("son")) {
                translatedTokens.get(i + 1).setName("eres");
            } else if (translatedTokens.get(i).getName().equals("nosotros") && translatedTokens.get(i + 1).getName().equals("son")) {
                translatedTokens.get(i + 1).setName("somos");

            }

        }
    }

    private void rule6() {
        for (int i = 0; i < translatedTokens.size() - 1; i++) { /* rule 6 */
            if (translatedTokens.get(i).getName().equals("eres") && (translatedTokens.get(i + 1).getType().equals("verb") || translatedTokens.get(i + 1).getName().equals("con"))) {

                translatedTokens.get(i).setName("estás");
            } else if (translatedTokens.get(i).getName().equals("somos") && (translatedTokens.get(i + 1).getType().equals("verb") || translatedTokens.get(i + 1).getName().equals("con"))) {

                translatedTokens.get(i).setName("estamos");
            } else if (translatedTokens.get(i).getName().equals("son") && (translatedTokens.get(i + 1).getType().equals("verb") || translatedTokens.get(i + 1).getName().equals("con"))) {

                translatedTokens.get(i).setName("están");
            } else if (translatedTokens.get(i).getName().equals("soy") && (translatedTokens.get(i + 1).getType().equals("verb") || translatedTokens.get(i + 1).getName().equals("con"))) {

                translatedTokens.get(i).setName("estoy");
            } else if (translatedTokens.get(i).getName().equals("es") && (translatedTokens.get(i + 1).getType().equals("verb") || translatedTokens.get(i + 1).getName().equals("con"))) {

                translatedTokens.get(i).setName("está");
            }
        }
    }

    private void rule7() {
        ArrayList < String > pronounList = new ArrayList < String > ();
        pronounList.add("es");
        pronounList.add("estoy");
        pronounList.add("estás");
        pronounList.add("está");
        pronounList.add("estamos");
        pronounList.add("están");

        for (int i = 0; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getType().equals("pronoun") && pronounList.contains(translatedTokens.get(i + 1).getName()) && i != translatedTokens.size() - 2) {
                translatedTokens.remove(i);
                if(translatedTokens.get(i+1).getType().equals("punctuation")) {
                    translatedTokens.remove(i+1);
                }
            }
        }
    }

    private void swapTokens(String word, String type, int position) {
        translatedTokens.get(position).setName(translatedTokens.get(position - 1).getName());
        translatedTokens.get(position).setType(translatedTokens.get(position - 1).getType());
        translatedTokens.get(position - 1).setName(translatedTokens.get(position - 2).getName());
        translatedTokens.get(position - 1).setType(translatedTokens.get(position - 2).getType());

        translatedTokens.get(position - 2).setName(word);
        translatedTokens.get(position - 2).setType(type);
    }


    private void rule8() {
        for (int i = 2; i < translatedTokens.size(); i++) {
   /* you are watching me */
            if (translatedTokens.get(i).getName().equals("yo")&& translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("me", "pronoun", i);
            }
   /* they are watching you */
            else if (translatedTokens.get(i).getName().equals("tú") && translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("te", "pronoun", i);
            }
   /* i am watching him */
            else if (translatedTokens.get(i).getName().equals("él") && translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("lo", "pronoun", i);
            } else if (translatedTokens.get(i).getName().equals("ella") &&  translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("la", "pronoun", i);
            } else if (translatedTokens.get(i).getName().equals("ellos") &&  translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("los", "pronoun", i);
            }

        }
    }

    private void rule9() {
        for (int i = 1; i < translatedTokens.size(); i++) {
            if (translatedTokens.get(i).getName().equals("no")) {
                translatedTokens.add(0, translatedTokens.get(i));
                translatedTokens.remove(i + 1);
                if(translatedTokens.get(i+1).getType().equals("punctuation")) {
                    translatedTokens.remove(i+1);
                }
            }
        }
    }

    private void rule10() {
        for (int i = 0; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getName().equals("con") && translatedTokens.get(i + 1).getName().equals("tú")) {
                translatedTokens.remove(i);
                translatedTokens.remove(i); /* removed i originally so now i + 1 == i */
                //if(translatedTokens.get(i).getType().equals("punctuation")) {
                  //  translatedTokens.remove(i);
                //}
                word x = new word();
                x.setName("contigo");
                x.setType("pronoun");
                translatedTokens.add(x);

            } else if (translatedTokens.get(i).getType().equals("preposition") && translatedTokens.get(i + 1).getName().equals("tú")) {
                translatedTokens.get(i + 1).setName("ti");
            }
            if (translatedTokens.get(i).getName().equals("con") && translatedTokens.get(i + 1).getName().equals("yo")) {
                translatedTokens.remove(i);
                translatedTokens.remove(i); /* removed i originally so now i + 1 == i */
                //if(translatedTokens.get(i).getType().equals("punctuation")) {
                  //  translatedTokens.remove(i);
               // }
                word x = new word();
                x.setName("conmigo");
                x.setType("pronoun");
                translatedTokens.add(x);
            }
        }
    }


    private int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();


        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
    private void grammarRules() {
        rule0();

        rule3();
        rule1();

        rule2And4();
        rule5();
        rule9();
        rule6();
        rule7();
        rule8();
        rule10();
    }

    public static void main(String[] args) throws IOException {
        new translate();
    }
}
