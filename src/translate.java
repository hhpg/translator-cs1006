import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class translate {
    /* Array List of words to store each translated token */
    private ArrayList<word> translatedTokens = new ArrayList<>();
    /* Hash Map that maps an English word to the corresponding Spanish word */
    private HashMap<String, word> dict = new HashMap<>();
    /* String to store the English sentence that the user wishes to be translated */
    private String stringToTranslate;
    /* String storing some regex to ensure that punctuation is considered as a token */
    private static final String TOKENIZE = " ?(?<!\\G)((?<=[^\\p{Punct}])(?=\\p{Punct})|\\b) ?";
    /* String storing a character sequence representing all of the vowels in the alphabet */
    private static final String VOWELS = "aeiou";

    /**
     * Constructor that gets some English sentence from the user and passes this sentence to the stringToTranslate field,
     * then calls the getInput() function to proceed with the translation
     *
     * @param stringToTranslate stores the English representation of the sentence to translate
     * @throws IOException if the .csv file is not found
     */
    private translate(String stringToTranslate) throws IOException {
        /**
         * If the inputted String stringToTranslate isn't empty then
         * the field stringToTranslate is set to stringToTranslate
         * and the getInput() function is called.
         */
        if (!stringToTranslate.isEmpty()) {
            this.stringToTranslate = stringToTranslate;
            doTranslation();
        }
    }

    public static void main(String[] args) throws IOException {
        /**
         * Keep the stuff below for testing
         */
        BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));
        for (String contents = fileReader.readLine(); contents != null; contents = fileReader.readLine()) {
            new translate(contents);
        }

        //System.out.println("ENGLISH TO SPANISH TRANSLATOR");
        //System.out.println("------------------------------");
        //System.out.println("Enter a sentence you would like to translate"); /* Default output msg to user */

        //Scanner sc = new Scanner(System.in); /* Scanner instance to get input */
        //String input = sc.nextLine(); /* Gets user input */
        //new translate(input);
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
         * that the English word is being read, the value 1 indicates that the correspondingd
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
    private void doTranslation() throws IOException {

        readDict(); /* Reads the csv file to the dict hash map */
        String[] tokens = stringToTranslate.split(TOKENIZE);  /* Splits each word by the TOKENIZE string */

        for (String token : tokens) { /* Iterates through all elements in tokens array and calls the translate */
            translate(token);          /* function to translate the elements to Spanish if possible */
        }
        new rule(translatedTokens); /* New rule object in order to enforce all grammar rules on translatedTokens */
        printTokens(); /* Prints the tokens to the console */
        System.out.print("\n"); /* New line for appropriate formatting */
    }

    /**
     * Method that prints the contents of the translatedTokens array list to the console
     */
    private void printTokens() {

        /**
         * The following block of code correctly formats the code so
         * it is printed to the console in a appropriate format. That is,
         * if the token being currently inspected is of type of punctuation
         * then there should not be white space following it, hence there is
         * a condition that checks if the token after the current token is
         * being inspected then the name of the current token should be printed
         * without white space in front of it; otherwise print the name of the current
         * token with white space after it. If the token to be printed is the
         * first token in the array list of translated tokens then it should
         * have its first character capitalised, regardless if the user
         * initially capitalised it, and if the token has its field value
         * isUCase equal to true, then it should be capitalised before
         * printing it.
         *
         * Also, if the token to be printed is the last token in the list then
         * it should not have white space following it; so there is a condition
         * to ensure this is enforced.
         */

        for (int i = 0; i < translatedTokens.size(); i++) { /* Iterates through all translated tokens */
            if (i == 0) {
                translatedTokens.get(i).makeUCase();
            } else if (i < translatedTokens.size() - 1 && translatedTokens.get(i + 1).getType().equals("punctuation")) {
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
     */
    private void translate(String token) {

        /**
         * The following block of code actually translates each token to its
         * corresponding Spanish word, if the token is valid. There are numerous
         * if-else conditions, determining if the inputted token is valid,
         * if it is a valid plural form of some word in the dictionary, if it
         * starts with an upper-case character, if it starts with an upper-case
         * character and is a valid plural form of some word in the dictionary, etc.
         *
         * Firstly, we have to consider if the token is simply some punctuation.
         * To check this, we declare a String with a character sequence representing
         * different kinds of punctuation, then check if this character sequences contains
         * the inputted token; if so, a word object is declared, with the token representing
         * the name and its grammar type as "punctuation". The function is then escaped.
         *
         * The following remaining else-if statements simply determine if the token is valid; the following
         * list of conditions is how a valid token is determined.
         * --------------------------------------------------------------------------------------
         * A token is valid the dictionary contains it
         * A token is valid if the dictionary contains a lower-case version of it
         * A token is valid if it is a *valid* plural form of a word in the dictionary
         * A token is valid if it is a *valid* upper-case, plural form of a word in the dictionary
         * A token is valid if it has a Levenshtein distance of 1 with some word in the dictionary
         * A token is valid if its lower-case form has a Levenshtein distance of 1 with some
         * word in the dictionary
         * --------------------------------------------------------------------------------------
         * If none of these conditions are met, then the token is not not valid; indicating that the user
         * has inputted some garbage.
         */

        String punc = ".,:;!?...";

        if (punc.contains(token)) {
            word x = new word();
            x.setName(token);
            x.setType("punctuation");
            translatedTokens.add(x);
            return;
        } else if (dict.containsKey(token)) {
            word x = new word();
            x.setName(dict.get(token).getName());
            x.setType(dict.get(token).getType());
            translatedTokens.add(x);
            return;
        } else if (dict.containsKey(token.toLowerCase())) {
            word x = new word();
            x.setName(dict.get(token.toLowerCase()).getName());
            x.setType(dict.get(token.toLowerCase()).getType());
            x.isUCase(true);
            translatedTokens.add(x);
            return;
        } else if (checkPlural(token)) {
            word x = new word();
            x.setName(getValue(token).getName());
            x.setType(getValue(token).getType());
            if (x.getType().equals("noun")) {
                x.setPlural(true);
                translatedTokens.add(x);
            }
            return;
        } else if (checkPlural(token.toLowerCase())) {
            word x = new word();
            x.setName(getValue(token.toLowerCase()).getName());
            x.setType(getValue(token.toLowerCase()).getType());
            x.isUCase(true);
            if (x.getType().equals("noun")) {
                x.setPlural(true);
                translatedTokens.add(x);
            }
            return;
        } else if (!getLev(token).isEmpty()) {
            String key = getLev(token);
            word x = new word();
            x.setName(dict.get(key).getName());
            x.setType(dict.get(key).getType());
            translatedTokens.add(x);
            return;
        } else if (!getLev(token.toLowerCase()).isEmpty()) {
            String key = getLev(token.toLowerCase());
            word x = new word();
            x.setName(dict.get(key).getName());
            x.setType(dict.get(key).getType());
            x.isUCase(true);
            translatedTokens.add(x);
            return;
        }
    }

    /**
     * A method that checks with the given String data is the correct plural of the given String
     * dictWord
     * @param pluralKey String representing the potential plural form of the String dictWord
     * @return a boolean value indicating whether or not data is the plural form of dictWord
     */
    private boolean checkPlural(String pluralKey) {

        /**
         * The following block of code iterates through all keys in the dictionary, then it implements
         * numerous if-else conditions in order to determine whether or not the inputted String potentialKey
         * is the plural form of some key in the hash map. For the English languages, there are many rules in
         * order to determine the plural form of a given word (and also many exceptions to these rules),
         * however, we will only be considering the most "popular" plural forms of words; since the given
         * dictionary only includes such words.
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
         * So, if the key string ends in any of the character sequences listed above, and the potentialKey String is simply
         * the key plus the corresponding plural form, then the data potentialKey is valid so the boolean value to be returned
         * will be true; otherwise, if none of the if-else conditions are met, the boolean value to be returned will be false.
         */

        for (String key : dict.keySet()) {
            if (pluralKey.equals(key + "es") && (key.endsWith("ch") || key.endsWith("x") || key.endsWith("s") || key.endsWith("z"))) {
                return true;
            } else if (pluralKey.equals(key + "s") && key.endsWith("y") && isVowel(key.substring(0, key.length() - 1))) {
                return true;
            } else if (key.endsWith("y") && pluralKey.equals(key.substring(0, key.length() - 1) + "ies") && !isVowel(key.substring(0, key.length() - 1))) {
                return true;
            } else if (pluralKey.equals(key + "es") && key.endsWith("o")) {
                return true;
            } else if (pluralKey.equals(key + "s") && key.endsWith("o")) {
                return true;
            } else if (pluralKey.equals(key + "s") && !key.endsWith("o") && !key.endsWith("y") && !key.endsWith("ch") && !key.endsWith("x") && !key.endsWith("s") && !key.endsWith("z")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that returns the un-pluralised form of the given pluralised word
     *
     * @param pluralKey String object representing the pluralised form of the key to find
     * @return
     */
    private word getValue(String pluralKey) {

        /**
         * The following block of code is similar to the checkPlural() method as it
         * iterates through all keys in the dict hash map, however, rather than returning
         * a boolean value indicating if the inputted String pluralKey is a valid pluralised
         * form of a word in the hash map, it returns the word that the String pluralKey is
         * the plural of.
         */
        for (String key : dict.keySet()) {
            if (pluralKey.equals(key + "es") && (key.endsWith("ch") || key.endsWith("x") || key.endsWith("s") || key.endsWith("z"))) {
                return dict.get(key);
            } else if (pluralKey.equals(key + "s") && key.endsWith("y") && isVowel(key.substring(0, key.length() - 1))) {
                return dict.get(key);
            } else if (key.endsWith("y") && pluralKey.equals(key.substring(0, key.length() - 1) + "ies") && !isVowel(key.substring(0, key.length() - 1))) {
                return dict.get(key);
            } else if (pluralKey.equals(key + "es") && key.endsWith("o")) {
                return dict.get(key);
            } else if (pluralKey.equals(key + "s") && key.endsWith("o")) {
                return dict.get(key);
            } else if (pluralKey.equals(key + "s") && !key.endsWith("o") && !key.endsWith("y") && !key.endsWith("ch") && !key.endsWith("x") && !key.endsWith("s") && !key.endsWith("z")) {
                return dict.get(key);
            }
        }
        return null;
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
         * it makes more sense. We check if the String VOWELS contains the substring of
         * strToCheck, starting from (input_size - 1) to (input_size), since this substring
         * is simply the last character, and return this, since String.contains() is a boolean
         * value.
         */

        int input_size = strToCheck.length();
        return VOWELS.contains(strToCheck.substring(input_size - 1, input_size));
    }

    /**
     * Method that determines if the given String tokenToTranslate should be corrected  to some
     * key in the dict hash map
     *
     * @param tokenToTranslate String representing the token that has to be translated
     * @return a String value representing the key that has a Levenshtein distance
     * with tokenToTranslate equal to 1; if such a key doesn't exist the method
     * returns an empty string
     */
    private String getLev(String tokenToTranslate) {

        /**
         * The following block of code iterates through each key in the
         * dictionary hash map, gets the Levenshtein ditance between
         * tokenToTranslate and each key; that is, an integer value
         * representing the number of changes or edits one has to make
         * to tokenToTranslate in order for its value to be equivalent
         * to the value of the key. If the dictionary does not already
         * contain a key equivalent to tokenToTranslate AND the Levenshtein
         * distance returned by the distance() method is equal to 1,
         * then the method returns a boolean value of true,
         * otherwise; it returns a boolean value of false.
         */
        for (String key : dict.keySet()) {
            int distance = distance(tokenToTranslate, key);
            if (!dict.containsKey(tokenToTranslate) && distance == 1) {
                return key;
            }
        }
        return "";
    }

    /**
     * Method that calculates to Levenshtein distance between the given String a and b
     * @param a represents the String that is "misspelled"
     * @param b represents the String that the a could be corrected to
     * @return a value that indicates the number of changes required to turn a into b (changes means edits, i.e.
     * remove a character, inserting a character, etc.)
     */
    private int distance(String a, String b) {
        /**
         * The following block of code determines the number of edits it
         * would talk to turn a --> b. This is a well known algorithm.
         */
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
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