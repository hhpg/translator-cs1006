import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class translate {
    /* Array List of words to store each translated token */
    private ArrayList<word> translatedTokens = new ArrayList<>();
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
        String key = ""; /* String representing the key that will map the Spanish translation of a word to the Hash Map */
        String espWord = ""; /* String representing the translated Spanish word */

        /**
         * The following block of code includes two iterations; the outer loop iterates through
         * each line in the csv file, and the inner loop iterates through each word split
         * by a comma in each line; since the standard delimiter in a csv file is a
         * comma. To keep count of which word is being read, a counter is implemented;
         * where the counter has 3 possible values; 0, 1 and 2. The value 0 indicates
         * that the English word is being read, the value 1 indicates that the corresponding
         * Spanish word is being read and the value 2 indicates that the grammar type of the
         * word is being read.
         *
         * Hence, if the counter is 0, the current word being read is passed to the key String,
         * if the counter is 1, the current word being read is passed to the espWord String
         * and if the counter is 2, a new word object is created, calling the appropriate
         * setters (i.e. setName and setType) in order to set the actual word name and the
         * type of the word, then the dict Hash Map is used to map the key String to the
         * word object; then the inner loop finishes iterating through each word in a line
         * so the outer loop continues. The value of the counter is reset each time, since it
         * is declared within the outer loop.
         */

        for (String contents = fileRead.readLine(); contents != null; contents = fileRead.readLine()) { /* iterates through all rows in .csv file */
            int counter = 0; /* initialises counter to 0 since counter is inspected later */
            for (String data : contents.split(",")) { /* iterates through all elements split around a comma (since .csv files seperate fields with commas */
                if (counter == 0) {
                    key = data;
                    counter++;
                } else if (counter == 1) {
                    espWord = data;
                    counter++;
                } else if (counter == 2) {
                    word x = new word();
                    x.setName(espWord);
                    x.setType(data);
                    dict.put(key, x);
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

        /**
         * The following block of code consists of numerous if-else conditions in order to determine
         * whether or not the inputted String data is the plural form of the inputted String dictWord.
         * For the English languages, there are many rules in order to determine the plural form
         * of a given word (and also many exceptions to these rules), however, we will only be considering
         * the most "popular" plural forms of words; since the given dictionary only includes such words.
         *
         * To summarise, the rules for checking if a word is the plural form of another word (ignoring any exceptions)
         * are the following:
         *
         * ------------------------------------------------------------------------------------------------------------
         * If a noun ends in -ch, -x, -s, -z or s- like sounds then the plural form of such a noun is -es.
         * If a noun ends in a vowel + y, the plural form of such a noun is -s.
         * If a noun ends in a consonant + y, the plural form of such a noun is -ies.
         * If a noun ends in -o, the plural form of such a noun is -es.
         * Otherwise, if a noun does not end in the above listed sequence of characters, then the plural form is -s.
         * ------------------------------------------------------------------------------------------------------------
         *
         * Note that we have ignored exceptions to the rules listed above (i.e. piano --> pianos as opposed to
         * piano --> pianoes, however, none of the words that break these rules are includes in the given
         * dictionary so it is OK to simplify the pluralisation of the words.
         *
         * So, if the dictWord string ends in any of the character sequences listed above, and the data String is simply
         * the dictWord plus the corresponding plural form, then the data String is valid so the boolean value to be returned
         * will be true; otherwise, if none of the if-else conditions are met, the boolean value to be returned will be false.
         */

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
     * @param strToCheck some String to be checked if it ends in a vowel or not
     * @return a boolean value indicating if the given String ends in a vowel
     */
    private boolean isVowel(String strToCheck) {
        /**
         * The following block of code checks if the inputted String strToCheck
         * ends in a character that is a vowel, that is; the character is either
         * a, e, i, o or u. Firstly, a String object consisting of such a character
         * sequence must be declared; then one can simply check if this String object
         * contains the last character of strToCheck. We then get the length of
         * strToCheck and pass this to a declared int, i.e. input_size. We then
         * return a statement that looks messy, however, when you inspect if further
         * it makes more sense. We check if the String vowels contains the substring of
         * strToCheck, starting from (input_size - 1) to (input_size), since this substring
         * is simply the last character, and return this, since String.contains() is a boolean
         * value.
         */

        String vowels = "aeiou";
        int input_size = strToCheck.length();
        return vowels.contains(strToCheck.substring(input_size - 1, input_size));
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

        /**
         * The following block of code gets the Levenshtein distance
         * between the two Strings tokenToTranslate and readData,
         * that is; an integer value representing the number of changes
         * or edits one has to make to tokenToTranslate in order for its
         * value to be equivalent to readData, then a boolean value is
         * returned, which has a value of true if the dictionary does not
         * contain a key with a value equivalent to the String tokenToTranslate
         * AND if the value returned by the distance() method is equal to 1;
         * otherwise a value of false is returned. We check if the dictionary
         * does not contain a key with the same value as tokenToTranslate
         * to ensure that a valid word isn't considered as a misspelled one
         * and incorrectly corrected into some other word.
         */

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
