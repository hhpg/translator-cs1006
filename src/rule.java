import java.util.ArrayList;

class rule {
    /**
     * Array list that stores each translated token
     */
    private ArrayList<word> translatedTokens = new ArrayList<>();
    /**
     * boolean value that indicates whether the noun is masculine or not
     */
    private boolean isMasc = true;

    /**
     * Constructor that sets the Array List translatedTokens field to the given Array List and calls the grammarRules() method
     * to enforce all grammar rules
     *
     * @param tokens Array List of the tokens to translate
     */
    rule(ArrayList<word> tokens) {
        this.translatedTokens = tokens;
        grammarRules();
    }

    /**
     * Method that checks what the grammar type of the English word "her" is, i.e. it can be a pronoun or a possessive, by checking the type of the
     * word that is in front of it; if the word in front of it is a noun or an adjective then the word type will be possessive rather than a pronoun
     * and the translated name of the word will be "su" as opposed to "ella", so this method enforces this grammar rule; let this rule be called rule 0
     */
    private void checkTypeOfHer() {
        for (int i = 0; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getName().equals("ella") && (translatedTokens.get(i + 1).getType().equals("noun") || translatedTokens.get(i + 1).getType().equals("adjective"))) {
                translatedTokens.get(i).setName("su");
                translatedTokens.get(i).setType("possessive");
            }
        }
    }

    /**
     * Method that enforces grammar rule 1, that is; ensuring that the article and adjective(if ending in 'o')
     * agrees with the noun in gender
     */
    private void setGenders() {
        String adj, noun;

        for (int i = 0; i < translatedTokens.size(); i++) {
            if (translatedTokens.get(i).getType().equals("noun")) {
                noun = translatedTokens.get(i).getName();
                if (noun.endsWith("a") || noun.endsWith("d") || noun.endsWith("z") || noun.endsWith("ión")) {
                    isMasc = false;
                    if (i > 0) {
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

            if (!isMasc) { /* feminine;  Rule 1 */
                if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getName().endsWith("o") && (translatedTokens.get(i - 1).getType().equals("noun") ||
                        translatedTokens.get(i - 2).getType().equals("noun") || translatedTokens.get(i - 1).getType().equals("conjunction"))) {
                    adj = translatedTokens.get(i).getName();
                    int size = adj.length();
                    adj = adj.substring(0, size - 1) + "a";
                    translatedTokens.get(i).setName(adj);
                }

            } else if (isMasc) { /* masculine */ /* rule 1 */

                if (i > 1 && translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getName().endsWith("o") && (translatedTokens.get(i - 1).getType().equals("noun") ||
                        translatedTokens.get(i - 2).getType().equals("noun") || translatedTokens.get(i - 1).getName().equals("y"))) {
                    adj = translatedTokens.get(i).getName();
                    int size = adj.length();
                    adj = adj.substring(0, size - 1) + "o";
                    translatedTokens.get(i).setName(adj);

                }

            }

        }

    }

    /**
     * Method that enforces grammar rule 3, that is; the adjectives should always be followed by the noun
     */
    private void swapNounAndAdj() {

        int article_pos = -1;

        for (int i = 0; i < translatedTokens.size(); i++) { /* Checks for unordered adjective/noun stuff */
            if (translatedTokens.get(i).getType().equals("article")) {
                article_pos = i;
            } else if (translatedTokens.get(i).getType().equals("noun") && article_pos >= 0 && (translatedTokens.get(i - 1).getType().equals("adjective") && translatedTokens.get(i - 2).getName().equals("y"))) {
                word x = translatedTokens.get(i);
                translatedTokens.add(article_pos + 1, x);
                translatedTokens.remove(i + 1);
            }
        }
        /* if there isn't an article present in the list then the following for loop is executed in */
        for (int i = 1; i < translatedTokens.size(); i++) { /* Checks for unordered adjective/noun stuff */
            if (translatedTokens.get(i).getType().equals("noun") && translatedTokens.get(i - 1).getType().equals("adjective")) {
                word x = translatedTokens.get(i);
                word y = translatedTokens.get(i - 1);
                translatedTokens.set(i - 1, x);
                translatedTokens.set(i, y);
            }
        }
    }

    /**
     * Method that sets the isPlural field of the word objects to true if the nouns are plural
     */
    private void setPlurals() {

        for (int i = 0; i < translatedTokens.size(); i++) {
            if (i > 0 && translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i - 1).getType().equals("noun") && translatedTokens.get(i - 1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            } else if (i > 1 && translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i - 2).getType().equals("noun") && translatedTokens.get(i - 2).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            } else if (i < translatedTokens.size() - 1 && translatedTokens.get(i).getType().equals("article") && isMasc && translatedTokens.get(i + 1).getType().equals("noun") && translatedTokens.get(i + 1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            } else if (i < translatedTokens.size() - 1 && translatedTokens.get(i).getType().equals("article") && !isMasc && translatedTokens.get(i + 1).getType().equals("noun") && translatedTokens.get(i + 1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            } else if (i < translatedTokens.size() - 1 && translatedTokens.get(i).getType().equals("possessive") && translatedTokens.get(i + 1).getIsPlural()) {
                translatedTokens.get(i).setPlural(true);
            }
        }
    }

    /**
     * Method that calls the setPlurals() methods to set the isPlural fields of each word object, then
     * decides what to do based on the value of the isPlural field (i.e. if the isPlural field has a value of true
     * then make the word being inspected plural if it an adjective or article) (rule 4)
     */
    private void ensurePlurality() {

        setPlurals();
        String noun, adj, article, possessive;
        /* Rule 2 and 4 */
        for (int i = 0; i < translatedTokens.size(); i++)
            if (translatedTokens.get(i).getType().equals("adjective") && translatedTokens.get(i).getIsPlural()) {
                adj = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(adj, i);
            } else if (translatedTokens.get(i).getType().equals("article") && isMasc && translatedTokens.get(i).getIsPlural()) {
                article = "los";
                translatedTokens.get(i).setName(article);
            } else if (translatedTokens.get(i).getType().equals("article") && !isMasc && translatedTokens.get(i).getIsPlural()) {
                article = "las";
                translatedTokens.get(i).setName(article);
            } else if (translatedTokens.get(i).getType().equals("noun") && translatedTokens.get(i).getIsPlural()) {
                noun = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(noun, i);
            } else if (translatedTokens.get(i).getType().equals("possessive") && translatedTokens.get(i).getIsPlural()) {
                possessive = translatedTokens.get(i).getName();
                translatedTokens.get(i).makePlural(possessive, i);
            }
    }

    /**
     * Spanish differentiates "are" based on the person, so this method determines what "are" should be replaced with (rule 5)
     */
    private void replaceAre() {
        for (int i = 0; i < translatedTokens.size() - 1; i++) { /* rule 5 */
            if (translatedTokens.get(i).getName().equals("tú") && translatedTokens.get(i + 1).getName().equals("son")) {
                translatedTokens.get(i + 1).setName("eres");
            } else if (translatedTokens.get(i).getName().equals("nosotros") && translatedTokens.get(i + 1).getName().equals("son")) {
                translatedTokens.get(i + 1).setName("somos");
            }
        }
    }

    /**
     * soy/eres/somos/es/son becomes “est**” based on the noun/pronoun when followed by a
     * verb or “with”, hence this method ensures that this rule is enforced (rule 6)
     */
    private void changeToEst() {
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

    /**
     * Spanish usually drops pronouns with es/est*** forms, hence this method enforces this rule (rule 7)
     */
    private void dropPronouns() {
        ArrayList<String> pronounList = new ArrayList<>();
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

    /**
     * Method that swaps a token with the token that is in front of it (in front of being the position of the token + 1)
     *
     * @param name     the name of token that needs to be swapped
     * @param type     the type of the token that needs to be swapped
     * @param position the position of the token within the translatedTokens array list
     */
    private void swapTokens(String name, String type, int position) {
        translatedTokens.get(position).setName(translatedTokens.get(position - 1).getName());
        translatedTokens.get(position).setType(translatedTokens.get(position - 1).getType());
        translatedTokens.get(position - 1).setName(translatedTokens.get(position - 2).getName());
        translatedTokens.get(position - 1).setType(translatedTokens.get(position - 2).getType());
        translatedTokens.get(position - 2).setName(name);
        translatedTokens.get(position - 2).setType(type);
    }

    /**
     * Method that checks if some pronoun is followed by some verb, and if so, enforces rule 8
     */
    private void pronounFollowedByVerb() {
        for (int i = 2; i < translatedTokens.size(); i++) {
            /* you are watching me */
            if (translatedTokens.get(i).getName().equals("yo") && translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("me", "pronoun", i);
            }
            /* they are watching you */
            else if (translatedTokens.get(i).getName().equals("tú") && translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("te", "pronoun", i);
            }
            /* i am watching him */
            else if (translatedTokens.get(i).getName().equals("él") && translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("lo", "pronoun", i);
            } else if (translatedTokens.get(i).getName().equals("ella") && translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("la", "pronoun", i);
            } else if (translatedTokens.get(i).getName().equals("ellos") && translatedTokens.get(i - 1).getType().equals("verb")) {
                swapTokens("los", "pronoun", i);
            }
        }
    }

    /**
     * Method that checks if the sentence contains some negation, and if so, enforces rule 9
     */
    private void negation() {
        for (int i = 1; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getName().equals("no")) {
                word neg, verb;
                neg = translatedTokens.get(i);
                verb = translatedTokens.get(i - 1);
                translatedTokens.set(i - 1, neg);
                translatedTokens.set(i, verb);
                // if(translatedTokens.get(i+1).getType().equals("punctuation")) {
                //   translatedTokens.remove(i+1);
                // }
            }
        }
    }

    /**
     * In Spanish, some replacements are usaully done, i.e. with me --> con mi --> conmigo, with you --> con tú --> contigo,
     * so this method ensures this rule is followed (rule 10).
     */
    private void replacements() {
        for (int i = 0; i < translatedTokens.size() - 1; i++) {
            if (translatedTokens.get(i).getName().equals("con") && translatedTokens.get(i + 1).getName().equals("tú")) {
                translatedTokens.get(i).setName("contigo");
                translatedTokens.get(i).setType("pronoun");
                translatedTokens.remove(i + 1);
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
                translatedTokens.remove(i + 1);
                //if(translatedTokens.get(i).getType().equals("punctuation")) {
                //  translatedTokens.remove(i);
                // }
            }
        }
    }

    /**
     * Method that calls all of the above grammar rule methods to enforce all
     * rules onto the translatedTokens Array List
     */
    private void grammarRules() {
        checkTypeOfHer();
        swapNounAndAdj();
        setGenders();
        ensurePlurality();
        replaceAre();
        dropPronouns();
        negation();
        changeToEst();
        pronounFollowedByVerb();
        replacements();
    }
}