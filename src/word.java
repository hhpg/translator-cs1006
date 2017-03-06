/* Class that represents a translated word */
class word {
    /**
     * Stores the value of the  word
     */
    private String name;
    /**
     * Stores the grammar type of the word; i.e. puncuation, adjective, noun, etc.
     */
    private String type;
    /**
     * Stores a boolean value indicating whether or not the translated word is plural or singular
     */
    private boolean isPlural = false;
    private String punctuation;
    /**
     * Stores a boolean value indicating whether ot not the translated word should be capitalised or not
     */
    private boolean isUCase = false;

    /**
     * Method that sets the boolean field isPlural to the given boolean value
     *
     * @param isPlural boolean value indicating if the word is plural or not
     */
    public void setPlural(boolean isPlural) {
        this.isPlural = isPlural;
    }

    /**
     * Method that returns the boolean field isPlural
     *
     * @return boolean value indicating if the word is plural or not
     */
    public boolean getIsPlural() {
        return this.isPlural;
    }

    /**
     * Method that sets the String field name to the given String object name
     * @param name String object representing the name of the word
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method that sets the String field type to the given String object type
     * @param type String object representing the grammar type of the word
     */
    public void setType(String type) {
        this.type = type;
    }

    public void setPunctuation(String punctuation) {
        this.punctuation = punctuation;
    } /* TBC */

    /**
     * Method that returns the name of the word
     * @return a String object representing the name of the word
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method that returns the type of the word
     * @return a String object representing the type of the word
     */
    public String getType() {
        return this.type;
    }

    /**
     * Method that sets the isUCase boolean field to the given boolean value x
     * @param x boolean value indicating if the word starts with an upper-case character
     */
    public void isUCase(boolean x) {
        this.isUCase = x;

    }

    /**
     * Method that makes the first character of the word upper-case
     */
    public void makeUCase() {
        this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Method that returns the boolean value isUCase
     * @return boolean value indicating if the word starts with an upper-case character
     */
    public boolean getIsUCase() {
        return this.isUCase;
    }

    public String getPunctuation() {
        return this.punctuation;
    }

    public void makePlural(String wordName, int position) {

        /**
         * The following block of code checks if the inputted String
         * wordName ends in a vowel or a consonant. If it ends in a
         * vowel then the plural form of this word is simply the wordName
         * + 's', otherwise; the plural form is the wordName + "es".
         */
        if (isVowel(wordName)) {
            this.name = wordName + "s";
        } else {
            wordName = wordName + "es";
            this.name = wordName;
        }
    }

    /**
     * Method that checks if the inputted String ends in a vowel
     * @param input String object that needs to be checked if it ends in a vowel
     * @return boolean value indicating if the inputted String ends in a vowel
     */
    private boolean isVowel(String input) {

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
        int input_size = input.length();
        return vowels.contains(input.substring(input_size - 1, input_size));
    }
}