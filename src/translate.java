import org.atteo.evo.inflector.English;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/* this is a test */

public class translate {
    private ArrayList < word > translatedTokens = new ArrayList < > (); /* array list that stores the value of the translated tokens */
    private boolean isMasc = true;
    private boolean isPlural = false;
    private Map<String, word> dict = new HashMap<>();
    private String stringToTranslate;



    private translate(String stringToTranslate) throws IOException { /* constructor that calls the getInput function when called in order to get the msg to be translated */
        this.stringToTranslate = stringToTranslate;
        getInput();
    }
    private void readDict() throws IOException {
        BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary.csv"), "UTF8"));
        int counter = 0; /* initialises counter to 0 since counter is inspected later */
        String key = "";
        String value = "";

        for (String contents = fileRead.readLine(); contents != null; contents = fileRead.readLine()) { /* iterates through all rows in .csv file */

            for (String data: contents.split(",")) { /* iterates through all elements split around a comma (since .csv files seperate fields with commas */

                if (counter == 0) {
                    key = data;
                    counter++;
                }else if (counter == 1) {
                    value = data;
                    counter++;
                }  else if (counter == 2) {
                    word x = new word();
                    x.setName(value);
                    x.setType(data);
                    dict.put(key, x);
                    break;
                }

            }
            counter = 0;
        }
    }

    private void getInput() throws IOException { /* needs to throw exceptions in order to call translate function */

        //System.out.println("ENGLISH TO SPANISH TRANSLATOR");
        //System.out.println("------------------------------");
        //System.out.println("Enter a sentence you would like to translate"); /* default output msg to user */

        readDict();

       // Scanner sc = new Scanner(System.in); /* scanner instance to get input */
        //String input = sc.nextLine(); /* gets user input */

        String[] tokens = stringToTranslate.split(" ?(?<!\\G)((?<=[^\\p{Punct}])(?=\\p{Punct})|\\b) ?"); /* regex to ensure that punctuation is considered as a token */

        for (String token : tokens) { /* iterates through all elements in tokens array and calls the translate function to translate the elements to Spanish if possible */
            translate(token);
        }

        grammarRules(); /* Calls the grammarRules method to enforce all possible grammar rules */


        printTokens();

        System.out.print("\n");


    }

    private void printTokens() {
        String punc = ".,:;!?..."; /* Contains punctuation to match to some string */

        for (int i = 0; i < translatedTokens.size(); i++) { /* Iterates through all translated tokens */
            if(i==0) {
                translatedTokens.get(i).makeUCase();
            }
            else if (i < translatedTokens.size() - 1 && punc.contains(translatedTokens.get(i + 1).getName())) {
                System.out.print(translatedTokens.get(i).getName());
                continue;
            } else if (translatedTokens.get(i).getIsUCase()) {
                translatedTokens.get(i).makeUCase();
            }

            if(i<translatedTokens.size() - 1) {
                System.out.print(translatedTokens.get(i).getName() + " ");
            } else {
                System.out.print(translatedTokens.get(i).getName());
            }

        }
    }
    private void translate(String token) throws IOException { /* needs to throw such exceptions in order to read from given .csv file */
        String punc = ".,:;!?...";
        String translatedWord = "";


        BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary.csv"), "UTF8")); /* BufferReader instance used to read the required .csv file */
        int counter = 0; /* initialises counter to 0 since counter is inspected later */

        for (String contents = fileRead.readLine(); contents != null; contents = fileRead.readLine()) { /* iterates through all rows in .csv file */
            boolean isUpperCase = false;

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

                } else if (counter == 0 && token.equals(English.plural(data)) && !token.equals("is")) {
                    this.isPlural = true;
                    counter++;
                } else if (counter == 0 && token.toLowerCase().equals(data)) {
                    isUpperCase = true;
                    counter++;

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
                } else if (counter == 2) {
                    if (!isUpperCase && this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        x.setPlural(true);
                        translatedTokens.add(x);
                    } else if (!isUpperCase && !this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setPlural(false);
                        x.setType(data);
                        translatedTokens.add(x);
                    } else if (isUpperCase && this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        x.setPlural(true);
                        x.isUCase(true);
                        translatedTokens.add(x);
                    } else if (isUpperCase && !this.isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        x.isUCase(true);
                        x.setPlural(false);
                        translatedTokens.add(x);
                    }
                    return;
                }

            }
            this.isPlural=false;
        }

    }

    private boolean getLev(String tokenToTranslate, String readData) { //Change this, close to finishing

        int distance = distance(tokenToTranslate, readData);
        return !dict.containsKey(tokenToTranslate) && distance == 1;

    }
    private void rule0() {

        for(int i = 0; i < translatedTokens.size() - 1; i++) {
            if(translatedTokens.get(i).getName().equals("ella") && (translatedTokens.get(i+1).getType().equals("noun") || translatedTokens.get(i+1).getType().equals("adjective"))) {
                translatedTokens.get(i).setName("su");
                translatedTokens.get(i).setType("possessive");
            }
        }

    }

    private void rule1() {
        String adj, noun;

        for (int i = 0; i < translatedTokens.size(); i++) {
            if (translatedTokens.get(i).getType().equals("noun")) {
                noun = translatedTokens.get(i).getName();
                if (noun.endsWith("a") || noun.endsWith("d") || noun.endsWith("z") || noun.endsWith("ión")) {
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

                if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getName().endsWith("o") &&(translatedTokens.get(i-1).getType().equals("noun") ||
                    translatedTokens.get(i-2).getType().equals("noun") || translatedTokens.get(i-1).getType().equals("conjunction"))) {
                    adj = translatedTokens.get(i).getName();
                    int size = adj.length();
                    adj = adj.substring(0, size - 1) + "a";
                    translatedTokens.get(i).setName(adj);

                }

            } else if (this.isMasc) { /* masculine */ /* rule 1 */

                if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getName().endsWith("o") &&(translatedTokens.get(i-1).getType().equals("noun") ||
                        translatedTokens.get(i-2).getType().equals("noun") || translatedTokens.get(i-1).getName().equals("y"))) {
                    adj = translatedTokens.get(i).getName();
                    int size = adj.length();
                    adj = adj.substring(0, size - 1) + "o";
                    translatedTokens.get(i).setName(adj);


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

            else if (translatedTokens.get(i).getType().equals("noun") && article_pos >=0 && (translatedTokens.get(i-1).getType().equals("adjective") && translatedTokens.get(i-2).getName().equals("y"))) {
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

    private void setPlurals() {
        for (int i = 0; i < translatedTokens.size(); i++) {
            if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i-1).getType().equals("noun") && translatedTokens.get(i - 1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            } else if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i-2).getType().equals("noun") && translatedTokens.get(i - 2).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            }
            else if (translatedTokens.get(i).getType().equals("article")  && isMasc && translatedTokens.get(i+1).getType().equals("noun") && translatedTokens.get(i+1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            } else if (translatedTokens.get(i).getType().equals("article")  && !isMasc && translatedTokens.get(i+1).getType().equals("noun") && translatedTokens.get(i+1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            } else if (translatedTokens.get(i).getType().equals("possessive") && translatedTokens.get(i+1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            }
        }
     }

    private void rule2And4() {

        setPlurals();
        String noun, adj, article, possessive;
        /* Rule 2 and 4 */
        for (int i = 0; i < translatedTokens.size(); i++)
            if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getIsPlural()) {
                adj = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(adj, i);
            }
             else if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getIsPlural()) {
                adj = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(adj, i);            }
            else if (translatedTokens.get(i).getType().equals("article") && isMasc && translatedTokens.get(i).getIsPlural()) {
                article = "los";
                translatedTokens.get(i).setName(article);
            } else if (translatedTokens.get(i).getType().equals("article") && !isMasc && translatedTokens.get(i).getIsPlural()) {
                article = "las";
                translatedTokens.get(i).setName(article);
            } else if (translatedTokens.get(i).getType().equals("noun") && translatedTokens.get(i).getIsPlural()) {
                noun = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(noun, i);
            } else if (translatedTokens.get(i).getType().equals("possessive") &&  translatedTokens.get(i).getIsPlural()) {
                possessive = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(possessive, i);
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
        ArrayList < String > pronounList = new ArrayList < > ();
        pronounList.add("es");
        pronounList.add("estoy");
        pronounList.add("estás");
        pronounList.add("está");
        pronounList.add("estamos");
        pronounList.add("están");
        pronounList.add("eres");
        pronounList.add("son");
        pronounList.add("somos");
        pronounList.add("soy");


        for (int i = 0; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getType().equals("pronoun") && pronounList.contains(translatedTokens.get(i + 1).getName())) {
                translatedTokens.remove(i);
               // if(translatedTokens.get(i+1).getType().equals("punctuation")) {
                   // translatedTokens.remove(i+1);
               // }
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
        for (int i = 1; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getName().equals("no")) {
                word neg, verb;
                neg = translatedTokens.get(i);
                verb = translatedTokens.get(i-1);

                translatedTokens.set(i-1, neg);

                translatedTokens.set(i, verb);
               // if(translatedTokens.get(i+1).getType().equals("punctuation")) {
                 //   translatedTokens.remove(i+1);
               // }
            }
        }



    }

    private void rule10() {
        for (int i = 0; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getName().equals("con") && translatedTokens.get(i + 1).getName().equals("tú")) {
                translatedTokens.get(i).setName("contigo");
                translatedTokens.get(i).setType("pronoun");
                translatedTokens.remove(i+1);
                /* removed i originally so now i + 1 == i */
                //if(translatedTokens.get(i).getType().equals("punctuation")) {
                //  translatedTokens.remove(i);
                //}


            } else if (translatedTokens.get(i).getType().equals("preposition") && translatedTokens.get(i + 1).getName().equals("tú")) {
                translatedTokens.get(i + 1).setName("ti");
            }
            if (translatedTokens.get(i).getName().equals("con") && translatedTokens.get(i + 1).getName().equals("yo")) {
                translatedTokens.get(i).setName("conmigo");
                translatedTokens.get(i).setType("pronoun");
                translatedTokens.remove(i+1);
                //if(translatedTokens.get(i).getType().equals("punctuation")) {
                //  translatedTokens.remove(i);
                // }

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
        rule7();
        rule9();
        rule6();

        rule8();
        rule10();
    }

    public static void main(String[] args) throws IOException {

 //       BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));

//        for(String contents = fileReader.readLine(); contents != null; contents = fileReader.readLine()) {
  //          new translate(contents);
    //    }

       new translate("The time is difficult, the hours are hard but I am clear about my life\n");
    }
}
