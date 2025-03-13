import java.util.*;

public class LangDictionaryLZW {
    public HashMap<String, Integer> Dictionary;
    public HashMap<Character, TreeSet<String>> WordsByFirstLetter;
    private Integer counter;
    public int maximumLength;
    private final String[] eng = {"ed ", "er ", " the ", " a ", " an ", " as ", "ing ", " with ", "th", " if ",
            " and ", " are ", "'re ", " am ", " by ", "he", "in", "er", "an", "re", "on", "at", "en", "nd", "ti",
            "es", "or", "te", "of", "ed", " is ", "it", "al", "ar", "st", "to", "nt", "oo", " what ", "n't ", "'s",
            "ion ", "ful ", "less ", " which ", " for ", " at ", " no ", " to ", " be", " not ", " from ", "ence ",
            "sh", "ch", "ly ", "ist ", "ll", "ous ", " re", " un", " de", " he ", " she ", " I ", " can ", " ha",
            " pre", " so "};
    private final String[] rus = {};
    private final String[] est = {"sse", "st", "le", "lt", "ks", "ni ", "na ", "ta", "ga ", "te ", "de ", "id ",
            "im ", "em ", "ma", "da", "ev ", "nud ", "tud ", "dud ", "takse ", "dakse ", "akse ", "me ",
            "vad ", "ti ", "di ", "si", " ol", " ei ", " ja ", "vat ", " är", "ku ", "ne", "se", "pp",
            "aa", "ee", "uu", "üü", "ää", "öö", "ii", "oo", "nn", "mm", "ll", " see ", "kk", ", et ",
            "pp", "sid ", "seid ", "ks", "ss", "ea", "au", "ki ", "gi ", " nii ", " kas ", " kes ", " kelle ",
            " mille ", " keda ", " mida ", " nagu ", " ka ", " on ", " see ", " selle ", " seda "};

    public LangDictionaryLZW() {
        Dictionary = new HashMap<>();
        WordsByFirstLetter = new HashMap<>();
        counter = Integer.MIN_VALUE + 1;
    }

    public void addTextSymbols(HashSet<Character> symbols) {
        for (Character textSymbol : symbols) {
            this.addWord(String.valueOf(textSymbol));
        }
    }
    public void setLang(String lang) {
        switch (lang) {
            case "est":
                for (String s : est) {
                    this.addWord(s);
                }
                break;
            case "eng":
                for (String s : eng) {
                    this.addWord(s);
                }
                break;
            case "rus":
                for (String s : rus) {
                    this.addWord(s);
                }
                break;
        }
    }
    public void addWord(String word) {
        Dictionary.put(word, counter);
        counter++;
        if (word.length() > 1) {
            if (WordsByFirstLetter.containsKey(word.charAt(0))) {
                WordsByFirstLetter.get(word.charAt(0)).add(word);
            } else WordsByFirstLetter.put(word.charAt(0), new TreeSet<>(Comparator.reverseOrder()){{add(word);}});
            if (maximumLength < word.length()) maximumLength = word.length();
        }
    }
    public Integer getCode(String word) {
        return Dictionary.get(word);
    }
    public String getWord(Integer b) {
        for (String s : Dictionary.keySet()) {
            if (Objects.equals(b, Dictionary.get(s))) return s;
        }
        return null;
    }
    public TreeSet<String> getFirstLetterList(Character firstLetter) {
        return WordsByFirstLetter.get(firstLetter);
    }
}
