import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class translate {
    /* Array List of words to store each translated token */
    private ArrayList<word> translatedTokens = new ArrayList<>(); /* array list that stores the value of the translated tokens */
    /* Hash Map that maps an English word to the corresponding Spanish word */
    private HashMap<String, word> dict = new HashMap<>();
    /* String to store the English sentence that the user wishes to be translated */
    private String stringToTranslate;

    /**
     * Constructor that gets some English sentence from the user and passes this sentence to the stringToTranslate field,
     * then calls the getInput() function to proceed with the translation
     *
     * @param stringToTranslate stores the English representation of the sentence to translate
     * @throws IOException if the .csv file is not found
     */
    private translate(String stringToTranslate) throws IOException { /* constructor that calls the getInput function when called in order to get the msg to be translated */
        this.stringToTranslate = stringToTranslate;
        getInput();
    }

    public static void main(String[] args) throws IOException {
        // BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));
        //for (String contents = fileReader.readLine(); contents != null; contents = fileReader.readLine()) {
        //   new translate(contents);
        // }

        new translate("The time is difficult, the hours are hard but I am clear about my life");
    }

    /**
     * Method that reads the dictionary.csv file and maps the English words of the dictionary to their corresponding
     * Spanish translation, that is; the key is the English word and the value is the resultant Spanish word
     *
     * @throws IOException if the .csv file is not found
     */
    private void readDict() throws IOException {
        BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary.csv"), "UTF8"));
        String key = "";
        String value = "";

        for (String contents = fileRead.readLine(); contents != null; contents = fileRead.readLine()) { /* iterates through all rows in .csv file */
            int counter = 0; /* initialises counter to 0 since counter is inspected later */
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
        }
    }

    /**
     * Method that tokenises the given sentence and calls the required functions to translate the sentence,
     * i.e. readDict(), translate() and creates a rule instance to enforce the given grammar rules onto the
     * sentence; then prints the translated sentence to the console
     *
     * @throws IOException if the .csv file is not found
     */
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
    }

    /**
     * Method that prints the contents of the translatedTokens array list to the console
     */
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

    /**
     * Method that determines if the given String token should be translated, and if so; translates it
     *
     * @param token a token of the tokenised sentence of the user's inputted sentence
     * @throws IOException if the .csv file is not found
     */
    private void translate(String token) throws IOException { /* needs to throw such exceptions in order to read from given .csv file */
        String punc = ".,:;!?...";
        String translatedWord = "";

        if (punc.contains(token)) {
            word x = new word();
            x.setName(token);
            x.setType("punctuation");
            translatedTokens.add(x);
            return;
        }

        BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary.csv"), "UTF8")); /* BufferReader instance used to read the required .csv file */
         /* initialises counter to 0 since counter is inspected later */
        for (String contents = fileRead.readLine(); contents != null; contents = fileRead.readLine()) { /* iterates through all rows in .csv file */
            boolean isUpperCase = false;
            boolean isPlural = false;
            int counter = 0;
            for (String data : contents.split(",")) { /* iterates through all elements split around a comma (since .csv files seperate fields with commas */
                if (counter == 0 && token.equals(data)) {
                    counter++;
                } else if (counter == 0 && checkPlural(data, token) && !token.equals("is")) {
                    isPlural = true;
                    counter++;
                } else if (counter == 0 && token.toLowerCase().equals(data)) {
                    isUpperCase = true;
                    counter++;
                } else if (counter == 0 && checkPlural(data, token.toLowerCase()) && !token.equals("is")) {
                    isUpperCase = true;
                    isPlural = true;
                    counter++;
                } else if (counter == 0 && !token.equals(data) && !dict.containsKey(token) && !dict.containsKey(token.toLowerCase())
                        && getLev(token, data)) {
                    translatedWord = data;
                    counter += 2;
                    continue;
                } else if (counter == 1) {
                    translatedWord = data;
                    counter++;
                } else if (counter == 2) {
                    if (!isUpperCase && isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        x.setPlural(true);
                        translatedTokens.add(x);
                    } else if (!isUpperCase && !isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setPlural(false);
                        x.setType(data);
                        translatedTokens.add(x);
                    } else if (isUpperCase && isPlural) {
                        word x = new word();
                        x.setName(translatedWord);
                        x.setType(data);
                        x.setPlural(true);
                        x.isUCase(true);
                        translatedTokens.add(x);
                    } else if (isUpperCase && !isPlural) {
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
        }
    }

    /**
     * A method that checks with the given String data is the correct plural of the given String
     * dictWord
     * @param dictWord String representing the value of the word in the dictionary being inspected
     * @param data String representing the potential plural form of the String dictWord
     * @return a boolean value indicating whether or not data is the plural form of dictWord
     */
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

    /**
     * Method that determines if the given String ends with a vowel or a
     * consonant
     * @param input some String to be checked if it ends in a vowel or not
     * @return a boolean value indicating if the given String ends in a vowel
     */
    private boolean isVowel(String input) {
        String vowels = "aeiou";
        int input_size = input.length();
        return vowels.contains(input.substring(input_size - 1, input_size));
    }

    /**
     * Method that determines if the given String tokenToTranslate should be corrected to the
     * other given String readData
     *
     * @param tokenToTranslate String representing the token that has to be translated
     * @param readData         String representing the word that the String may be corrected to
     * @return a boolean value indicating if the string that is "misspelled" is already in the
     * dictionary and if Levenshtein distance between the two given strings is equal to 1; that is,
     * it only takes 1 change to tokenToTranslate to "change" it to readData
     */
    private boolean getLev(String tokenToTranslate, String readData) {
        int distance = distance(tokenToTranslate, readData);
        return !dict.containsKey(tokenToTranslate) && distance == 1;
    }

    /**
     * Method that calculates to Levenshtein distance between the given String a and b
     * @param a represents the String that is "misspelled"
     * @param b represents the String that the a could be corrected to
     * @return a value that indicates the number of changes required to turn a into b (changes means edits, i.e.
     * remove a character, inserting a character, etc.)
     */
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
}
