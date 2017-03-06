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

    public void makePlural(String text, int position) {
        if (isVowel(text)) {
            this.name = text + "s";
        } else {
            text = text + "es";
            this.name = text;
        }
    }

    /**
     * Method that checks if the inputted String ends in a vowel
     * @param input String object that needs to be checked if it ends in a vowel
     * @return boolean value indicating if the inputted String ends in a vowel
     */
    private boolean isVowel(String input) {
        String vowels = "aeiou";
        int input_size = input.length();
        return vowels.contains(input.substring(input_size - 1, input_size));
    }
}