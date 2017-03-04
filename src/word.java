/* Class that represents a translated word */
class word {
    private String name; /* stores the value of the translated word */
    private String type; /* stores the type of the translated word; i.e. punctuation, adjective, noun, etc. */
    private boolean isPlural = false; /* stores a boolean value indicating whether or not the translated word is plural or singular */
    private String punctuation;
    private boolean isUCase = false; /* stores a boolean value indicating whether ot not the translated word should be capitalised or not */

    public void setPlural(boolean isPlural) {
        this.isPlural = isPlural;
    }


    public boolean getIsPlural() {
        return this.isPlural;
    }
    /*
     * Sets the name of the translated word
     */
    public void setName(String name) {
        this.name = name;
    }
    /*
     * Sets the type of the translated word
     */
    public void setType(String type) {
        this.type = type;
    }

    public void setPunctuation(String punctuation) {
        this.punctuation = punctuation;
    } /* TBC */
    /*
     * Gets the name of the translated word
     */
    public String getName() {
        return this.name;
    }
    /*
     * Gets the type of the translated word
     */
    public String getType() {
        return this.type;
    }
    /*
     * Sets the isUCase value to the inputted value
     */
    public void isUCase(boolean x) {
        this.isUCase = x;

    }
    public void makeUCase() {
        this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    /*
     * Gets the value indicating whether or not the translated word's name should be capitalised
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
    /*
     * Function used to determine if the name of the translated word
     * ends in a vowel or not
     */
    private boolean isVowel(String input) {
        String vowels = "aeiou";
        int input_size = input.length();
        return vowels.contains(input.substring(input_size - 1, input_size));
    }
}