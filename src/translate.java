import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/* this is a test */

public class translate {
    private ArrayList<word> translatedTokens = new ArrayList<>(); /* array list that stores the value of the translated tokens */
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

            for (String data : contents.split(",")) { /* iterates through all elements split around a comma (since .csv files seperate fields with commas */

                if (counter == 0) {
                    key = data;
                    counter++;
                } else if (counter == 1) {
                    value = data;
                    counter++;
                } else if (counter == 2) {
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

        new rule(translatedTokens);


        printTokens();

        System.out.print("\n");


    }

    private void printTokens() {
        String punc = ".,:;!?..."; /* Contains punctuation to match to some string */

        for (int i = 0; i < translatedTokens.size(); i++) { /* Iterates through all translated tokens */
            if (i == 0) {
                translatedTokens.get(i).makeUCase();
            } else if (i < translatedTokens.size() - 1 && punc.contains(translatedTokens.get(i + 1).getName())) {
                System.out.print(translatedTokens.get(i).getName());
                continue;
            } else if (translatedTokens.get(i).getIsUCase()) {
                translatedTokens.get(i).makeUCase();
            }

            if (i < translatedTokens.size() - 1) {
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

            for (String data : contents.split(",")) { /* iterates through all elements split around a comma (since .csv files seperate fields with commas */
                if (punc.contains(token)) {
                    word x = new word();
                    x.setName(token);
                    x.setType("punctuation");
                    translatedTokens.add(x);
                    return;
                }
                if (counter == 0 && token.equals(data)) {
                    counter++;

                } else if (counter == 0 && checkPlural(data, token) && !token.equals("is")) {
                    this.isPlural = true;
                    counter++;
                } else if (counter == 0 && token.toLowerCase().equals(data)) {
                    isUpperCase = true;
                    counter++;

                } else if (counter == 0 && checkPlural(data, token.toLowerCase()) && !token.equals("is")) {
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
            this.isPlural = false;
        }

    }


    private boolean checkPlural(String dictWord, String data) {

        if (data.equals(dictWord + "es") && (dictWord.endsWith("ch") || dictWord.endsWith("x") || dictWord.endsWith("s") || dictWord.endsWith("z"))) {
            return true;
        } else if (data.equals(dictWord + "s") && dictWord.endsWith("y") && isVowel(dictWord.substring(0, dictWord.length() - 1))) {
            return true;
        } else if (dictWord.endsWith("y") && data.equals(dictWord.substring(0, dictWord.length() - 1) + "ies") && !isVowel(dictWord.substring(0, dictWord.length() - 1))) {
            return true;
        } else if (data.equals(dictWord + "es") && dictWord.endsWith("o")) {
            return true;
        } else if (data.equals(dictWord + "s") && dictWord.endsWith("o")) {
            return true;
        } else if (data.equals(dictWord + "s") && !dictWord.endsWith("o") && !dictWord.endsWith("y") && !dictWord.endsWith("ch") && !dictWord.endsWith("x") && !dictWord.endsWith("s") && !dictWord.endsWith("z")) {
            return true;
        }

        return false;

    }

    private boolean isVowel(String input) {
        String vowels = "aeiou";
        int input_size = input.length();
        return vowels.contains(input.substring(input_size - 1, input_size));
    }

    private boolean getLev(String tokenToTranslate, String readData) { //Change this, close to finishing

        int distance = distance(tokenToTranslate, readData);
        return !dict.containsKey(tokenToTranslate) && distance == 1;

    }


    private int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();

        // i == 0
        int[] costs = new int[b.length() + 1];
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


    public static void main(String[] args) throws IOException {

        BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));

        for (String contents = fileReader.readLine(); contents != null; contents = fileReader.readLine()) {
            new translate(contents);
        }

    }
}
